package com.example.qrmealtrack.domain.time

import com.example.qrmealtrack.presentation.stats.TimeFilter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JavaTimeDateRangeProvider @Inject constructor() : DateRangeProvider {
    private val zoneId: ZoneId = ZoneId.systemDefault()

    override fun rangeFor(filter: TimeFilter): DateRange {
        val end = Long.MAX_VALUE
        val start = when (filter) {
            TimeFilter.Today -> LocalDate.now(zoneId)
                .atStartOfDay(zoneId).toInstant().toEpochMilli()
            TimeFilter.Week -> LocalDate.now(zoneId)
                .with(DayOfWeek.MONDAY)
                .atStartOfDay(zoneId).toInstant().toEpochMilli()
            TimeFilter.Month -> LocalDate.now(zoneId)
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay(zoneId).toInstant().toEpochMilli()
            TimeFilter.All -> Long.MIN_VALUE
        }
        return DateRange(start, end)
    }
}