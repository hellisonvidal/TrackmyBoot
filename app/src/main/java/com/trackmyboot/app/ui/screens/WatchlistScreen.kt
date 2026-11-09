package com.trackmyboot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmyboot.app.data.local.WatchedOffer
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.worker.PriceCheckWorker
import com.trackmyboot.app.ui.AppViewModel
import com.trackmyboot.app.ui.components.OfferImage
import com.trackmyboot.app.ui.components.PriceRow
import com.trackmyboot.app.ui.components.Sparkline
import com.trackmyboot.app.ui.components.StoreBadge
import com.trackmyboot.app.ui.components.WatchDialog
import com.trackmyboot.app.util.toBrl
import java.text.DateFormat
import java.util.Date

@Composable
fun WatchlistScreen(app: AppViewModel) {
    val items by app.watched.collectAsStateWithLifecycle()
    val history by app.history.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var editing by remember { mutableStateOf<WatchedOffer?>(null) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "O app confere os preços a cada 6 horas e avisa quando baixam.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = { PriceCheckWorker.runNow(context) }, enabled = items.isNotEmpty()) {
                Icon(Icons.Filled.Refresh, null)
                Spacer(Modifier.width(4.dp))
                Text("Conferir")
            }
        }
        if (items.isEmpty()) {
            Text(
                "Você ainda não monitora nenhum produto.\nToque no sino 🔔 de uma oferta para acompanhar o preço.",
                Modifier.fillMaxWidth().padding(32.dp),
                textAlign = TextAlign.Center,
            )
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items, key = { it.id }) { item ->
                WatchedCard(
                    item,
                    history[item.id].orEmpty().map { it.price },
                    onEdit = { editing = item },
                    onDelete = { app.unwatch(item.id) },
                )
            }
        }
    }

    editing?.let { item ->
        WatchDialog(item.title, item.currentPrice, item.targetPrice, onDismiss = { editing = null }) { target ->
            app.updateTarget(item, target)
            editing = null
        }
    }
}

@Composable
private fun WatchedCard(item: WatchedOffer, prices: List<Double>, onEdit: () -> Unit, onDelete: () -> Unit) {
    val uri = LocalUriHandler.current
    val discount = item.listPrice?.takeIf { it > item.currentPrice }?.let { (((it - item.currentPrice) / it) * 100).toInt() } ?: 0
    Card(onClick = { runCatching { uri.openUri(item.url) } }) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OfferImage(item.imageUrl, Category.detect(item.title)?.emoji ?: "⚽", 64)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    StoreBadge(item.storeName)
                    Text(item.title, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                    PriceRow(item.currentPrice, item.listPrice, discount)
                }
                Column {
                    IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Editar preço alvo") }
                    IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Parar de monitorar") }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    val diff = item.currentPrice - item.initialPrice
                    val trend = when {
                        diff < 0 -> "📉 ${(-diff).toBrl()} mais barato desde que você começou a monitorar"
                        diff > 0 -> "📈 ${diff.toBrl()} mais caro desde que você começou a monitorar"
                        else -> "Sem variação de preço até agora"
                    }
                    Text(trend, style = MaterialTheme.typography.labelMedium)
                    Text(
                        "Menor preço: ${item.lowestPrice.toBrl()}" + (item.targetPrice?.let { " · Alvo: ${it.toBrl()}" } ?: ""),
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        "Conferido em " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(item.lastCheckedAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Sparkline(prices, Modifier.width(90.dp).height(36.dp))
            }
        }
    }
}
