package com.example.qrmealtrack.domain.usecase

import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.model.ReceiptCategory
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.presentation.trends.components.GranularityType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.ZoneId

class GetFilteredChartPointsUseCaseTest {

 private lateinit var repository: ReceiptRepository
 private lateinit var useCase: GetFilteredChartPointsUseCase

 @Before
 fun setup() {
  repository = mock()
  useCase = GetFilteredChartPointsUseCase(repository)
 }

 @Test
 fun `returns chart point grouped by day for selected category`() = runTest {
  val receipts = listOf(
   fakeReceipt(ReceiptCategory.MEALS, 10.0, date("2025-08-01")),
   fakeReceipt(ReceiptCategory.MEALS, 5.0, date("2025-08-01"))
  )
  whenever(repository.getAllReceipts()).thenReturn(flowOf(receipts))

  val result = useCase(
   granularity = GranularityType.DAY,
   selectedKeys = listOf(ReceiptCategory.MEALS.key)
  ).first()

  val expectedDate = LocalDate.of(2025, 8, 1)
  val chartPoints = result[ReceiptCategory.MEALS.key] ?: error("Chart points for '${ReceiptCategory.MEALS.key}' not found")

  assertNotNull(chartPoints)
  assertEquals(15.0f, chartPoints.first().value)
  assertEquals(expectedDate, chartPoints.first().localDate)
 }

 @Test
 fun `groups receipts by week when granularity is WEEK`() = runTest {
  val receipts = listOf(
   fakeReceipt(ReceiptCategory.MEALS, 10.0, date("2025-08-01")),
   fakeReceipt(ReceiptCategory.MEALS, 5.0, date("2025-08-01"))
  )
  whenever(repository.getAllReceipts()).thenReturn(flowOf(receipts))

  val result = useCase(GranularityType.WEEK, listOf("meals")).first()

  val expectedWeekStart = LocalDate.of(2025, 7, 28)
  val chartPoints = result[ReceiptCategory.MEALS.key] ?: error("Chart points for '${ReceiptCategory.MEALS.key}' not found")

  assertNotNull(chartPoints)
  assertEquals(15.0f, chartPoints.first().value)
  assertEquals(expectedWeekStart, chartPoints.first().localDate)
 }

 @Test
 fun `filters receipts by selected category only`() = runTest {
  val receipts = listOf(
   fakeReceipt(ReceiptCategory.MEALS, 10.0, date("2025-08-01")),
   fakeReceipt(ReceiptCategory.MEALS, 5.0, date("2025-08-01"))
  )
  whenever(repository.getAllReceipts()).thenReturn(flowOf(receipts))

  val result = useCase(GranularityType.DAY, listOf("meals")).first()

  assertEquals(setOf("meals"), result.keys)
  assertEquals(15.0f, result["meals"]!!.first().value)
 }

 @Test
 fun `returns multiple chart points for same category on different days`() = runTest {
  val receipts = listOf(
   fakeReceipt(ReceiptCategory.MEALS, 10.0, date("2025-08-01")),
   fakeReceipt(ReceiptCategory.MEALS, 5.0, date("2025-08-02"))
  )
  whenever(repository.getAllReceipts()).thenReturn(flowOf(receipts))

  val result = useCase(GranularityType.DAY, listOf(ReceiptCategory.MEALS.key)).first()

  val chartPoints = result[ReceiptCategory.MEALS.key]
   ?: error("Chart points for '${ReceiptCategory.MEALS.key}' not found")
  assertNotNull(chartPoints)
  assertEquals(2, chartPoints.size)

  assertEquals(LocalDate.of(2025, 8, 1), chartPoints[0].localDate)
  assertEquals(10.0f, chartPoints[0].value)

  assertEquals(LocalDate.of(2025, 8, 2), chartPoints[1].localDate)
  assertEquals(5.0f, chartPoints[1].value)
 }


 private fun fakeReceipt(
  category: ReceiptCategory,
  total: Double,
  dateTime: Long
 ): Receipt {
  return Receipt(
   id = 0,
   total = total,
   dateTime = dateTime,
   fiscalCode = "0000",
   enterprise = "Test",
   type = "Normal",
   items = emptyList(),
   category = category
  )
 }

 private fun date(isoDate: String): Long {
  return LocalDate.parse(isoDate)
   .atStartOfDay(ZoneId.of("UTC"))
   .toInstant()
   .toEpochMilli()
 }
}