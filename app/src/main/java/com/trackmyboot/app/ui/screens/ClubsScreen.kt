package com.trackmyboot.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmyboot.app.data.model.Club
import com.trackmyboot.app.ui.AppViewModel
import com.trackmyboot.app.ui.ClubsViewModel
import com.trackmyboot.app.ui.components.ClubCrest

@Composable
fun ClubsScreen(app: AppViewModel, vm: ClubsViewModel, onClub: (Club) -> Unit) {
    val query by vm.query.collectAsStateWithLifecycle()
    val clubs by vm.clubs.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { vm.query.value = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Buscar clube ou seleção (ex.: Galo, Timão)") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            singleLine = true,
        )
        Text(
            "Toque na estrela para marcar os clubes que você torce.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyColumn {
            items(clubs, key = { it.first.id }) { (club, favorite) ->
                ListItem(
                    modifier = Modifier.clickable { onClub(club) },
                    leadingContent = { ClubCrest(club) },
                    headlineContent = { Text(club.name) },
                    supportingContent = { Text(club.country) },
                    trailingContent = {
                        IconButton(onClick = { app.toggleFavorite(club) }) {
                            Icon(
                                if (favorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = if (favorite) "Remover dos meus clubes" else "Adicionar aos meus clubes",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                )
            }
        }
    }
}
