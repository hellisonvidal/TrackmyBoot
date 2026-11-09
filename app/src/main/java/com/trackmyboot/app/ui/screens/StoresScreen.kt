package com.trackmyboot.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmyboot.app.data.catalog.ClubCatalog
import com.trackmyboot.app.data.model.StoreKind
import com.trackmyboot.app.data.model.StorePlatform
import com.trackmyboot.app.ui.AppViewModel

@Composable
fun StoresScreen(app: AppViewModel) {
    val settings by app.settings.collectAsStateWithLifecycle()
    val stores by app.stores.collectAsStateWithLifecycle()
    var adding by remember { mutableStateOf(false) }

    LazyColumn {
        item {
            ListItem(
                headlineContent = { Text("Avisos de queda de preço") },
                supportingContent = { Text("Notificações dos produtos monitorados") },
                trailingContent = { Switch(checked = settings.notificationsEnabled, onCheckedChange = app::setNotifications) },
            )
            HorizontalDivider()
        }
        StoreKind.entries.forEach { kind ->
            val group = stores.filter { it.kind == kind }
            if (group.isNotEmpty()) {
                item(key = "header-$kind") {
                    Text(kind.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                }
                items(group, key = { it.id }) { store ->
                    val custom = settings.customStores.firstOrNull { it.id == store.id }
                    val club = custom?.clubId?.let(ClubCatalog::find)
                    ListItem(
                        headlineContent = { Text(store.name) },
                        supportingContent = {
                            Text(
                                listOfNotNull(
                                    store.baseUrl.removePrefix("https://").takeIf { store.platform != StorePlatform.DEMO },
                                    platformLabel(store.platform),
                                    club?.let { "Clube: ${it.name}" },
                                ).joinToString(" · "),
                            )
                        },
                        trailingContent = {
                            androidx.compose.foundation.layout.Row {
                                if (custom != null) {
                                    IconButton(onClick = { app.removeCustomStore(store.id) }) { Icon(Icons.Filled.Delete, contentDescription = "Remover loja") }
                                }
                                Switch(checked = store.id !in settings.disabledStores, onCheckedChange = { app.setStoreEnabled(store.id, it) })
                            }
                        },
                    )
                }
            }
        }
        item {
            Column(Modifier.padding(16.dp)) {
                FilledTonalButton(onClick = { adding = true }) {
                    Icon(Icons.Filled.Add, null)
                    Text("Adicionar loja (ex.: loja oficial do seu clube)")
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Lojas em VTEX ou Shopify podem ser adicionadas pelo endereço do site. " +
                        "Se uma loja bloquear o acesso automático, ela aparece como indisponível na busca.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    if (adding) AddStoreDialog(onDismiss = { adding = false }) { name, url, platform, clubId ->
        app.addCustomStore(name, url, platform, clubId)
        adding = false
    }
}

private fun platformLabel(p: StorePlatform) = when (p) {
    StorePlatform.VTEX -> "VTEX"
    StorePlatform.SHOPIFY -> "Shopify"
    StorePlatform.MERCADO_LIVRE -> "API Mercado Livre"
    StorePlatform.DEMO -> "Dados fictícios para testes"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStoreDialog(onDismiss: () -> Unit, onAdd: (String, String, StorePlatform, String?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf(StorePlatform.VTEX) }
    var clubId by remember { mutableStateOf<String?>(null) }
    var clubMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova loja") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Nome") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(url, { url = it }, label = { Text("Endereço (ex.: www.loja.com.br)") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                androidx.compose.foundation.layout.Row {
                    listOf(StorePlatform.VTEX, StorePlatform.SHOPIFY).forEach { p ->
                        FilterChip(selected = platform == p, onClick = { platform = p }, label = { Text(platformLabel(p)) }, modifier = Modifier.padding(end = 8.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = clubMenu, onExpandedChange = { clubMenu = it }) {
                    OutlinedTextField(
                        value = clubId?.let { ClubCatalog.find(it)?.name } ?: "Nenhum (loja geral)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Loja oficial de qual clube?") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(clubMenu) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = clubMenu, onDismissRequest = { clubMenu = false }) {
                        DropdownMenuItem(text = { Text("Nenhum (loja geral)") }, onClick = { clubId = null; clubMenu = false })
                        ClubCatalog.clubs.forEach { c ->
                            DropdownMenuItem(text = { Text(c.name) }, onClick = { clubId = c.id; clubMenu = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank() && url.contains('.'), onClick = { onAdd(name, url, platform, clubId) }) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
