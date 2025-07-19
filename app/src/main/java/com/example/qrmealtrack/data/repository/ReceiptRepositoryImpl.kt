package com.example.qrmealtrack.data.repository

import android.util.Log
import com.example.qrmealtrack.data.local.ReceiptDao
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.data.mapper.toDomain
import com.example.qrmealtrack.data.mapper.toEntity
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.model.ReceiptCategory
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao
) : ReceiptRepository {
    override fun getAllReceipts(): Flow<List<Receipt>> {
        return dao.getAllReceiptsWithItems()
            .map { list -> list.map { it.toDomain()} }
    }

    override suspend fun insertReceipt(receipt: Receipt): Long {
        val receiptId = dao.insertReceipt(receipt.toEntity())
        val items = receipt.items.map { it.toEntity(receiptId) }
        dao.insertItems(items)
        return receiptId
    }

    override suspend fun countReceipts(fiscalCode: String, dateTime: Long): Int {
        return dao.countReceiptsForCheck(fiscalCode, dateTime)
    }

    override suspend fun getReceiptsByFiscalCodeAndDate(
        fiscalCode: String,
        dateTime: Long
    ): List<Receipt> {
        return dao.getReceiptGroup(fiscalCode, dateTime)
            .map { it.toDomain() }
    }

    override suspend fun deleteReceiptGroup(fiscalCode: String, dateTime: Long) {
        dao.deleteReceiptGroup(fiscalCode, dateTime)
    }

    override suspend fun updateReceiptCategory(receiptId: Long, category: ReceiptCategory) {
        dao.updateReceiptCategory(receiptId, category.key)
    }
}