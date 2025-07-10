package com.example.qrmealtrack.presentation.home

import com.example.qrmealtrack.domain.model.Receipt
import java.util.Collections.emptyMap

data class HomeUiState(
    val receiptsByDay: Map<String, List<Receipt>> = emptyMap()
)