package com.example.qrmealtrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Query("SELECT * FROM ReceiptEntity")
    fun getAllReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM ReceiptEntity WHERE type = :type")
    fun getReceiptsByType(type: String): Flow<List<ReceiptEntity>>

    @Query("SELECT COUNT(*) FROM ReceiptEntity WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun countReceiptsForCheck(fiscalCode: String, dateTime: Long): Int

    @Query("SELECT * FROM ReceiptEntity WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun getReceiptsByFiscalCodeAndDate(fiscalCode: String, dateTime: Long): List<ReceiptEntity>
}