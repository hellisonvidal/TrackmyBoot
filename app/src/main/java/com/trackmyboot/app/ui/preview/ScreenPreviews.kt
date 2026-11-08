package com.trackmyboot.app.ui.preview

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmyboot.app.R
import com.trackmyboot.app.domain.model.Alert
import com.trackmyboot.app.domain.model.AlertType
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.ProductCategory
import com.trackmyboot.app.domain.model.Store
import com.trackmyboot.app.ui.components.AsyncImage
import com.trackmyboot.app.ui.components.CategoryChip
import com.trackmyboot.app.ui.components.ProductCard
import com.trackmyboot.app.ui.theme.Theme
import java.util.Date
import kotlinx.coroutines.flow.flowOf

// ============================================================
// DADOS MOCK PARA PREVIEWS
// ============================================================

private val mockProducts = listOf(
    Product(
        id = "1",
        name = "Camisa Brasil 2024 Home",
        brand = "Nike",
        category = ProductCategory.JERSEYS,
        description = "Camisa oficial da Seleção Brasileira",
        imageUrl = "https://picsum.photos/seed/brasil2024/400/400.jpg",
        productUrl = "https://www.nike.com.br/camisa-brasil",
        store = Store.NIKE,
        currentPrice = 299.90,
        originalPrice = 349.90,
        discountPercentage = 14,
        sizes = listOf("P", "M", "G", "GG"),
        colors = listOf("Amarelo", "Azul"),
        tags = listOf("futebol", "camisa", "brasil", "nike"),
        rating = 4.8,
        reviewCount = 1245,
        lastUpdated = Date(),
        createdAt = Date()
    ),
    Product(
        id = "2",
        name = "Bola Oficial Copa do Mundo",
        brand = "Adidas",
        category = ProductCategory.BALLS,
        description = "Bola oficial FIFA Quality Pro",
        imageUrl = "https://picsum.photos/seed/bolacopa/400/400.jpg",
        productUrl = "https://www.adidas.com.br/bola-copa",
        store = Store.ADIDAS,
        currentPrice = 189.90,
        originalPrice = 249.90,
        discountPercentage = 24,
        colors = listOf("Branco", "Colorido"),
        tags = listOf("futebol", "bola", "copa", "adidas"),
        rating = 4.9,
        reviewCount = 856,
        lastUpdated = Date(),
        createdAt = Date()
    ),
    Product(
        id = "3",
        name = "Chuteira Mercurial Vapor 15",
        brand = "Nike",
        category = ProductCategory.CLEATS,
        description = "Chuteira de campo profissional",
        imageUrl = "https://picsum.photos/seed/mercurial/400/400.jpg",
        productUrl = "https://www.nike.com.br/mercurial",
        store = Store.NETSHOES,
        currentPrice = 899.90,
        originalPrice = 1199.90,
        discountPercentage = 25,
        sizes = listOf("38", "39", "40", "41", "42", "43", "44"),
        colors = listOf("Rosa", "Preto", "Azul"),
        tags = listOf("futebol", "chuteira", "mercurial", "nike"),
        rating = 4.7,
        reviewCount = 2100,
        lastUpdated = Date(),
        createdAt = Date()
    ),
    Product(
        id = "4",
        name = "Meias Grip Antiderrapante",
        brand = "Trusox",
        category = ProductCategory.SOCKS,
        description = "Meias de performance com grip",
        imageUrl = "https://picsum.photos/seed/meiasgrip/400/400.jpg",
        productUrl = "https://www.trusox.com.br/grip",
        store = Store.CENTAURO,
        currentPrice = 89.90,
        originalPrice = 119.90,
        discountPercentage = 25,
        sizes = listOf("35-38", "39-42", "43-46"),
        colors = listOf("Preto", "Branco", "Azul"),
        tags = listOf("futebol", "meias", "grip", "performance"),
        rating = 4.6,
        reviewCount = 432,
        lastUpdated = Date(),
        createdAt = Date()
    ),
    Product(
        id = "5",
        name = "Luvas Goleiro Profissional",
        brand = "Reusch",
        category = ProductCategory.GLOVES,
        description = "Luvas com látex alemão 4mm",
        imageUrl = "https://picsum.photos/seed/luvasgk/400/400.jpg",
        productUrl = "https://www.reusch.com.br/luvas-pro",
        store = Store.FUTFANATICS,
        currentPrice = 249.90,
        originalPrice = 329.90,
        discountPercentage = 24,
        sizes = listOf("7", "8", "9", "10", "11"),
        colors = listOf("Preto/Amarelo", "Azul/Laranja"),
        tags = listOf("goleiro", "luvas", "reusch", "profissional"),
        rating = 4.8,
        reviewCount = 678,
        lastUpdated = Date(),
        createdAt = Date()
    ),
    Product(
        id = "6",
        name = "Bolsa Esportiva Grande",
        brand = "Puma",
        category = ProductCategory.BAGS,
        description = "Bolsa com compartimento para chuteira",
        imageUrl = "https://picsum.photos/seed/bolsaesportiva/400/400.jpg",
        productUrl = "https://www.puma.com.br/bolsa-grande",
        store = Store.KANUI,
        currentPrice = 179.90,
        originalPrice = 229.90,
        discountPercentage = 22,
        colors = listOf("Preto", "Cinza", "Azul Marinho"),
        tags = listOf("bolsa", "esporte", "puma", "academia"),
        rating = 4.5,
        reviewCount = 321,
        lastUpdated = Date(),
        createdAt = Date()
    ),
    Product(
        id = "7",
        name = "Caneleira Carbono Leve",
        brand = "Nike",
        category = ProductCategory.SHIN_GUARDS,
        description = "Caneleira de carbono ultraleve",
        imageUrl = "https://picsum.photos/seed/caneleira/400/400.jpg",
        productUrl = "https://www.nike.com.br/caneleira-carbono",
        store = Store.AMAZON,
        currentPrice = 129.90,
        originalPrice = 169.90,
        discountPercentage = 24,
        sizes = listOf("P", "M", "G"),
        colors = listOf("Preto", "Branco"),
        tags = listOf("futebol", "caneleira", "protecao", "nike"),
        rating = 4.7,
        reviewCount = 543,
        lastUpdated = Date(),
        createdAt = Date()
    ),
    Product(
        id = "8",
        name = "Camisa Flamengo 2024 Home",
        brand = "Adidas",
        category = ProductCategory.JERSEYS,
        description = "Manto sagrado versão torcedor",
        imageUrl = "https://picsum.photos/seed/flamengo2024/400/400.jpg",
        productUrl = "https://www.loja.flamengo.com.br/camisa",
        store = Store.LOJA_FLAMENGO,
        currentPrice = 279.90,
        originalPrice = 329.90,
        discountPercentage = 15,
        sizes = listOf("P", "M", "G", "GG", "XG"),
        colors = listOf("Vermelho/Preto"),
        tags = listOf("futebol", "camisa", "flamengo", "adidas"),
        rating = 4.9,
        reviewCount = 3421,
        lastUpdated = Date(),
        createdAt = Date()
    ),
)

