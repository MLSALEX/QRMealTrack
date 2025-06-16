package com.example.qrmealtrack.data.repository

import android.util.Log
import com.example.qrmealtrack.data.local.ReceiptDao
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.qrmealtrack.data.mapper.toDomainReceipts

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao
) : ReceiptRepository {
    override fun getAllReceipts(): Flow<List<Receipt>> {
        return dao.getAllReceipts()
            .map { list -> list.toDomainReceipts() }
    }

    override suspend fun insertReceipt(receipt: ReceiptEntity): Long {
        Log.d("🧾SAVE", "Сохраняем: ${receipt.itemName} | Вес: ${receipt.weight} г | Цена: ${receipt.price}")
        return dao.insertReceipt(receipt)
    }

    override suspend fun countReceipts(fiscalCode: String, dateTime: Long): Int {
        return dao.countReceiptsForCheck(fiscalCode, dateTime)
    }

    override suspend fun getReceiptsByFiscalCodeAndDate(fiscalCode: String, dateTime: Long): List<ReceiptEntity> {
        return dao.getReceiptsByFiscalCodeAndDate(fiscalCode, dateTime)
    }

    override suspend fun deleteReceiptGroup(fiscalCode: String, dateTime: Long) {
        dao.deleteReceiptGroup(fiscalCode, dateTime)
    }
}