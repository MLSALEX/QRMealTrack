package com.example.qrmealtrack.domain.usecase


import com.example.qrmealtrack.domain.model.Receipt
import com.example.qrmealtrack.domain.model.ReceiptCategory
import com.example.qrmealtrack.domain.model.ReceiptItem
import com.example.qrmealtrack.domain.model.StatsSummary
import com.example.qrmealtrack.domain.repository.ReceiptRepository
import com.example.qrmealtrack.domain.time.DateRange
import com.example.qrmealtrack.domain.time.DateRangeProvider
import com.example.qrmealtrack.presentation.stats.TimeFilter

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.ZoneId

class GetStatisticsUseCaseTest {

 private lateinit var repository: ReceiptRepository
 private lateinit var dateRangeProvider: DateRangeProvider
 private lateinit var useCase: GetStatisticsUseCase

 private val zone: ZoneId = ZoneId.systemDefault()
 private val delta = 1e-6

 @Before
 fun setUp() {
  MockitoAnnotations.openMocks(this)
  repository = mock()
  dateRangeProvider = mock()
  useCase = GetStatisticsUseCase(repository, dateRangeProvider)
 }

 @Test
 fun `sums only MEALS receipts within range (basic correctness)`() = runTest {
  val start = dateMillis("2025-08-01")
  whenever(dateRangeProvider.rangeFor(TimeFilter.Month))
   .thenReturn(DateRange(startMillis = start))

  val receipts = listOf(
   // MEALS inside the period → should be included (cost 10.0, weight 500g)
   receipt(
    id = 1L,
    category = ReceiptCategory.MEALS,
    total = 10.0,
    date = "2025-08-10",
    items = listOf(item(weight = 200.0, price = 3.0), item(weight = 300.0, price = 7.0))
   ),
   // Non-MEALS inside the period → should be ignored
   receipt(
    id = 2L,
    category = ReceiptCategory.GROCERIES,
    total = 999.0,
    date = "2025-08-15",
    items = listOf(item(weight = 1000.0, price = 999.0))
   ),
   // MEALS before start of period → should be ignored
   receipt(
    id = 3L,
    category = ReceiptCategory.MEALS,
    total = 5.0,
    date = "2025-07-25",
    items = listOf(item(weight = 100.0, price = 5.0))
   )
  )
  whenever(repository.getAllReceipts()).thenReturn(flowOf(receipts))

  val result: StatsSummary = useCase(TimeFilter.Month).first()

  assertEquals(10.0, result.totalCost, delta)
  assertEquals(500.0, result.totalWeight, delta)
 }

 @Test
 fun `includes receipts exactly at startMillis boundary`() = runTest {
  val start = dateMillis("2025-08-01")
  whenever(dateRangeProvider.rangeFor(TimeFilter.Month))
   .thenReturn(DateRange(startMillis = start))

  val boundaryReceipt = receipt(
   id = 10L,
   category = ReceiptCategory.MEALS,
   total = 12.0,
   date = "2025-08-01", // exactly at boundary → should be included (>= start)
   items = listOf(item(weight = 250.0, price = 12.0))
  )

  whenever(repository.getAllReceipts()).thenReturn(flowOf(listOf(boundaryReceipt)))

  val result = useCase(TimeFilter.Month).first()

  assertEquals(12.0, result.totalCost, delta)
  assertEquals(250.0, result.totalWeight, delta)
 }

 @Test
 fun `correctly sums across multiple MEALS receipts and items (decimals)`() = runTest {
  val start = dateMillis("2025-08-01")
  whenever(dateRangeProvider.rangeFor(TimeFilter.Month))
   .thenReturn(DateRange(startMillis = start))

  val r1 = receipt(
   id = 21L,
   category = ReceiptCategory.MEALS,
   total = 12.34, // decimals
   date = "2025-08-02",
   items = listOf(item(250.5, 6.17), item(249.5, 6.17)) // 500.0g
  )
  val r2 = receipt(
   id = 22L,
   category = ReceiptCategory.MEALS,
   total = 7.66, // 12.34 + 7.66 = 20.00
   date = "2025-08-03",
   items = listOf(item(300.0, 4.0), item(200.0, 3.66))   // 500.0g
  )
  val r3 = receipt(
   id = 23L,
   category = ReceiptCategory.GROCERIES, // non-MEALS → should be ignored
   total = 1000.0,
   date = "2025-08-03",
   items = listOf(item(999.9, 1000.0))
  )

  whenever(repository.getAllReceipts()).thenReturn(flowOf(listOf(r1, r2, r3)))

  val result = useCase(TimeFilter.Month).first()

  // expected total: MEALS only → 12.34 + 7.66 = 20.00
  assertEquals(20.00, result.totalCost, delta)
  // expected weight: 500.0 + 500.0 = 1000.0
  assertEquals(1000.0, result.totalWeight, delta)
 }

 @Test
 fun `returns zeros when no MEALS receipts in range`() = runTest {
  val start = dateMillis("2025-08-01")
  whenever(dateRangeProvider.rangeFor(TimeFilter.Month))
   .thenReturn(DateRange(startMillis = start))

  val receipts = listOf(
   receipt(
    id = 31L,
    category = ReceiptCategory.GROCERIES,
    total = 100.0,
    date = "2025-08-05",
    items = listOf(item(250.0, 100.0))
   )
  )
  whenever(repository.getAllReceipts()).thenReturn(flowOf(receipts))

  val result = useCase(TimeFilter.Month).first()

  assertEquals(0.0, result.totalCost, delta)
  assertEquals(0.0, result.totalWeight, delta)
 }

 @Test
 fun `returns zeros for empty source`() = runTest {
  val start = dateMillis("2025-08-01")
  whenever(dateRangeProvider.rangeFor(TimeFilter.Month))
   .thenReturn(DateRange(startMillis = start))
  whenever(repository.getAllReceipts()).thenReturn(flowOf(emptyList()))

  val result = useCase(TimeFilter.Month).first()

  assertEquals(0.0, result.totalCost, delta)
  assertEquals(0.0, result.totalWeight, delta)
 }

 // ---------- helpers ----------

 private fun dateMillis(isoDate: String, zone: ZoneId = this.zone): Long =
  LocalDate.parse(isoDate).atStartOfDay(zone).toInstant().toEpochMilli()

 private fun receipt(
  id: Long,
  category: ReceiptCategory,
  total: Double,
  date: String,
  items: List<ReceiptItem>
 ): Receipt = Receipt(
  id = id,
  fiscalCode = "FC-$id",
  enterprise = "Test Enterprise",
  dateTime = dateMillis(date),
  type = "QR", // string per your model
  items = items,
  total = total,
  category = category
 )

 private fun item(
  weight: Double,
  price: Double,
  name: String = "X",
  unitPrice: Double = 0.0,
  isWeightBased: Boolean = true,
  category: ReceiptCategory = ReceiptCategory.MEALS // item category does not affect the use case, but keep MEALS for clarity
 ): ReceiptItem = ReceiptItem(
  name = name,
  weight = weight,
  unitPrice = unitPrice,
  price = price,
  isWeightBased = isWeightBased,
  category = category
 )
}

