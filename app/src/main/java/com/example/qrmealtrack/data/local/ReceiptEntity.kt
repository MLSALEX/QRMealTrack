package com.example.qrmealtrack.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val fiscalCode: String,
    val enterprise: String,
    val itemName: String,
    val weight: Double,
    val price: Double, // цена за товар
    val dateTime: Long,
    val type: String,
    val isWeightBased: Boolean = false, // ← весовой товар
    val category: String? = null
//    val unitPrice: Double, // считано с чека, не пересчитывается
//    val total: Double, // сумма по чеку, считана с чека
)