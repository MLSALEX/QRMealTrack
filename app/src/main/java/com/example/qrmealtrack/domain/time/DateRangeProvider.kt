package com.example.qrmealtrack.domain.time

import com.example.qrmealtrack.presentation.stats.TimeFilter

interface DateRangeProvider {
    fun rangeFor(filter: TimeFilter): DateRange
}