private val mockCategories = ProductCategory.values().toList()

private val mockAlerts = listOf(
    Alert(
        id = 1,
        productId = "1",
        productName = "Camisa Brasil 2024 Home",
        productImageUrl = "https://picsum.photos/seed/brasil2024/400/400.jpg",
        store = "Nike",
        previousPrice = 349.90,
        currentPrice = 299.90,
        alertType = AlertType.PRICE_DROP,
        message = "Camisa Brasil 2024 Home caiu de R$ 349,90 para R$ 299,90",
        isRead = false,
        createdAt = Date(System.currentTimeMillis() - 3600000)
    ),
    Alert(
        id = 2,
        productName = "Chuteira Mercurial Vapor 15",
        productId = "3",
        productImageUrl = "https://picsum.photos/seed/mercurial/400/400.jpg",
        store = "Netshoes",
        previousPrice = 1199.90,
        currentPrice = 899.90,
        targetPrice = 900.0,
        alertType = AlertType.TARGET_PRICE_REACHED,
        message = "Chuteira Mercurial Vapor 15 atingiu seu preço alvo de R$ 900,00!",
        isRead = false,
        createdAt = Date(System.currentTimeMillis() - 7200000)
    ),
    Alert(
        id = 3,
        productName = "Bola Oficial Copa do Mundo",
        productId = "2",
        productImageUrl = "https://picsum.photos/seed/bolacopa/400/400.jpg",
        store = "Adidas",
        previousPrice = 249.90,
        currentPrice = 189.90,
        alertType = AlertType.NEW_LOWEST_PRICE,
        message = "Novo menor preço histórico para Bola Oficial Copa do Mundo!",
        isRead = true,
        createdAt = Date(System.currentTimeMillis() - 86400000)
    ),
)

