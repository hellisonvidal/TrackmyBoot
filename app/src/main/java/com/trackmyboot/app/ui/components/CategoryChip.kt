package com.trackmyboot.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Chip(
        onClick = onClick,
        selected = isSelected,
        onSelectedChange = null,
        modifier = modifier,
        colors = ChipDefaults.chipColors(
            selectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest,
            selectedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
            labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Medium else androidx.compose.ui.text.font.FontWeight.Normal
        )
    }
}