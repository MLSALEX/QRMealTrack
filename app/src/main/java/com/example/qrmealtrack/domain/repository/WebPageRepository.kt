package com.example.qrmealtrack.domain.repository

interface WebPageRepository {
    suspend fun fetchPageInfo(url: String): String
}