// ============================================================
// PREVIEW: HOME SCREEN
// ============================================================

@Composable
@Preview(name = "Home Screen", showBackground = true, showSystemUi = true)
fun PreviewHomeScreen() {
    Theme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                TopAppBar(
                    title = { Text("Ofertas de Futebol", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                    actions = {
                        IconButton(onClick = {}) { Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = "Buscar") }
                        IconButton(onClick = {}) { Icon(androidx.compose.material.icons.Icons.Default.FavoriteBorder, contentDescription = "Wishlist") }
                        IconButton(onClick = {}) { Icon(androidx.compose.material.icons.Icons.Default.NotificationsNone, contentDescription = "Alertas") }
                        IconButton(onClick = {}) { Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "Configurações") }
                    }
                )

                // Search Bar
                androidx.compose.material3.TextField(
                    value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth().padding(16.dp),
                    readOnly = true, placeholder = { Text("Buscar camisas, bolas, chuteiras...") },
                    leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = null) }
                )

                // Category Chips
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(label = "Todas", isSelected = true, onClick = {})
                    mockCategories.forEach { cat ->
                        CategoryChip(label = cat.displayName, isSelected = false, onClick = {})
                    }
                }

                // Products on Sale Section
                androidx.compose.foundation.layout.Text(
                    text = "Melhores Ofertas", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                LazyVerticalGrid(
                    cells = androidx.compose.foundation.lazy.GridCells.Fixed(2),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockProducts.take(4)) { product ->
                        ProductCard(product = product, onClick = {})
                    }
                }

                // All Products Section
                androidx.compose.foundation.layout.Text(
                    text = "Todos os Produtos", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                LazyVerticalGrid(
                    cells = androidx.compose.foundation.lazy.GridCells.Fixed(2),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockProducts) { product ->
                        ProductCard(product = product, onClick = {})
                    }
                }
            }
        }
    }
}

// ============================================================
// PREVIEW: SEARCH SCREEN
// ============================================================

@Composable
@Preview(name = "Search Screen", showBackground = true, showSystemUi = true)
fun PreviewSearchScreen() {
    Theme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = {
                        androidx.compose.material3.TextField(
                            value = "Mercurial", onValueChange = {}, modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar camisas, bolas, chuteiras...") },
                            leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = null) }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Category Chips
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(label = "Todas", isSelected = true, onClick = {})
                    mockCategories.forEach { cat ->
                        CategoryChip(label = cat.displayName, isSelected = false, onClick = {})
                    }
                }

                // Sort Chips
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Relevância", "Menor Preço", "Maior Preço", "Maior Desconto", "Mais Recentes").forEach { label ->
                        CategoryChip(label = label, isSelected = label == "Relevância", onClick = {})
                    }
                }

                // Results
                LazyVerticalGrid(
                    cells = androidx.compose.foundation.lazy.GridCells.Fixed(2),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockProducts) { product ->
                        ProductCard(product = product, onClick = {})
                    }
                }
            }
        }
    }
}

// ============================================================
// PREVIEW: WISHLIST SCREEN
// ============================================================

