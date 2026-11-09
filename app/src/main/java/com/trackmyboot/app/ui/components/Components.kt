package com.trackmyboot.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.model.Club
import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.ui.theme.DealRed
import com.trackmyboot.app.util.toBrl

/** Escudo estilizado do clube: círculo em duas cores com as iniciais (sem usar marcas registradas). */
@Composable
fun ClubCrest(club: Club, size: Int = 44) {
    val primary = Color(club.primaryColor)
    val secondary = Color(club.secondaryColor)
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(2.dp, secondary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(primary)
            val stripe = Path().apply {
                moveTo(this@Canvas.size.width * 0.65f, 0f)
                lineTo(this@Canvas.size.width, 0f)
                lineTo(this@Canvas.size.width, this@Canvas.size.height)
                lineTo(this@Canvas.size.width * 0.35f, this@Canvas.size.height)
                close()
            }
            drawPath(stripe, secondary.copy(alpha = 0.85f))
        }
        val textColor = if (primary.luminance() > 0.5f) Color.Black else Color.White
        Text(
            club.initials,
            color = textColor,
            fontWeight = FontWeight.Black,
            fontSize = (size / 2.8).sp,
            modifier = Modifier
                .background(primary.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                .padding(horizontal = 2.dp),
        )
    }
}

private fun Color.luminance(): Float = 0.2126f * red + 0.7152f * green + 0.0722f * blue

@Composable
fun CategoryTile(category: Category, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(category.emoji, fontSize = 30.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                category.label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun OfferCard(offer: Offer, watched: Boolean, onWatch: () -> Unit, modifier: Modifier = Modifier) {
    val uri = LocalUriHandler.current
    Card(modifier = modifier.fillMaxWidth(), onClick = { runCatching { uri.openUri(offer.url) } }) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OfferImage(offer.imageUrl, Category.detect(offer.title)?.emoji ?: "⚽", 84)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StoreBadge(offer.storeName)
                    if (!offer.available) {
                        Spacer(Modifier.width(6.dp))
                        Text("Esgotado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(offer.title, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                PriceRow(offer.price, offer.listPrice, offer.discountPercent)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalIconButton(onClick = onWatch) {
                    Icon(
                        if (watched) Icons.Filled.NotificationsActive else Icons.Filled.Notifications,
                        contentDescription = if (watched) "Monitorando" else "Monitorar preço",
                        tint = if (watched) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { runCatching { uri.openUri(offer.url) } }) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Abrir na loja")
                }
            }
        }
    }
}

@Composable
fun OfferImage(url: String?, fallbackEmoji: String, size: Int) {
    Box(
        Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (url != null) {
            AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Text(fallbackEmoji, fontSize = (size / 2.4).sp)
        }
    }
}

@Composable
fun StoreBadge(name: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            name,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
fun PriceRow(price: Double, listPrice: Double?, discount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(price.toBrl(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        if (listPrice != null && discount > 0) {
            Text(listPrice.toBrl(), style = MaterialTheme.typography.bodySmall, textDecoration = TextDecoration.LineThrough, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(color = DealRed, shape = RoundedCornerShape(6.dp)) {
                Text("-$discount%", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
        }
    }
}

/** Mini gráfico com o histórico de preços. */
@Composable
fun Sparkline(prices: List<Double>, modifier: Modifier = Modifier) {
    if (prices.size < 2) return
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier) {
        val min = prices.min()
        val max = prices.max()
        val range = (max - min).takeIf { it > 0 } ?: 1.0
        val stepX = size.width / (prices.size - 1)
        val path = Path()
        prices.forEachIndexed { i, p ->
            val point = Offset(i * stepX, (size.height * (1 - (p - min) / range)).toFloat())
            if (i == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
        val last = prices.last()
        drawCircle(color, 3.dp.toPx(), Offset(size.width, (size.height * (1 - (last - min) / range)).toFloat()))
    }
}

/** Diálogo para definir o preço alvo ao monitorar um produto. */
@Composable
fun WatchDialog(title: String, currentPrice: Double, initialTarget: Double?, onDismiss: () -> Unit, onConfirm: (Double?) -> Unit) {
    var text by remember { mutableStateOf(initialTarget?.let { "%.2f".format(it).replace('.', ',') } ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Monitorar preço") },
        text = {
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text("Preço atual: ${currentPrice.toBrl()}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                    label = { Text("Avisar quando chegar a (R$)") },
                    placeholder = { Text("Opcional: qualquer queda") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(text.replace(',', '.').toDoubleOrNull()) }) { Text("Monitorar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null) {
    Row(modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        action?.invoke()
    }
}

@Composable
fun ClickableRow(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth().clickable(onClick = onClick)) { content() }
}

/** Texto de uma linha que diminui a fonte até caber no espaço disponível (ex.: rótulos do menu inferior). */
@Composable
fun FitText(text: String, modifier: Modifier = Modifier, style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelMedium) {
    var scale by remember(text) { mutableStateOf(1f) }
    var ready by remember(text) { mutableStateOf(false) }
    Text(
        text,
        modifier = modifier.drawWithContent { if (ready) drawContent() },
        style = style.copy(fontSize = style.fontSize * scale),
        maxLines = 1,
        softWrap = false,
        onTextLayout = { result ->
            if (result.didOverflowWidth && scale > 0.6f) scale *= 0.92f else ready = true
        },
    )
}
