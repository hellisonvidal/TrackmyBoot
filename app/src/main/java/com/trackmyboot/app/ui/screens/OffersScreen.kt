package com.trackmyboot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.model.Club
import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.SortOrder
import com.trackmyboot.app.ui.AppViewModel
import com.trackmyboot.app.ui.OffersViewModel
import com.trackmyboot.app.ui.components.ClubCrest
import com.trackmyboot.app.ui.components.OfferCard
import com.trackmyboot.app.ui.components.WatchDialog

/** Lista de ofertas usada nas telas de categoria, clube e busca. */
@Composable
fun OffersContent(
    app: AppViewModel,
    vm: OffersViewModel,
    showCategoryChips: Boolean,
    header: (@Composable () -> Unit)? = null,
    emptyHint: String = "Nenhuma oferta encontrada.",
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val watchedIds by app.watchedIds.collectAsStateWithLifecycle()
    var dialogFor by remember { mutableStateOf<Offer?>(null) }

    Column(Modifier.fillMaxSize()) {
        header?.invoke()
        if (showCategoryChips) {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = state.category == null, onClick = { vm.selectCategory(null) }, label = { Text("Tudo") }) }
                items(Category.entries) { c ->
                    FilterChip(selected = state.category == c, onClick = { vm.selectCategory(c) }, label = { Text("${c.emoji} ${c.label}") })
                }
            }
        }
        Toolbar(
            sort = state.sort, onSort = vm::setSort,
            showDemo = state.showDemo, onShowDemo = vm::setShowDemo,
            onRefresh = { vm.load(force = true) },
            count = state.offers.size,
        )
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        val result = state.result
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (result != null && !state.loading) {
                val failed = result.failedStores.filter { it.store.id != "demo" }
                if (!result.hasRealOffers) {
                    item {
                        InfoCard(
                            if (failed.isNotEmpty()) "Não foi possível obter preços reais agora (${failed.size} loja(s) indisponível(is)). " +
                                "Exibindo ofertas de demonstração quando ativadas." else "As lojas não retornaram resultados para esta busca.",
                        )
                    }
                } else if (failed.isNotEmpty()) {
                    item { InfoCard("Indisponíveis agora: " + failed.joinToString { it.store.name }) }
                }
            }
            items(state.offers, key = { it.id }) { offer ->
                OfferCard(offer, watched = offer.id in watchedIds, onWatch = {
                    if (offer.id in watchedIds) app.unwatch(offer.id) else dialogFor = offer
                })
            }
            if (result != null && !state.loading && state.offers.isEmpty()) {
                item { Text(emptyHint, Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center) }
            }
            if (result == null && state.loading) {
                item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            }
        }
    }

    dialogFor?.let { offer ->
        WatchDialog(offer.title, offer.price, null, onDismiss = { dialogFor = null }) { target ->
            app.watch(offer, target)
            dialogFor = null
        }
    }
}

@Composable
private fun Toolbar(sort: SortOrder, onSort: (SortOrder) -> Unit, showDemo: Boolean, onShowDemo: (Boolean) -> Unit, onRefresh: () -> Unit, count: Int) {
    var menu by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("$count ofertas", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
        Text("Demo", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.width(4.dp))
        Switch(checked = showDemo, onCheckedChange = onShowDemo)
        Box {
            IconButton(onClick = { menu = true }) { Icon(Icons.Filled.SwapVert, contentDescription = "Ordenar") }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                SortOrder.entries.forEach { s ->
                    DropdownMenuItem(text = { Text((if (s == sort) "✓ " else "") + s.label) }, onClick = { onSort(s); menu = false })
                }
            }
        }
        IconButton(onClick = onRefresh) { Icon(Icons.Filled.Refresh, contentDescription = "Atualizar preços") }
    }
}

@Composable
private fun InfoCard(text: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Text(text, Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ClubHeader(club: Club, favorite: Boolean, onToggle: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        ClubCrest(club, 52)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(club.name, style = MaterialTheme.typography.titleLarge)
            Text(club.country, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onToggle) {
            Icon(if (favorite) Icons.Filled.Star else Icons.Filled.StarBorder, contentDescription = "Torço por este clube", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
