package com.example.qrmealtrack.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.qrmealtrack.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.res.painterResource
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
    itemVerticalPadding: Dp = 8.dp
) {
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var itemHeight by remember { mutableStateOf(0) }

    val itemHeightDp by remember {
        derivedStateOf { with(density) { itemHeight.toDp().plus(itemVerticalPadding) } }
    }
    val menuWidth = LocalConfiguration.current.screenWidthDp.dp - 32.dp

    val dropdownSelectedBackgroundColor = if (expanded) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color(0xffc1c3ce)
    }
    val shape = RoundedCornerShape(999.dp)
    val backgroundModifier = if (expanded) {
        Modifier.background(Color.White, shape = shape)
    } else {
        Modifier.background(Color.Transparent, shape = shape)
    }

    Column(modifier = modifier) {
        // Кнопка открытия дропдауна
        Box(
            modifier = Modifier
                .border(1.dp, dropdownSelectedBackgroundColor, shape)
                .then(backgroundModifier)
                .clickable { expanded = true }
                .animateContentSize()
        ) {
            FilterTitle(
                title = title,
                filterType = filterType,
                onClearFilter = onClearFilter
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            modifier = Modifier
                .width(menuWidth)
                .heightIn(
                    min = itemHeightDp.times(filterType.getSize().coerceAtMost(3)),
                    max = itemHeightDp.times(filterType.getSize().coerceAtMost(5))
                ),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White
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
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.secondary,
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
@Composable
private fun FilterTitle(
    title: String,
    filterType: FilterType.Categories,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paddingStart = if (filterType.categories.none { it.isSelected }) 12.dp else 0.dp
    val paddingEnd = if (filterType.getSelectedCount() > 0) 8.dp else 12.dp

    Row(
        modifier = modifier
            .padding(vertical = 6.dp)
            .padding(start = paddingStart),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getTitle(title, filterType),
            modifier = Modifier.padding(end = paddingEnd),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
        )
        if (filterType.getSelectedCount() > 0) {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable { onClearFilter() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear filter",
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier.size(18.dp)
                )
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
private fun getSelectedBackgroundColor(isSelected: Boolean): Color {
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