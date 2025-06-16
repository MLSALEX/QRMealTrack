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
    val price: Double,
    val dateTime: Long,
    val type: String,
    val unitPrice: Double,
    val total: Double,
)