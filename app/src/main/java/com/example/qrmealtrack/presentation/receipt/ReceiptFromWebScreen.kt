package com.example.qrmealtrack.presentation.receipt

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.qrmealtrack.presentation.utils.parseTextToReceipts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ReceiptFromWebScreen(
    url: String,
    viewModel: ReceiptListViewModel = hiltViewModel(),
    onDone: () -> Unit = {}
) {
    val context = LocalContext.current
    var extractedText by remember { mutableStateOf<String?>(null) }
    val webView = remember { WebView(context) }

    LaunchedEffect(Unit) {
        viewModel.message.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "📄 Виртуальный чек",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        AndroidView(
            factory = {
                webView.apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            // ✅ Извлекаем всё содержимое <body> как текст
                            evaluateJavascript(
                                "(function() { return document.body.innerText; })();"
                            ) { value ->
                                val cleaned = value
                                    .removeSurrounding("\"")
                                    .replace("\\n", "\n")
                                    .replace("\\\"", "\"")

                                extractedText = cleaned

                                CoroutineScope(Dispatchers.Default).launch {
                                    val parsed = parseTextToReceipts(cleaned)

                                    withContext(Dispatchers.Main) {
                                        if (parsed != null) {
                                            viewModel.onAction(ReceiptUiAction.SaveParsed(parsed))
                                            Toast.makeText(context, "Сохранено ${parsed.items.size} блюд, всего: ${parsed.total} MDL", Toast.LENGTH_SHORT).show()
                                            onDone()
                                        } else {
                                            Toast.makeText(context, "Не удалось распознать чек", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Divider()

        Text(
            text = "🧾 Распознанный текст:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier
            .weight(1f)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())) {
            Text(
                text = extractedText ?: "Загрузка...",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}