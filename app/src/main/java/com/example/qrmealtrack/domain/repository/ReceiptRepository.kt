package com.example.qrmealtrack.domain.repository

import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun getAllReceipts():Flow<List<Receipt>>
    suspend fun insertReceipt(receipt: ReceiptEntity): Long
    suspend fun countReceipts(fiscalCode: String, dateTime: Long): Int
    suspend fun getReceiptsByFiscalCodeAndDate(fiscalCode: String, dateTime: Long): List<ReceiptEntity>
    suspend fun deleteReceiptGroup(fiscalCode: String, dateTime: Long)
}