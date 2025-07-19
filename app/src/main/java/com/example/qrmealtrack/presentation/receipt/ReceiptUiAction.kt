package com.example.qrmealtrack.presentation.receipt

import com.example.qrmealtrack.presentation.components.CategoryUi
import com.example.qrmealtrack.presentation.model.ReceiptUiModel
import com.example.qrmealtrack.presentation.utils.ParsedReceipt

sealed interface ReceiptUiAction {
    data class ToggleReceipt(val id: Long) : ReceiptUiAction
    data class DeleteReceipt(val receipt: ReceiptUiModel) : ReceiptUiAction
    data class SaveParsed(val parsed: ParsedReceipt) : ReceiptUiAction
    data class FetchWebPageInfo(val url: String) : ReceiptUiAction

    data class ChangeCategory(
        val receiptId: Long,
        val newCategory: CategoryUi
    ) : ReceiptUiAction
}