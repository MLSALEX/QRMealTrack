package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.repository.WebPageRepository

class FetchWebPageInfoUseCase(
    private val repository: WebPageRepository
) {
    suspend operator fun invoke(url: String): String {
        return repository.fetchPageInfo(url)
    }
}