package com.example.qrmealtrack.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.qrmealtrack.data.local.relation.ReceiptWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ReceiptItemEntity>)

    @Delete
    suspend fun deleteReceipt(receipt: ReceiptEntity)

    @Transaction
    @Query("SELECT * FROM receipt ORDER BY dateTime DESC")
    fun getAllReceiptsWithItems(): Flow<List<ReceiptWithItems>>

    @Transaction
    @Query("SELECT * FROM receipt WHERE id = :receiptId")
    suspend fun getReceiptWithItems(receiptId: Long): ReceiptWithItems?

    @Transaction
    @Query("SELECT * FROM receipt WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun getReceiptGroup(fiscalCode: String, dateTime: Long): List<ReceiptWithItems>

    @Query("SELECT COUNT(*) FROM receipt WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun countReceiptsForCheck(fiscalCode: String, dateTime: Long): Int

    @Query("DELETE FROM receipt WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun deleteReceiptGroup(fiscalCode: String, dateTime: Long)

    @Query("UPDATE receipt SET category = :categoryKey WHERE id = :receiptId")
    suspend fun updateReceiptCategory(receiptId: Long, categoryKey: String)
}