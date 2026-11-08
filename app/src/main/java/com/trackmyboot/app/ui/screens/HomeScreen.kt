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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.overlap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.trackmyboot.app.R
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.ProductCategory
import com.trackmyboot.app.presentation.viewmodel.HomeViewModel
import com.trackmyboot.app.ui.components.CategoryChip
import com.trackmyboot.app.ui.components.ProductCard
import com.trackmyboot.app.ui.theme.Theme
import coil3.compose.AsyncImage
import coil3.compose.Crossfade
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSearchClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle(initialValue = emptyList())
    val productsOnSale by viewModel.productsOnSale.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle(initialValue = null)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val error by viewModel.error.collectAsStateWithLifecycle(initialValue = null)
    val categories = viewModel.categories

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top App Bar
            item {
                TopAppBar(
                    title = { Text(stringResource(R.string.home_title), fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                                contentDescription = stringResource(R.string.nav_search)
                            )
                        }
                        IconButton(onClick = onWatchlistClick) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(R.string.nav_watchlist)
                            )
                        }
                        IconButton(onClick = onAlertsClick) {
                            Box(modifier = Modifier.padding(end = 8.dp)) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.NotificationsNone,
                                    contentDescription = stringResource(R.string.nav_alerts)
                                )
                            }
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                                contentDescription = stringResource(R.string.nav_settings)
                            )
                        }
                    }
                )
            }

            // Search bar
            item {
                SearchBar(onClick = onSearchClick)
            }

            // Categories
            item {
                CategoryChips(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryClick = { category ->
                        viewModel.selectCategory(category)
                    }
                )
            }

            // Error message
            error?.let {
                item {
                    ErrorBanner(message = it, onRetry = { viewModel.refreshData() })
                }
            }

            // Featured products on sale
            if (productsOnSale.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.best_deal),
                        onSeeAllClick = { viewModel.selectCategory(null) }
                    )
                }
                item {
                    ProductGrid(
                        products = productsOnSale.take(8),
                        onProductClick = onProductClick
                    )
                }
            }

            // All products
            item {
                SectionHeader(
                    title = if (selectedCategory != null) selectedCategory!!.displayName else stringResource(R.string.home_title),
                    onSeeAllClick = { viewModel.selectCategory(null) }
                )
            }
            item {
                if (isLoading && products.isEmpty()) {
                    LoadingIndicator()
                } else if (products.isEmpty()) {
                    EmptyState(
                        message = stringResource(R.string.no_results),
                        onActionClick = { viewModel.refreshData() }
                    )
                } else {
                    ProductGrid(
                        products = products,
                        onProductClick = onProductClick
                    )
                }
            }
        }

        if (isLoading && products.isNotEmpty()) {
            LoadingOverlay()
        }
    }
}

@Composable
fun SearchBar(onClick: () -> Unit) {
    androidx.compose.material3.TextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        readOnly = true,
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Mic,
                contentDescription = null
            )
        },
        singleLine = true
    )
}

@Composable
fun CategoryChips(
    categories: List<ProductCategory>,
    selectedCategory: ProductCategory?,
    onCategoryClick: (ProductCategory?) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryChip(
            label = stringResource(R.string.category_all),
            isSelected = selectedCategory == null,
            onClick = { onCategoryClick(null) }
        )
        categories.forEach { category ->
            CategoryChip(
                label = category.displayName,
                isSelected = selectedCategory == category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            text = stringResource(R.string.view_all),
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        ).also { it.modifier = Modifier
            .fillMaxWidth()
            .clickable { onSeeAllClick() }
            .padding(16.dp)
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
fun LoadingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = androidx.compose.foundation.layout.dp(8)
        ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
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