@Composable
@Preview(name = "Wishlist Screen", showBackground = true, showSystemUi = true)
fun PreviewWishlistScreen() {
    Theme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Minha Lista de Desejos", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                androidx.compose.foundation.layout.Text(
                    text = "8 itens na lista", fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )

                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockProducts.take(3)) { product ->
                        WishlistItemPreview(product = product)
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistItemPreview(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = product.imageUrl, contentDescription = product.name, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)))
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.width(12.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = product.brand, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "R$ ${product.formattedCurrentPrice}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        if (product.isOnSale) Text(text = "R$ ${product.formattedOriginalPrice}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                    }
                }
                IconButton(onClick = {}) { Icon(androidx.compose.material.icons.Icons.Default.DeleteOutline, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error) }
            }

            // Target price + notifications
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.material3.OutlinedTextField(
                    value = "R$ 250,00", onValueChange = {}, modifier = Modifier.width(120.dp),
                    singleLine = true, label = { Text("Preço alvo") },
                    keyboardOptions = androidx.compose.ui.platform.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Checkbox(checked = true, onCheckedChange = {}) { Text("Queda de preço", fontSize = 12.sp) }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Checkbox(checked = true, onCheckedChange = {}) { Text("Volta ao estoque", fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

// ============================================================
// PREVIEW: ALERTS SCREEN
// ============================================================

@Composable
@Preview(name = "Alerts Screen", showBackground = true, showSystemUi = true)
fun PreviewAlertsScreen() {
    Theme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Alertas de Preço", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                    actions = { androidx.compose.material3.TextButton(onClick = {}) { Text("Marcar todos como lidos") } }
                )

                // Unread badge
                androidx.compose.material3.Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "3 alertas não lidos", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Medium)
                    }
                }

                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockAlerts) { alert ->
                        AlertCardPreview(alert = alert)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCardPreview(alert: Alert) {
    val alertColor = when (alert.alertType) {
        AlertType.PRICE_DROP -> MaterialTheme.colorScheme.success
        AlertType.TARGET_PRICE_REACHED -> MaterialTheme.colorScheme.secondary
        AlertType.BACK_IN_STOCK -> MaterialTheme.colorScheme.primary
        AlertType.NEW_LOWEST_PRICE -> MaterialTheme.colorScheme.tertiary
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = if (!alert.isRead) alertColor.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.foundation.layout.Box(modifier = Modifier.size(8.dp).background(alertColor).clip(androidx.compose.foundation.shape.CircleShape))
                    Text(text = alert.alertType.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = alertColor)
                }
                Text(text = "2h atrás", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(model = alert.productImageUrl, contentDescription = alert.productName, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = alert.productName, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "R$ ${alert.formattedCurrentPrice}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        if (alert.previousPrice > alert.currentPrice) {
                            Text(text = "R$ ${alert.formattedPreviousPrice}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                            androidx.compose.material3.Badge(backgroundColor = alertColor) { androidx.compose.material3.Text(text = "-${alert.priceDropPercentage}%", color = Color.White, fontSize = 10.sp) }
                        }
                    }
                }
                if (!alert.isRead) {
                    IconButton(onClick = {}) { Icon(androidx.compose.material.icons.Icons.Default.Check, contentDescription = "Marcar como lido", tint = alertColor) }
                }
            }
            Text(text = alert.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

// ============================================================
// PREVIEW: SETTINGS SCREEN
// ============================================================

@Composable
@Preview(name = "Settings Screen", showBackground = true, showSystemUi = true)
fun PreviewSettingsScreen() {
    Theme {
        Surface {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Top App Bar
                item {
                    TopAppBar(
                        title = { Text("Configurações", fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                }

                // Notifications
                item { SettingsSectionPreview(title = "Notificações") {
                    SettingRowPreview(title = "Notificações de Preço", subtitle = "Receber alertas de queda de preço e volta ao estoque") {
                        Switch(checked = true, onCheckedChange = {})
                    }
                }}

                // Sync
                item { SettingsSectionPreview(title = "Sincronização") {
                    SettingRowPreview(title = "Sincronização automática", subtitle = "Atualizar preços automaticamente em segundo plano") {
                        Switch(checked = true, onCheckedChange = {})
                    }
                    SettingRowPreview(title = "Intervalo de verificação", subtitle = "Verificar preços a cada 6 hora(s)") {
                        androidx.compose.material3.OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                            Text("6h")
                            Icon(androidx.compose.material.icons.Icons.Default.ExpandMore, contentDescription = null)
                        }
                    }
                }}

                // Theme
                item { SettingsSectionPreview(title = "Aparência") {
                    SettingRowPreview(title = "Tema", subtitle = "Escolher tema do aplicativo") {
                        androidx.compose.material3.OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                            Text("Sistema")
                            Icon(androidx.compose.material.icons.Icons.Default.ExpandMore, contentDescription = null)
                        }
                    }
                }}

                // Categories
                item { SettingsSectionPreview(title = "Categorias monitoradas") {
                    ProductCategory.values().forEach { cat ->
                        SettingRowPreview(title = cat.displayName, subtitle = "Monitorar preços de ${cat.displayName.toLowerCase()}") {
                            Checkbox(checked = true, onCheckedChange = {})
                        }
                    }
                }}

                // Stores
                item { SettingsSectionPreview(title = "Lojas monitoradas") {
                    listOf("Netshoes", "Centauro", "Nike", "Adidas", "Amazon", "Mercado Livre").forEach { store ->
                        SettingRowPreview(title = store, subtitle = "Buscar ofertas em $store") {
                            Checkbox(checked = true, onCheckedChange = {})
                        }
                    }
                }}

                // Min Discount
                item { SettingsSectionPreview(title = "Desconto mínimo") {
                    SettingRowPreview(title = "Desconto mínimo", subtitle = "Só mostrar produtos com desconto acima de 15%") {
                        Slider(value = 15f, onValueChange = {}, valueRange = 0f..80f, steps = 8, modifier = Modifier.width(150.dp))
                    }
                }}

                // Data
                item { SettingsSectionPreview(title = "Dados") {
                    SettingRowPreview(title = "Limpar cache", subtitle = "Limpar dados em cache e histórico antigo") {
                        TextButton(onClick = {}) { Text("Limpar") }
                    }
                    SettingRowPreview(title = "Exportar dados", subtitle = "Exportar sua lista de desejos e histórico") {
                        TextButton(onClick = {}) { Text("Exportar") }
                    }
                }}

                // About
                item { SettingsSectionPreview(title = "Sobre") {
                    SettingRowPreview(title = "TrackmyBoot", subtitle = "Versão 1.0.0") {}
                    SettingRowPreview(title = "Política de Privacidade", subtitle = "Como usamos seus dados") {
                        Icon(androidx.compose.material.icons.Icons.Default.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    SettingRowPreview(title = "Termos de Uso", subtitle = "Termos e condições do serviço") {
                        Icon(androidx.compose.material.icons.Icons.Default.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }}
            }
        }
    }
}

@Composable
fun SettingsSectionPreview(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            androidx.compose.foundation.layout.Divider()
            content()
        }
    }
}

@Composable
fun SettingRowPreview(title: String, subtitle: String, trailing: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing()
    }
}

// ============================================================
// PREVIEW: DETAIL SCREEN
// ============================================================

@Composable
@Preview(name = "Detail Screen", showBackground = true, showSystemUi = true)
fun PreviewDetailScreen() {
    val product = mockProducts[0]
    Theme {
        Surface {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Hero Image
                item {
                    ProductImagePreview(product = product, isInWatchlist = true)
                }

                // Price Info
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(text = "R$ ${product.formattedCurrentPrice}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    if (product.isOnSale) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(text = "R$ ${product.formattedOriginalPrice}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                            androidx.compose.material3.Badge(backgroundColor = MaterialTheme.colorScheme.error) { Text("-${product.discountPercentage}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                        }
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(androidx.compose.material.icons.Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF388E3C))
                                        Text(text = "Em estoque", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF388E3C))
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(androidx.compose.material.icons.Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Text(text = "${product.rating} (${product.reviewCount})", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            if (product.sizes.isNotEmpty()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "Tamanhos:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        product.sizes.forEach { size ->
                                            androidx.compose.material3.Chip(
                                                onClick = {}, label = { Text(size, fontSize = 12.sp) },
                                                colors = androidx.compose.material3.ChipDefaults.chipColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Price History Chart
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Histórico de Preços", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Mín: R$ 279,90 | Máx: R$ 349,90", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            // Chart placeholder
                            androidx.compose.foundation.Canvas(
                                modifier = Modifier.fillMaxWidth().height(200.dp).background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            ) {
                                // Draw simple chart placeholder
                                drawRect(color = MaterialTheme.colorScheme.outlineVariant, topLeft = androidx.compose.ui.geometry.Offset(40f, 40f), size = androidx.compose.ui.geometry.Size(size.width - 80f, 160f))
                            }
                            Row(horizontalArrangement = Arrangement.SpaceAround) {
                                StatItemPreview(label = "Menor", value = "R$ 279,90")
                                StatItemPreview(label = "Maior", value = "R$ 349,90")
                                StatItemPreview(label = "Média", value = "R$ 314,90")
                                StatItemPreview(label = "Economia", value = "R$ 70,00 (20%)", valueColor = MaterialTheme.colorScheme.success)
                            }
                        }
                    }
                }

                // Watchlist Settings
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Configurações de Alerta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(text = "Preço alvo:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                androidx.compose.material3.OutlinedTextField(value = "R$ 250,00", onValueChange = {}, modifier = Modifier.width(140.dp), singleLine = true, keyboardOptions = androidx.compose.ui.platform.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number), placeholder = { Text("R$ 0,00") }, label = { Text("Preço alvo") })
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Checkbox(checked = true, onCheckedChange = {}) { Text("Queda de preço", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer) }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Checkbox(checked = true, onCheckedChange = {}) { Text("Volta ao estoque", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer) }
                                }
                            }
                        }
                    }
                }

                // Product Details
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Detalhes do Produto", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = product.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Store Link
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Comprar na ${product.store.displayName}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Icon(androidx.compose.material.icons.Icons.Default.ArrowForward, contentDescription = null)
                            }
                            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                                Text("Abrir na loja", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductImagePreview(product: Product, isInWatchlist: Boolean) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxWidth().height(280.dp)
    ) {
        AsyncImage(model = product.imageUrl, contentDescription = product.name, contentScale = androidx.compose.ui.layout.ContentScale.Crop, modifier = Modifier.fillMaxSize())
        // Gradient overlay
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.7f))))
        )
        // Bottom info
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = product.brand, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(text = product.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 2)
                }
                androidx.compose.material3.Surface(
                    color = if (isInWatchlist) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = if (isInWatchlist) androidx.compose.material.icons.Icons.Default.Favorite else androidx.compose.material.icons.Icons.Default.FavoriteBorder, contentDescription = if (isInWatchlist) "Remover da lista" else "Adicionar à lista", tint = if (isInWatchlist) Color.White else MaterialTheme.colorScheme.primary)
                        Text(text = if (isInWatchlist) "Remover da lista" else "Adicionar à lista", color = if (isInWatchlist) Color.White else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        // Top badges
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth().height(56.dp).background(androidx.compose.ui.graphics.Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)))
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                if (product.isOnSale) {
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(top = 8.dp)) {
                        androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.error, shape = RoundedCornerShape(12.dp)) {
                            Text(text = "-${product.discountPercentage}%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }
                androidx.compose.foundation.layout.Box(modifier = Modifier.padding(top = 8.dp)) {
                    androidx.compose.material3.Surface(color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(12.dp)) {
                        Text(text = product.store.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatItemPreview(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ============================================================
// PREVIEW: PRODUCT CARD (Isolado)
// ============================================================

@Composable
@Preview(name = "Product Card", showBackground = true, widthDp = 180, heightDp = 280)
fun PreviewProductCard() {
    Theme {
        Surface {
            ProductCard(product = mockProducts[0], onClick = {})
        }
    }
}

// ============================================================
// PREVIEW: CATEGORY CHIPS
// ============================================================

@Composable
@Preview(name = "Category Chips", showBackground = true, widthDp = 360, heightDp = 120)
fun PreviewCategoryChips() {
    Theme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(label = "Todas", isSelected = true, onClick = {})
                    mockCategories.take(3).forEach { cat ->
                        CategoryChip(label = cat.displayName, isSelected = false, onClick = {})
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    mockCategories.drop(3).take(3).forEach { cat ->
                        CategoryChip(label = cat.displayName, isSelected = false, onClick = {})
                    }
                }
            }
        }
    }
}