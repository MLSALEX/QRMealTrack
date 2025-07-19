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

    // ✅ Сохраняем сам чек
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    // ✅ Сохраняем товары
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ReceiptItemEntity>)

    // ✅ Удаляем чек (Room сам удалит товары из-за CASCADE)
    @Delete
    suspend fun deleteReceipt(receipt: ReceiptEntity)

    // ✅ Получаем ВСЕ чеки с товарами
    @Transaction
    @Query("SELECT * FROM receipt ORDER BY dateTime DESC")
    fun getAllReceiptsWithItems(): Flow<List<ReceiptWithItems>>

    // ✅ Получаем конкретный чек с товарами
    @Transaction
    @Query("SELECT * FROM receipt WHERE id = :receiptId")
    suspend fun getReceiptWithItems(receiptId: Long): ReceiptWithItems?

    // ✅ Поиск по fiscalCode + dateTime (для проверки дублей)
    @Transaction
    @Query("SELECT * FROM receipt WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun getReceiptGroup(fiscalCode: String, dateTime: Long): List<ReceiptWithItems>

    // ✅ Счётчик дублей
    @Query("SELECT COUNT(*) FROM receipt WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun countReceiptsForCheck(fiscalCode: String, dateTime: Long): Int

    // ✅ Удаление группы по fiscalCode + dateTime
    @Query("DELETE FROM receipt WHERE fiscalCode = :fiscalCode AND dateTime = :dateTime")
    suspend fun deleteReceiptGroup(fiscalCode: String, dateTime: Long)

    @Query("UPDATE receipt SET category = :categoryKey WHERE id = :receiptId")
    suspend fun updateReceiptCategory(receiptId: Long, categoryKey: String)
}