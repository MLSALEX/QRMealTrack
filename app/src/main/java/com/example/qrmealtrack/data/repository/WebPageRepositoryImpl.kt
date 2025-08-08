package com.example.qrmealtrack.data.repository

import com.example.qrmealtrack.domain.repository.WebPageRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import javax.inject.Inject

class WebPageRepositoryImpl @Inject constructor(): WebPageRepository {
    override suspend fun fetchPageInfo(url: String): String {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val html = response.body?.string()
            val document = Jsoup.parse(html)

            val title = document.title()
            val metaDescription = document.select("meta[name=description]").attr("content")
            val h1 = document.select("h1").first()?.text()

            "Title: $title\nDescription: $metaDescription\nH1: $h1"
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}