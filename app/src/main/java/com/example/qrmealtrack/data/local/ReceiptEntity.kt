package com.example.qrmealtrack.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.qrmealtrack.domain.model.ReceiptCategory

@Entity(tableName = "receipt")
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val fiscalCode: String,
    val enterprise: String,
    val total: Double,
    val dateTime: Long,
    val category: String = ReceiptCategory.NO_CATEGORY.key
)
@Entity(
    tableName = "receipt_item",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("receiptId")]
)
data class ReceiptItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val receiptId: Long, // FK → ReceiptEntity
    val name: String,
    val weight: Double,
    val unitPrice: Double,
    val price: Double,
    val isWeightBased: Boolean = false,
    val category: String? = null
)