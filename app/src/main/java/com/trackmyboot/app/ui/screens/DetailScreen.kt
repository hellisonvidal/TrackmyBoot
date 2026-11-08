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
import com.trackmyboot.app.domain.model.PriceHistory
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.presentation.viewmodel.DetailViewModel
import com.trackmyboot.app.ui.components.AsyncImage
import com.trackmyboot.app.ui.theme.Theme
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    productId: String,
    onBackClick: () -> Unit
) {
    val productWithWatchlist by viewModel.productWithWatchlist.collectAsStateWithLifecycle(initialValue = null)
    val priceHistory by viewModel.priceHistory.collectAsStateWithLifecycle(initialValue = emptyList())
    val priceStats by viewModel.priceStats.collectAsStateWithLifecycle(initialValue = null)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val error by viewModel.error.collectAsStateWithLifecycle(initialValue = null)

    val product = productWithWatchlist?.product
    val watchlistItem = productWithWatchlist?.watchlistItem
    val isInWatchlist = productWithWatchlist?.isInWatchlist ?: false

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Top App Bar
        item {
            TopAppBar(
                title = { Text("", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
            )
        }

        product?.let { p ->
            // Product Image Header
            item {
                ProductImageHeader(
                    product = p,
                    isInWatchlist = isInWatchlist,
                    onWatchlistClick = {
                        if (isInWatchlist) {
                            viewModel.removeFromWatchlist(p.id)
                        } else {
                            viewModel.addToWatchlist(p)
                        }
                    }
                )
            }

            // Product Info
            item {
                ProductInfoSection(product = p)
            }

            // Price History Chart
            if (priceHistory.isNotEmpty()) {
                item {
                    PriceHistoryChart(
                        history = priceHistory,
                        stats = priceStats
                    )
                }
            }

            // Watchlist settings (if in watchlist)
            if (isInWatchlist) {
                item {
                    WatchlistSettingsSection(
                        watchlistItem = watchlistItem!!,
                        product = p,
                        onTargetPriceChange = { price ->
                            viewModel.updateTargetPrice(p.id, price)
                        },
                        onPriceDropToggle = { enabled ->
                            viewModel.togglePriceDropNotification(p.id, enabled)
                        },
                        onBackInStockToggle = { enabled ->
                            // Handle back in stock toggle
                        }
                    )
                }
            }

            // Product Details
            item {
                ProductDetailsSection(product = p)
            }

            // Store Link
            item {
                StoreLinkSection(product = p)
            }
        }

        if (isLoading && product == null) {
            item {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun ProductImageHeader(
    product: Product,
    isInWatchlist: Boolean,
    onWatchlistClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            crossfade = Crossfade(true)
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Bottom info
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.brand,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = product.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.overflow.TextOverflow.Ellipsis
                    )
                }

                // Watchlist button
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isInWatchlist)
                                androidx.compose.material3.MaterialTheme.colorScheme.secondary
                            else
                                Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .fillMaxWidth()
                        .clickable { onWatchlistClick() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist)
                                androidx.compose.material.icons.Icons.Default.Favorite
                            else
                                androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                            contentDescription = if (isInWatchlist)
                                stringResource(R.string.remove_from_watchlist)
                            else
                                stringResource(R.string.add_to_watchlist),
                            tint = if (isInWatchlist) Color.White else androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isInWatchlist)
                                stringResource(R.string.remove_from_watchlist)
                            else
                                stringResource(R.string.add_to_watchlist),
                            color = if (isInWatchlist) Color.White else androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Top bar area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            // Status badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (product.isOnSale) {
                    Box(
                        modifier = Modifier.padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Surface(
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "-${product.discountPercentage}%",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Store badge
                Box(
                    modifier = Modifier.padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = product.store.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductInfoSection(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "R$ ${product.formattedCurrentPrice}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                    if (product.isOnSale) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "R$ ${product.formattedOriginalPrice}",
                                fontSize = 16.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                            androidx.compose.material3.Badge(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                label = {
                                    Text(
                                        "-${product.discountPercentage}%",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            ) {
                                Box()
                            }
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (product.isAvailable)
                                androidx.compose.material.icons.Icons.Default.CheckCircle
                            else
                                androidx.compose.material.icons.Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (product.isAvailable)
                                Color(0xFF388E3C)
                            else
                                Color(0xFFD32F2F)
                        )
                        Text(
                            text = if (product.isAvailable)
                                stringResource(R.string.in_stock)
                            else
                                stringResource(R.string.out_of_stock),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (product.isAvailable)
                                Color(0xFF388E3C)
                            else
                                Color(0xFFD32F2F)
                        )
                    }

                    // Rating
                    if (product.rating > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                contentDescription = null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = String.format("%.1f", product.rating),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "(${product.reviewCount})",
                                fontSize = 12.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Sizes and colors
            if (product.sizes.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Tamanhos:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        product.sizes.forEach { size ->
                            androidx.compose.material3.Chip(
                                onClick = {},
                                label = { Text(size, fontSize = 12.sp) },
                                colors = ChipDefaults.chipColors(
                                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            )
                        }
                    }
                }
            }

            if (product.colors.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Cores:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        product.colors.forEach { color ->
                            androidx.compose.material3.Chip(
                                onClick = {},
                                label = { Text(color, fontSize = 12.sp) },
                                colors = ChipDefaults.chipColors(
                                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceHistoryChart(
    history: List<PriceHistory>,
    stats: com.trackmyboot.app.presentation.viewmodel.PriceStats?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title and stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.price_history),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (stats != null && stats.hasData) {
                    Text(
                        text = "Mín: R$ ${stats.lowestPrice?.let { String.format("%.2f", it).replace(".", ",") }} | Máx: R$ ${stats.highestPrice?.let { String.format("%.2f", it).replace(".", ",") }}",
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Simple chart visualization
            PriceChartGraph(history = history)

            // Stats row
            if (stats != null && stats.hasData) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(
                        label = "Menor",
                        value = "R$ ${stats.lowestPrice?.let { String.format("%.2f", it).replace(".", ",") } ?: "N/A"}"
                    )
                    StatItem(
                        label = "Maior",
                        value = "R$ ${stats.highestPrice?.let { String.format("%.2f", it).replace(".", ",") } ?: "N/A"}"
                    )
                    StatItem(
                        label = "Média",
                        value = "R$ ${String.format("%.2f", stats.averagePrice).replace(".", ",")}"
                    )
                    if (stats.savingsFromHighest != null && stats.savingsFromHighest!! > 0) {
                        StatItem(
                            label = "Economia",
                            value = "R$ ${String.format("%.2f", stats.savingsFromHighest!!).replace(".", ",")} (${stats.savingsPercentage}%)",
                            valueColor = androidx.compose.material3.MaterialTheme.colorScheme.success
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriceChartGraph(history: List<PriceHistory>) {
    // Simple line chart using Canvas
    val prices = history.reversed().map { it.price }
    val minPrice = prices.minOrNull() ?: 0.0
    val maxPrice = prices.maxOrNull() ?: 1.0
    val range = maxPrice - minPrice

    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        val width = size.width
        val height = size.height
        val padding = 40f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding

        if (prices.size > 1 && range > 0) {
            val stepX = chartWidth / (prices.size - 1)

            // Draw grid lines
            val gridColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
            for (i in 0..4) {
                val y = padding + (i * chartHeight / 4)
                drawLine(
                    color = gridColor,
                    start = androidx.compose.ui.geometry.Offset(padding, y),
                    end = androidx.compose.ui.geometry.Offset(width - padding, y),
                    strokeWidth = 1f
                )
            }

            // Draw price line
            val path = androidx.compose.ui.graphics.Path()
            prices.forEachIndexed { index, price ->
                val x = padding + index * stepX
                val normalizedPrice = (price - minPrice) / range
                val y = padding + chartHeight - normalizedPrice * chartHeight
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                style = androidx.compose.ui.graphics.DrawingStyle.Stroke(3f)
            )

            // Draw fill area
            val fillPath = androidx.compose.ui.graphics.Path()
            prices.forEachIndexed { index, price ->
                val x = padding + index * stepX
                val normalizedPrice = (price - minPrice) / range
                val y = padding + chartHeight - normalizedPrice * chartHeight
                if (index == 0) {
                    fillPath.moveTo(x, height - padding)
                    fillPath.lineTo(x, y)
                } else {
                    fillPath.lineTo(x, y)
                }
            }
            fillPath.lineTo(padding + (prices.size - 1) * stepX, height - padding)
            fillPath.close()

            drawPath(
                path = fillPath,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                style = androidx.compose.ui.graphics.DrawingStyle.Fill
            )

            // Draw points
            prices.forEachIndexed { index, price ->
                val x = padding + index * stepX
                val normalizedPrice = (price - minPrice) / range
                val y = padding + chartHeight - normalizedPrice * chartHeight
                drawCircle(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    center = androidx.compose.ui.geometry.Offset(x, y),
                    radius = 4f
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    valueColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun ProductDetailsSection(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.product_details), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = product.description, fontSize = 14.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StoreLinkSection(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Comprar na ${product.store.displayName}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }

            Button(
                onClick = {
                    // Open product URL in browser
                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(product.productUrl))
                        .also { context -> context.startActivity(it) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.open_store), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun WatchlistSettingsSection(
    watchlistItem: com.trackmyboot.app.domain.model.WatchlistItem,
    product: Product,
    onTargetPriceChange: (Double?) -> Unit,
    onPriceDropToggle: (Boolean) -> Unit,
    onBackInStockToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Configurações de Alerta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer)

            // Target price
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.target_price), fontSize = 14.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer)
                androidx.compose.material3.OutlinedTextField(
                    value = watchlistItem.targetPrice?.let { "R$ ${it}" } ?: "",
                    onValueChange = { text ->
                        val price = text.replace("R$", "").replace(",", ".").trim().toDoubleOrNull()
                        onTargetPriceChange(price)
                    },
                    modifier = Modifier.width(140.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.ui.platform.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    placeholder = { Text("R$ 0,00") },
                    label = { Text(stringResource(R.string.target_price)) }
                )
            }

            // Notification toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NotificationToggle(
                    label = stringResource(R.string.notify_price_drop),
                    checked = watchlistItem.notifyOnPriceDrop,
                    onCheckedChange = onPriceDropToggle
                )
                NotificationToggle(
                    label = stringResource(R.string.notify_back_in_stock),
                    checked = watchlistItem.notifyOnBackInStock,
                    onCheckedChange = onBackInStockToggle
                )
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