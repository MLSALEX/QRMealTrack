package com.example.qrmealtrack.domain.model

import androidx.annotation.DrawableRes
import com.example.qrmealtrack.R

enum class ReceiptCategory(val key: String) {
    NO_CATEGORY("NO_CATEGORY"),
    MEALS("MEALS"),
    CLOTHING("CLOTHING"),
    BEAUTY("BEAUTY"),
    TRANSPORT("TRANSPORT"),
    GROCERIES("GROCERIES");

    companion object {
        fun fromKey(key: String?): ReceiptCategory =
            entries.firstOrNull { it.key == key } ?: NO_CATEGORY
    }
}