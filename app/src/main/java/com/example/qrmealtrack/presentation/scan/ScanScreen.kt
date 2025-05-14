package com.example.qrmealtrack.presentation.scan

import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.presentation.ReceiptListViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.qrmealtrack.R
import com.example.qrmealtrack.presentation.components.TopBar
import kotlinx.coroutines.flow.collectLatest
import java.net.URLEncoder

@Composable
fun ScanScreen(
    navController: NavHostController,
    viewModel: ReceiptListViewModel = hiltViewModel(),
    onScanned: () -> Unit
) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var scannedText by remember { mutableStateOf<String?>(null) }

    TopBar(titleRes = R.string.scan)

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val barcodeScanner = BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                )
                val analysisUseCase = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                    processImageProxy(barcodeScanner, imageProxy) { rawValue ->
                        scannedText = rawValue

                        // ✅ Если это ссылка — получаем информацию о странице
                        if (Patterns.WEB_URL.matcher(rawValue).matches()) {
                            viewModel.fetchWebPageInfo(rawValue)
                        }

                        // ✅ Если это Receipt — сохраняем
                        val receipt = parseQrToReceipt(rawValue)
                        if (receipt != null) {
                            viewModel.addReceipt(receipt)
                            onScanned()
                        }
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, analysisUseCase
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
    // ✅ Показываем ссылку и извлечённую информацию
    scannedText?.let { text ->
        if (Patterns.WEB_URL.matcher(text).matches()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                ClickableText(
                    text = AnnotatedString(text),
                    style = TextStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    ),
                    onClick = {
                        val encodedUrl = try {
                            URLEncoder.encode(text, "UTF-8")
                        } catch (e: Exception) {
                            null
                        }

                        if (!encodedUrl.isNullOrBlank()) {
                            navController.navigate("receipt_from_web/$encodedUrl")
                        } else {
                            Toast.makeText(
                                context,
                                "Ошибка при переходе по ссылке",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }
}

fun parseQrToReceipt(rawValue: String): ReceiptEntity? {
    return try {
        val parts = rawValue.split(";")
        ReceiptEntity(
            fiscalCode = parts[0],
            enterprise = parts[1],
            itemName = parts[2],
            weight = parts[3].toDouble(),
            price = parts[4].toDouble(),
            dateTime = System.currentTimeMillis(),
            type = parts[5]
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
