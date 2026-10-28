package com.pira.ccloud.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.FilterType
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.ui.glass.GlassChip
import com.pira.ccloud.ui.glass.glass

/**
 * Liquid Glass filters.
 *
 * Before, genres hid inside a second dropdown (two taps, no overview).
 * Now categories are a horizontally scrollable row of glass chips — one tap,
 * everything visible — while sorting stays a single frosted pill, because
 * sort order is a setting, not a category to browse.
 */
@Composable
fun GenreFilterSection(
    genres: List<Genre>,
    selectedGenreId: Int,
    selectedFilterType: FilterType,
    onGenreSelected: (Int) -> Unit,
    onFilterTypeSelected: (FilterType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Browse",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            FilterTypeSelector(
                selectedFilterType = selectedFilterType,
                onFilterTypeSelected = onFilterTypeSelected
            )
        }

        // Category chips: All first, then every genre, scrollable.
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) {
            item {
                GlassChip(
                    text = "All",
                    selected = selectedGenreId == 0,
                    onClick = { onGenreSelected(0) }
                )
            }
            items(genres) { genre ->
                GlassChip(
                    text = genre.title,
                    selected = selectedGenreId == genre.id,
                    onClick = { onGenreSelected(genre.id) }
                )
            }
        }
    }
}

@Composable
fun FilterTypeSelector(
    selectedFilterType: FilterType,
    onFilterTypeSelected: (FilterType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .glass(RoundedCornerShape(50.dp))
            .clickable { expanded = true }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when (selectedFilterType) {
                    FilterType.DEFAULT -> "Default"
                    FilterType.BY_YEAR -> "By Year"
                    FilterType.BY_IMDB -> "By IMDB"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Sort options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Default") },
                onClick = {
                    onFilterTypeSelected(FilterType.DEFAULT)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("By Year") },
                onClick = {
                    onFilterTypeSelected(FilterType.BY_YEAR)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("By IMDB") },
                onClick = {
                    onFilterTypeSelected(FilterType.BY_IMDB)
                    expanded = false
                }
            )
        }
    }
}
