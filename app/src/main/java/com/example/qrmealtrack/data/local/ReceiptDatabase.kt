package com.example.qrmealtrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ReceiptEntity::class], version = 7)
abstract class ReceiptDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}