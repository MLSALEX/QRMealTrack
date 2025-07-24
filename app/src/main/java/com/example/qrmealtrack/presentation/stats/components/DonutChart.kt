package com.example.qrmealtrack.presentation.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qrmealtrack.presentation.stats.components.utils.PreviewMocks
import com.example.qrmealtrack.presentation.stats.components.utils.drawArcSegment
import com.example.qrmealtrack.presentation.stats.components.utils.drawLabelOnArc
import com.example.qrmealtrack.presentation.stats.model.CategoryUiModel
import com.example.qrmealtrack.ui.theme.stats.StatsTheme

@Composable
fun DonutChart(
    categories: List<CategoryUiModel>,
    modifier: Modifier = Modifier,
) {
    // Мемоизируем Paint (создаётся 1 раз, не при каждой итерации)
    val textPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
    }

    Canvas(modifier = modifier) {
        if (categories.isEmpty()) return@Canvas

        val minSize = size.minDimension
        val total = categories.sumOf { it.value }
        var startAngle = -90f
        val radius = minSize / 2
        val dynamicStroke = minSize * 0.5f
        textPaint.textSize = minSize * 0.06f // обновляем размер шрифта относительно minSize


        categories.forEach { item ->
            val sweepAngle = if (total > 0) (item.value.toFloat() / total * 360f) else 0f

            drawArcSegment(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                strokeWidth = dynamicStroke
            )
            drawLabelOnArc(
                canvas = drawContext.canvas.nativeCanvas,
                center = center,
                radius = radius,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                labelText = item.labelText,
                textPaint = textPaint,
                stroke = dynamicStroke
            )

            startAngle += sweepAngle
        }
    }
}

@Immutable
data class CategoryStat(
    val category: String,
    val total: Double
)

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    StatsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            DonutChart(
                categories = PreviewMocks.categoryUiModels,
                modifier = Modifier.size(250.dp)
            )
        }
    }
}
