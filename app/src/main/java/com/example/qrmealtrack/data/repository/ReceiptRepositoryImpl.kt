package com.example.qrmealtrack.data.repository

import com.example.qrmealtrack.data.local.ReceiptDao
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao
) : ReceiptRepository {
    override fun getAllReceipts(): Flow<List<ReceiptEntity>> = dao.getAllReceipts()

    override suspend fun insertReceipt(receipt: ReceiptEntity): Long {
        return dao.insertReceipt(receipt)
    }

    override suspend fun countReceipts(fiscalCode: String, dateTime: Long): Int {
        return dao.countReceiptsForCheck(fiscalCode, dateTime)
    }

    override suspend fun getReceiptsByFiscalCodeAndDate(fiscalCode: String, dateTime: Long): List<ReceiptEntity> {
        return dao.getReceiptsByFiscalCodeAndDate(fiscalCode, dateTime)
    }
}