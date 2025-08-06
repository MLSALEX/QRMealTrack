package com.example.qrmealtrack.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.qrmealtrack.R
import com.example.qrmealtrack.domain.model.ReceiptCategory

data class CategoryUi(
    @DrawableRes val resId: Int,
    val key: String,
    val name: String,
    val isSelected: Boolean = false
)

sealed interface FilterType {
    data class Categories(val categories: Set<CategoryUi>) : FilterType

    fun getSize(): Int = when (this) {
        is Categories -> categories.size
    }

    fun getSelectedCount(): Int = when (this) {
        is Categories -> categories.count { it.isSelected }
    }

    fun getSelectedNames(): List<String> = when (this) {
        is Categories -> categories.filter { it.isSelected }.map { it.name }.sorted()
    }

    fun getSelectedKeys(): List<String> = when (this) {
        is Categories -> categories.filter { it.isSelected }.map { it.key }
    }
}
@Composable
fun CategoryFilterDropdown(
    title: String,
    filterType: FilterType.Categories,
    onToggle: (FilterType.Categories) -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier,
    itemVerticalPadding: Dp = 8.dp,
    dropdownWidth: Dp = 180.dp
) {
    var expanded by remember { mutableStateOf(false) }
    var itemHeight by remember { mutableStateOf(0) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier
                .height(32.dp)
                .clip(RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Cyan.copy(alpha = 0.15f),
                contentColor = Color.White
            ),
            border = BorderStroke(0.5.dp, Color.Cyan)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getTitle(title, filterType),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )

                if (filterType.getSelectedCount() > 0) {
                    Spacer(Modifier.width(8.dp))

                    // ✅ Отдельная иконка "очистить"
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear filter",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onClearFilter()
                            },
                        tint = Color.White
                    )
                }

                Spacer(Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            modifier = Modifier
                .requiredWidth(dropdownWidth),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(10.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            offset = DpOffset(x = 0.dp, y = 4.dp)
        ) {
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .verticalScroll(scrollState)
                    .verticalColumnScrollbar(scrollState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    filterType.categories.forEach { category ->
                        FilterDropdownItem(
                            isSelected = category.isSelected,
                            itemVerticalPadding = itemVerticalPadding,
                            onClick = {
                                val updated = FilterType.Categories(
                                    categories = filterType.categories.map {
                                        if (category.name == it.name) it.copy(isSelected = !it.isSelected)
                                        else it
                                    }.toSet()
                                )
                                onToggle(updated)
                            },
                            modifier = Modifier.onGloballyPositioned {
                                itemHeight = it.size.height
                            }
                        ) {
                            Image(
                                painter = painterResource(category.resId),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(
                                    if (category.isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (category.isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            if (category.isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterDropdownItem(
    isSelected: Boolean,
    itemVerticalPadding: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) { content() }
        },
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = itemVerticalPadding),
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .background(getSelectedBackgroundColor(isSelected), shape = RoundedCornerShape(8.dp))
    )
}

@Composable
fun getSelectedBackgroundColor(isSelected: Boolean): Color {
    return if (isSelected) MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.05f)
    else Color.Transparent
}

@Composable
private fun getTitle(title: String, filterType: FilterType.Categories): String {
    val selectedFilters = filterType.getSelectedNames()
    val altTitle = buildList {
        addAll(selectedFilters.take(2))
        if (selectedFilters.size > 2) add("+${selectedFilters.size - 2}")
    }.joinToString(", ")

    return altTitle.ifBlank { title }
}
fun getDefaultCategories(): FilterType.Categories {
    return FilterType.Categories(
        setOf(
            CategoryUi(R.drawable.plate, ReceiptCategory.MEALS.key, "Meals", false),
            CategoryUi(R.drawable.clothing, ReceiptCategory.CLOTHING.key, "Clothing", false),
            CategoryUi(R.drawable.beauty, ReceiptCategory.BEAUTY.key, "Beauty", false),
            CategoryUi(R.drawable.transport, ReceiptCategory.TRANSPORT.key, "Transport", false),
            CategoryUi(R.drawable.cart, ReceiptCategory.GROCERIES.key, "Groceries", false)
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterDropdownPreview() {
    var selectedCategories by remember { mutableStateOf(getDefaultCategories()) }

    MaterialTheme {
        CategoryFilterDropdown(
            title = "All Categories",
            filterType = selectedCategories,
            onToggle = { updated -> selectedCategories = updated },
            onClearFilter = {
                selectedCategories = FilterType.Categories(
                    selectedCategories.categories.map { it.copy(isSelected = false) }.toSet()
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}