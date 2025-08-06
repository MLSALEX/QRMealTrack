package com.example.qrmealtrack.domain.model

import androidx.annotation.DrawableRes
import com.example.qrmealtrack.R

enum class ReceiptCategory(val key: String) {
    NO_CATEGORY("no_category"),
    MEALS("meals"),
    CLOTHING("clothing"),
    BEAUTY("beauty"),
    TRANSPORT("transport"),
    GROCERIES("groceries");

    companion object {
        fun fromKey(key: String?): ReceiptCategory =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: NO_CATEGORY
    }
}