package com.example.qrmealtrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReceiptEntity::class, ReceiptItemEntity::class],
    version = 9,
    exportSchema = true
)
abstract class ReceiptDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}