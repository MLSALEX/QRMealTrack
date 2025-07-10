package com.example.qrmealtrack.presentation.home

import com.example.qrmealtrack.presentation.model.ReceiptUiModel
import java.util.Collections.emptyMap

data class HomeUiState(
    val receiptsByDay: Map<String, List<ReceiptUiModel>> = emptyMap()
)