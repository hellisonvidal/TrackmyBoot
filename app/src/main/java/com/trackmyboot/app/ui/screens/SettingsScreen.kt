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
import androidx.compose.material3.Checkbox
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
import com.trackmyboot.app.domain.model.ProductCategory
import com.trackmyboot.app.domain.model.Store
import com.trackmyboot.app.presentation.viewmodel.LanguageOption
import com.trackmyboot.app.presentation.viewmodel.SettingsViewModel
import com.trackmyboot.app.presentation.viewmodel.ThemeOption
import com.trackmyboot.app.ui.theme.Theme
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val priceCheckInterval by viewModel.priceCheckIntervalHours.collectAsStateWithLifecycle(initialValue = 6)
    val autoSyncEnabled by viewModel.autoSyncEnabled.collectAsStateWithLifecycle(initialValue = true)
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle(initialValue = true)
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle(initialValue = 0)
    val language by viewModel.language.collectAsStateWithLifecycle(initialValue = "pt")
    val enabledCategories by viewModel.enabledCategories.collectAsStateWithLifecycle(initialValue = "")
    val enabledStores by viewModel.enabledStores.collectAsStateWithLifecycle(initialValue = "")
    val minDiscount by viewModel.minDiscountPercentage.collectAsStateWithLifecycle(initialValue = 10)

    val selectedCategories = remember { enabledCategories.split(",").filter { it.isNotBlank() }.toMutableSet() }
    val selectedStores = remember { enabledStores.split(",").filter { it.isNotBlank() }.toMutableSet() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top App Bar
        item {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
            )
        }

        // Notifications section
        item {
            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                SettingRow(
                    title = stringResource(R.string.settings_notifications),
                    subtitle = "Receber alertas de queda de preço e volta ao estoque",
                    trailing = {
                        androidx.compose.material3.Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                        )
                    }
                )
            }
        }

        // Sync section
        item {
            SettingsSection(title = "Sincronização") {
                SettingRow(
                    title = stringResource(R.string.settings_auto_sync),
                    subtitle = "Atualizar preços automaticamente em segundo plano",
                    trailing = {
                        androidx.compose.material3.Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { viewModel.setAutoSyncEnabled(it) }
                        )
                    }
                )
                SettingRow(
                    title = stringResource(R.string.settings_price_check_interval),
                    subtitle = "Verificar preços a cada ${priceCheckInterval} hora(s)",
                    trailing = {
                        androidx.compose.material3.MenuAnchor { anchor ->
                            androidx.compose.material3.DropdownMenu(
                                expanded = true,
                                onDismissRequest = {},
                                anchor = anchor
                            ) {
                                viewModel.priceCheckIntervals.forEach { interval ->
                                    androidx.compose.material3.DropdownMenuItem(
                                        onClick = { viewModel.setPriceCheckInterval(interval) },
                                        text = { Text("${interval}h") }
                                    )
                                }
                            }
                        } {
                            androidx.compose.material3.OutlinedButton(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${priceCheckInterval}h")
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        }

        // Appearance section
        item {
            SettingsSection(title = stringResource(R.string.settings_theme)) {
                SettingRow(
                    title = stringResource(R.string.settings_theme),
                    subtitle = "Escolher tema do aplicativo",
                    trailing = {
                        androidx.compose.material3.MenuAnchor { anchor ->
                            androidx.compose.material3.DropdownMenu(
                                expanded = true,
                                onDismissRequest = {},
                                anchor = anchor
                            ) {
                                viewModel.themeModes.forEach { theme ->
                                    androidx.compose.material3.DropdownMenuItem(
                                        onClick = { viewModel.setThemeMode(theme.value) },
                                        text = { Text(theme.label) }
                                    )
                                }
                            }
                        } {
                            androidx.compose.material3.OutlinedButton(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(viewModel.themeModes.first { it.value == themeMode }.label)
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        }

        // Language section
        item {
            SettingsSection(title = stringResource(R.string.settings_language)) {
                SettingRow(
                    title = stringResource(R.string.settings_language),
                    subtitle = "Idioma do aplicativo",
                    trailing = {
                        androidx.compose.material3.MenuAnchor { anchor ->
                            androidx.compose.material3.DropdownMenu(
                                expanded = true,
                                onDismissRequest = {},
                                anchor = anchor
                            ) {
                                viewModel.languages.forEach { lang ->
                                    androidx.compose.material3.DropdownMenuItem(
                                        onClick = { viewModel.setLanguage(lang.code) },
                                        text = { Text(lang.name) }
                                    )
                                }
                            }
                        } {
                            androidx.compose.material3.OutlinedButton(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(viewModel.languages.first { it.code == language }.name)
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        }

        // Categories filter section
        item {
            SettingsSection(title = "Categorias monitoradas") {
                viewModel.allCategories.forEach { category ->
                    SettingRow(
                        title = category.displayName,
                        subtitle = "Monitorar preços de ${category.displayName.toLowerCase()}",
                        trailing = {
                            Checkbox(
                                checked = selectedCategories.contains(category.name),
                                onCheckedChange = { checked ->
                                    if (checked) selectedCategories.add(category.name) else selectedCategories.remove(category.name)
                                    viewModel.setEnabledCategories(selectedCategories.toList())
                                }
                            )
                        }
                    )
                }
            }
        }

        // Stores filter section
        item {
            SettingsSection(title = "Lojas monitoradas") {
                viewModel.allStores.forEach { store ->
                    SettingRow(
                        title = store.displayName,
                        subtitle = "Buscar ofertas em ${store.displayName}",
                        trailing = {
                            Checkbox(
                                checked = selectedStores.contains(store.name),
                                onCheckedChange = { checked ->
                                    if (checked) selectedStores.add(store.name) else selectedStores.remove(store.name)
                                    viewModel.setEnabledStores(selectedStores.toList())
                                }
                            )
                        }
                    )
                }
            }
        }

        // Minimum discount section
        item {
            SettingsSection(title = "Desconto mínimo") {
                SettingRow(
                    title = stringResource(R.string.settings_min_discount),
                    subtitle = "Só mostrar produtos com desconto acima de ${minDiscount}%",
                    trailing = {
                        androidx.compose.material3.Slider(
                            value = minDiscount.toFloat(),
                            onValueChange = { viewModel.setMinDiscountPercentage(it.roundToInt()) },
                            valueRange = 0f..80f,
                            steps = 8,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                )
            }
        }

        // Data section
        item {
            SettingsSection(title = "Dados") {
                SettingRow(
                    title = stringResource(R.string.settings_clear_cache),
                    subtitle = "Limpar dados em cache e histórico antigo",
                    trailing = {
                        androidx.compose.material3.TextButton(onClick = { viewModel.clearCache() }) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                )
                SettingRow(
                    title = stringResource(R.string.settings_export_data),
                    subtitle = "Exportar sua lista de desejos e histórico",
                    trailing = {
                        androidx.compose.material3.TextButton(onClick = { viewModel.exportData() }) {
                            Text(stringResource(R.string.export))
                        }
                    }
                )
            }
        }

        // About section
        item {
            SettingsSection(title = stringResource(R.string.settings_about)) {
                SettingRow(
                    title = stringResource(R.string.app_name),
                    subtitle = stringResource(R.string.settings_version),
                    trailing = {}
                )
                SettingRow(
                    title = "Política de Privacidade",
                    subtitle = "Como usamos seus dados",
                    trailing = {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                SettingRow(
                    title = "Termos de Uso",
                    subtitle = "Termos e condições do serviço",
                    trailing = {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
            androidx.compose.foundation.layout.Divider()
            content()
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing()
    }
}