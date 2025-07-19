package com.example.qrmealtrack.domain.repository

import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.model.ReceiptCategory
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun getAllReceipts():Flow<List<Receipt>>
    suspend fun insertReceipt(receipt: Receipt): Long
    suspend fun countReceipts(fiscalCode: String, dateTime: Long): Int
    suspend fun getReceiptsByFiscalCodeAndDate(fiscalCode: String, dateTime: Long): List<Receipt>
    suspend fun deleteReceiptGroup(fiscalCode: String, dateTime: Long)
    suspend fun updateReceiptCategory(receiptId: Long, category: ReceiptCategory)
}