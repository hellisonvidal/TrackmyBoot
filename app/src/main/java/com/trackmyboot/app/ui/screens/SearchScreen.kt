package com.trackmyboot.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmyboot.app.R
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.ProductCategory
import com.trackmyboot.app.presentation.viewmodel.SearchViewModel
import com.trackmyboot.app.presentation.viewmodel.SortOption
import com.trackmyboot.app.ui.components.CategoryChip
import com.trackmyboot.app.ui.components.ProductCard
import com.trackmyboot.app.ui.theme.Theme
import coil3.compose.AsyncImage
import coil3.compose.Crossfade
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onProductClick: (String) -> Unit
) {
    val query by viewModel.query.collectAsStateWithLifecycle(initialValue = "")
    val results by viewModel.sortedAndFilteredResults.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle(initialValue = null)
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle(initialValue = SortOption.RELEVANCE)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val error by viewModel.error.collectAsStateWithLifecycle(initialValue = null)
    val categories = viewModel.categories

    var searchText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top App Bar
            item {
                TopAppBar(
                    title = {
                        androidx.compose.material3.TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.search_hint)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Search,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (searchText.isNotBlank()) {
                                    IconButton(onClick = { searchText = "" }) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                            contentDescription = stringResource(R.string.clear)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = androidx.compose.ui.platform.KeyboardOptions(
                                imeAction = androidx.compose.ui.text.input.ImeAction.Search
                            ),
                            keyboardActions = androidx.compose.ui.platform.KeyboardActions(
                                onSearch = { viewModel.setQuery(searchText) }
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
                )
            }

            // Categories filter
            item {
                CategoryChips(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryClick = { category ->
                        viewModel.setCategory(category)
                    }
                )
            }

            // Sort options
            item {
                SortChips(
                    currentSort = sortBy,
                    onSortClick = { sort ->
                        viewModel.setSortBy(sort)
                    }
                )
            }

            // Error message
            error?.let {
                item {
                    ErrorBanner(message = it, onRetry = { viewModel.setQuery(query) })
                }
            }

            // Results
            item {
                if (query.isBlank()) {
                    SearchEmptyState()
                } else if (isLoading && results.isEmpty()) {
                    LoadingIndicator()
                } else if (results.isEmpty()) {
                    EmptyState(
                        message = stringResource(R.string.no_results),
                        onActionClick = { viewModel.setQuery(query) }
                    )
                } else {
                    ResultHeader(count = results.size)
                    ProductGrid(
                        products = results,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
fun SortChips(
    currentSort: SortOption,
    onSortClick: (SortOption) -> Unit
) {
    val options = listOf(
        SortOption.RELEVANCE to stringResource(R.string.sort_relevance),
        SortOption.PRICE_LOW to stringResource(R.string.sort_price_low),
        SortOption.PRICE_HIGH to stringResource(R.string.sort_price_high),
        SortOption.DISCOUNT to stringResource(R.string.sort_discount),
        SortOption.NEWEST to stringResource(R.string.sort_newest)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (sort, label) ->
            CategoryChip(
                label = label,
                isSelected = currentSort == sort,
                onClick = { onSortClick(sort) }
            )
        }
    }
}

@Composable
fun ResultHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${count} ${stringResource(R.string.search_results)}",
            fontSize = 16.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchEmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.search_hint),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
            )
            androidx.compose.material3.TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun EmptyState(message: String, onActionClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.SearchOff,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = message,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            androidx.compose.material3.FilledButton(onClick = onActionClick) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}