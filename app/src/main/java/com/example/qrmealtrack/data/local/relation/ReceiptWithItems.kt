package com.example.qrmealtrack.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.qrmealtrack.data.local.ReceiptEntity
import com.example.qrmealtrack.data.local.ReceiptItemEntity

data class ReceiptWithItems(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "receiptId"
    )
    val items: List<ReceiptItemEntity>
)