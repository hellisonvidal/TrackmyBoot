package com.trackmyboot.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmyboot.app.R
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.model.Club
import com.trackmyboot.app.ui.AppViewModel
import com.trackmyboot.app.ui.components.CategoryTile
import com.trackmyboot.app.ui.components.ClubCrest
import com.trackmyboot.app.ui.components.SectionTitle
import com.trackmyboot.app.ui.theme.PitchGreen
import com.trackmyboot.app.ui.theme.PitchGreenDark

@Composable
fun HomeScreen(
    app: AppViewModel,
    onCategory: (Category) -> Unit,
    onClub: (Club) -> Unit,
    onPickClubs: () -> Unit,
) {
    val favorites by app.favoriteClubs.collectAsStateWithLifecycle()
    val watched by app.watched.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { Hero(watched.size, watched.count { it.currentPrice < it.initialPrice }) }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTitle("Meus clubes", action = { TextButton(onClick = onPickClubs) { Text(if (favorites.isEmpty()) "Escolher" else "Editar") } })
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            if (favorites.isEmpty()) {
                AssistChip(onClick = onPickClubs, label = { Text("Escolha o clube para quem você torce") }, leadingIcon = { Icon(Icons.Filled.Add, null) })
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(favorites, key = { it.id }) { club ->
                        Column(
                            Modifier.width(72.dp).clip(RoundedCornerShape(12.dp)).clickable { onClub(club) }.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            ClubCrest(club, 56)
                            Spacer(Modifier.height(4.dp))
                            Text(club.name, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { SectionTitle("Categorias") }
        items(Category.entries, key = { it.name }) { category ->
            CategoryTile(category, onClick = { onCategory(category) })
        }
    }
}

@Composable
private fun Hero(watchedCount: Int, droppedCount: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(PitchGreen, PitchGreenDark)))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(72.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("TrackmyBoot", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("Ofertas de futebol em várias lojas, num lugar só.", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                if (watchedCount > 0) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "$watchedCount monitorado(s) · $droppedCount com preço menor",
                        color = Color(0xFFC6F135),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
