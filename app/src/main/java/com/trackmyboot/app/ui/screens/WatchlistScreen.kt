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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmyboot.app.R
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.presentation.viewmodel.WatchlistViewModel
import com.trackmyboot.app.ui.components.ProductCard
import com.trackmyboot.app.ui.theme.Theme
import coil3.compose.AsyncImage
import coil3.compose.Crossfade
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onProductClick: (String) -> Unit
) {
    val watchlistItems by viewModel.watchlistWithProducts.collectAsStateWithLifecycle(initialValue = emptyList())
    val watchlistCount by viewModel.watchlistCount.collectAsStateWithLifecycle(initialValue = 0)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val error by viewModel.error.collectAsStateWithLifecycle(initialValue = null)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top App Bar
            item {
                TopAppBar(
                    title = { Text(stringResource(R.string.watchlist_title), fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
                )
            }

            // Error message
            error?.let {
                item {
                    ErrorBanner(message = it, onRetry = { /* refresh */ })
                }
            }

            // Watchlist content
            item {
                if (watchlistItems.isEmpty()) {
                    WatchlistEmptyState()
                } else if (isLoading) {
                    LoadingIndicator()
                } else {
                    WatchlistHeader(count = watchlistCount)
                    items(watchlistItems) { item ->
                        WatchlistItemCard(
                            item = item,
                            onClick = { onProductClick(item.product.id) },
                            onRemoveClick = { viewModel.removeFromWatchlist(item.watchlistItem.productId) },
                            onTargetPriceChange = { newPrice ->
                                viewModel.updateTargetPrice(item.watchlistItem.productId, newPrice)
                            },
                            onNotificationToggle = { notifyDrop, notifyStock ->
                                viewModel.updateNotificationSettings(
                                    item.watchlistItem.productId,
                                    notifyDrop,
                                    notifyStock
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WatchlistHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$count ${if (count == 1) stringResource(R.string.watchlist_item) else stringResource(R.string.watchlist_items)}",
            fontSize = 16.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WatchlistEmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.watchlist_empty),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.watchlist_empty_desc),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    )
}

@Composable
fun WatchlistItemCard(
    item: com.trackmyboot.app.presentation.viewmodel.WatchlistItemWithProduct,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onTargetPriceChange: (Double?) -> Unit,
    onNotificationToggle: (Boolean, Boolean) -> Unit
) {
    val product = item.product
    val watchlistItem = item.watchlistItem

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Product row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                    crossfade = Crossfade(true)
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(text = product.brand, fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = androidx.compose.ui.text.overflow.TextOverflow.Ellipsis)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R$ ${product.formattedCurrentPrice}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                        if (product.isOnSale) {
                            Text(
                                text = "R$ ${product.formattedOriginalPrice}",
                                fontSize = 12.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }
                }

                // Remove button
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.watchlist_remove),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
            }

            // Target price and notifications
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Target price input
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.target_price), fontSize = 14.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)

                    androidx.compose.material3.OutlinedTextField(
                        value = watchlistItem.targetPrice?.let { "R$ ${it}" } ?: "",
                        onValueChange = { text ->
                            val price = text.replace("R$", "").replace(",", ".").trim().toDoubleOrNull()
                            onTargetPriceChange(price)
                        },
                        modifier = Modifier.width(120.dp),
                        singleLine = true,
                        keyboardOptions = androidx.compose.ui.platform.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        placeholder = { Text("R$ 0,00") },
                        label = { Text(stringResource(R.string.target_price)) }
                    )

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))

                    // Notification toggles
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        NotificationToggle(
                            label = stringResource(R.string.notify_price_drop),
                            checked = watchlistItem.notifyOnPriceDrop,
                            onCheckedChange = { onNotificationToggle(it, watchlistItem.notifyOnBackInStock) }
                        )
                        NotificationToggle(
                            label = stringResource(R.string.notify_back_in_stock),
                            checked = watchlistItem.notifyOnBackInStock,
                            onCheckedChange = { onNotificationToggle(watchlistItem.notifyOnPriceDrop, it) }
                        )
                    }
                }

                // Price drop indicator
                if (item.isAtTargetPrice) {
                    androidx.compose.material3.Surface(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.success.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.success
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Preço abaixo do alvo! Economia de R$ ${String.format("%.2f", item.priceDropFromTarget!!).replace(".", ",")}",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.success,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.material3.Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(text = label, fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
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
            Text(text = message, color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer)
            androidx.compose.material3.TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
        }
    }
}