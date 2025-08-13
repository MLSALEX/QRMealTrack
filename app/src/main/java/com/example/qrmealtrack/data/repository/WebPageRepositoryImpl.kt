package com.example.qrmealtrack.data.repository

import com.example.qrmealtrack.domain.repository.WebPageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebPageRepositoryImpl @Inject constructor(
    private val client: OkHttpClient
) : WebPageRepository {

    override suspend fun fetchPageInfo(url: String): String = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error("HTTP ${response.code}")
                val html = response.body?.string().orEmpty()
                val document = Jsoup.parse(html)

                val title = document.title().orEmpty()
                val metaDescription = document.selectFirst("meta[name=description]")?.attr("content").orEmpty()
                val h1 = document.selectFirst("h1")?.text().orEmpty()

                "Title: $title\nDescription: $metaDescription\nH1: $h1"
            }
        }.getOrElse { e -> "Error: ${e.localizedMessage ?: "Unknown error"}" }
    }
}