package com.example.qrmealtrack.presentation.scan

import android.util.Patterns
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.qrmealtrack.R
import com.example.qrmealtrack.data.mapper.parseQrToReceipt
import com.example.qrmealtrack.presentation.components.TopBar
import com.example.qrmealtrack.presentation.receipt.ReceiptListViewModel
import com.example.qrmealtrack.presentation.receipt.ReceiptUiAction
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.net.URLEncoder

@Composable
fun ScanScreen(
    navController: NavHostController,
    parentNavController: NavHostController,
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

                        if (Patterns.WEB_URL.matcher(rawValue).matches()) {
                            viewModel.onAction(ReceiptUiAction.FetchWebPageInfo(rawValue))
                        }

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

    SciFiQrScreen()

    scannedText?.let { text ->
        if (Patterns.WEB_URL.matcher(text).matches()) {
            val annotatedLink = buildAnnotatedString {
                val tag = "URL"
                val start = 0
                val end = text.length

                append(text)
                addStyle(
                    style = SpanStyle(
                        color = Color(0xFF00CFFF),
                        textDecoration = TextDecoration.Underline
                    ),
                    start = start,
                    end = end
                )
                addStringAnnotation(
                    tag = tag,
                    annotation = text,
                    start = start,
                    end = end
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = annotatedLink,
                    style = TextStyle(
                        color = Color(0xFF00CFFF),
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        val url = annotatedLink
                            .getStringAnnotations("URL", 0, annotatedLink.length)
                            .firstOrNull()?.item

                        val encodedUrl = try {
                            URLEncoder.encode(url, "UTF-8")
                        } catch (e: Exception) {
                            null
                        }
                        if (!encodedUrl.isNullOrBlank()) {
                            parentNavController.navigate("receipt_from_web/$encodedUrl")
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
