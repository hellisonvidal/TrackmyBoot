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
import com.trackmyboot.app.domain.model.Alert
import com.trackmyboot.app.presentation.viewmodel.AlertsViewModel
import com.trackmyboot.app.ui.theme.Theme
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel,
    onProductClick: (String) -> Unit
) {
    val alerts by viewModel.recentAlerts.collectAsStateWithLifecycle(initialValue = emptyList())
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle(initialValue = 0)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top App Bar
            item {
                TopAppBar(
                    title = { Text(stringResource(R.string.alerts_title), fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
                    actions = {
                        if (unreadCount > 0) {
                            androidx.compose.material3.TextButton(onClick = { viewModel.markAllAsRead() }) {
                                Text(stringResource(R.string.mark_all_read))
                            }
                        }
                    }
                )
            }

            // Unread count badge
            if (unreadCount > 0) {
                item {
                    androidx.compose.material3.Surface(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$unreadCount ${if (unreadCount == 1) stringResource(R.string.alert_unread) else stringResource(R.string.alerts_unread)}",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                            androidx.compose.material3.TextButton(onClick = { viewModel.markAllAsRead() }) {
                                Text(stringResource(R.string.mark_all_read))
                            }
                        }
                    }
                }
            }

            // Alerts list
            item {
                if (alerts.isEmpty()) {
                    AlertsEmptyState()
                } else if (isLoading) {
                    LoadingIndicator()
                } else {
                    items(alerts) { alert ->
                        AlertCard(
                            alert = alert,
                            onClick = { onProductClick(alert.productId) },
                            onReadClick = { viewModel.markAsRead(alert.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertsEmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.NotificationsNone,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.alerts_empty),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.alerts_empty_desc),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AlertCard(
    alert: Alert,
    onClick: () -> Unit,
    onReadClick: () -> Unit
) {
    val alertColor = when (alert.alertType) {
        com.trackmyboot.app.domain.model.AlertType.PRICE_DROP -> androidx.compose.material3.MaterialTheme.colorScheme.success
        com.trackmyboot.app.domain.model.AlertType.TARGET_PRICE_REACHED -> androidx.compose.material3.MaterialTheme.colorScheme.secondary
        com.trackmyboot.app.domain.model.AlertType.BACK_IN_STOCK -> androidx.compose.material3.MaterialTheme.colorScheme.primary
        com.trackmyboot.app.domain.model.AlertType.NEW_LOWEST_PRICE -> androidx.compose.material3.MaterialTheme.colorScheme.tertiary
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!alert.isRead)
                alertColor.copy(alpha = 0.05f)
            else
                androidx.compose.material3.MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (!alert.isRead) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with alert type and time
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(8.dp)
                            .background(alertColor)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                    )
                    Text(
                        text = alert.alertType.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = alertColor
                    )
                }

                Text(
                    text = formatTime(alert.createdAt.time),
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Product info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = alert.productImageUrl,
                    contentDescription = alert.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                    crossfade = Crossfade(true)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = alert.productName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.overflow.TextOverflow.Ellipsis
                    )

                    // Price comparison
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "R$ ${alert.formattedCurrentPrice}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                        if (alert.previousPrice > alert.currentPrice) {
                            Text(
                                text = "R$ ${alert.formattedPreviousPrice}",
                                fontSize = 12.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                            androidx.compose.material3.Badge(
                                backgroundColor = alertColor,
                                label = { Text("-${alert.priceDropPercentage}%", color = Color.White, fontSize = 10.sp) }
                            ) {
                                Box()
                            }
                        }
                    }
                }

                if (!alert.isRead) {
                    IconButton(onClick = onReadClick) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                            contentDescription = stringResource(R.string.mark_read),
                            tint = alertColor
                        )
                    }
                }
            }

            // Message
            Text(
                text = alert.message,
                fontSize = 12.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = androidx.compose.ui.text.overflow.TextOverflow.Ellipsis
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

private fun formatTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> "agora mesmo"
        diff < 3_600_000 -> "${diff / 60_000} min atrás"
        diff < 86_400_000 -> "${diff / 3_600_000}h atrás"
        else -> "${diff / 86_400_000}d atrás"
    }
}