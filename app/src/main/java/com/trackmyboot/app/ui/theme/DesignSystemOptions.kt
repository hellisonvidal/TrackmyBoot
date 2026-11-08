package com.trackmyboot.app.ui.theme

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 5 OPÇÕES DE DESIGN SYSTEM - Escolha uma para usar no app
 *
 * Para aplicar: substitua o conteúdo de Color.kt pelo tema escolhido
 * e atualize o Theme.kt conforme necessário.
 */

// ============================================================
// OPÇÃO 1: "CAMPO VERDE" - Atual (Futebol Clássico)
// Verde gramado + Amarelo ouro - Tradicional, confiável
// ============================================================
object Option1_CampoVerde {
    val Light = lightColorScheme(
        primary = Color(0xFF1B5E20),      // Verde escuro gramado
        primaryContainer = Color(0xFFC8E6C9),
        secondary = Color(0xFFF9A825),    // Amarelo ouro
        secondaryContainer = Color(0xFFFFF3E0),
        tertiary = Color(0xFF00695C),
        surface = Color.White,
        surfaceContainerHighest = Color(0xFFF5F5F5),
        error = Color(0xFFD32F2F),
        outline = Color(0xFFE0E0E0),
        outlineVariant = Color(0xFFBDBDBD),
    )
    val Dark = darkColorScheme(
        primary = Color(0xFF81C784),
        primaryContainer = Color(0xFF1B5E20),
        secondary = Color(0xFFFFB74D),
        surface = Color(0xFF1E1E1E),
    )
}

// ============================================================
// OPÇÃO 2: "NOITE DE ESTÁDIO" - Dark Moderno
// Azul petróleo + Laranja vibrante - Premium, noturno
// ============================================================
object Option2_NoiteEstadio {
    val Light = lightColorScheme(
        primary = Color(0xFF0D47A1),      // Azul petróleo profundo
        primaryContainer = Color(0xFFBBDEFB),
        secondary = Color(0xFFFF6D00),    // Laranja vibrante
        secondaryContainer = Color(0xFFFFE0B2),
        tertiary = Color(0xFF00ACC1),
        surface = Color.White,
        surfaceContainerHighest = Color(0xFFF5F5F5),
        error = Color(0xFFD32F2F),
        outline = Color(0xFFE0E0E0),
        outlineVariant = Color(0xFFBDBDBD),
    )
    val Dark = darkColorScheme(
        primary = Color(0xFF64B5F6),
        primaryContainer = Color(0xFF0D47A1),
        secondary = Color(0xFFFFB74D),
        surface = Color(0xFF121212),
    )
}

// ============================================================
// OPÇÃO 3: "GRAMADO PREMIUM" - Elegante Minimalista
// Verde musgo + Dourado suave - Sofisticado, clean
// ============================================================
object Option3_GramadoPremium {
    val Light = lightColorScheme(
        primary = Color(0xFF2E7D32),      // Verde musgo elegante
        primaryContainer = Color(0xFFDCEDC8),
        secondary = Color(0xFFF57F17),    // Dourado suave
        secondaryContainer = Color(0xFFFFF8E1),
        tertiary = Color(0xFF558B2F),
        surface = Color(0xFFFAFAFA),
        surfaceContainerHighest = Color(0xFFF1F8E9),
        error = Color(0xFFC62828),
        outline = Color(0xFFE8EAF6),
        outlineVariant = Color(0xFFDCE4EC),
    )
    val Dark = darkColorScheme(
        primary = Color(0xFFA5D6A7),
        primaryContainer = Color(0xFF2E7D32),
        secondary = Color(0xFFFFF176),
        surface = Color(0xFF1B1B1B),
    )
}

// ============================================================
// OPÇÃO 4: "TORCIDA VIBRANTE" - Energético Jovem
// Vermelho paixão + Azul royal - Dinâmico, apaixonado
// ============================================================
object Option4_TorcidaVibrante {
    val Light = lightColorScheme(
        primary = Color(0xFFC62828),      // Vermelho paixão
        primaryContainer = Color(0xFFFFCDD2),
        secondary = Color(0xFF1565C0),    // Azul royal
        secondaryContainer = Color(0xFFBBDEFB),
        tertiary = Color(0xFFEF6C00),
        surface = Color.White,
        surfaceContainerHighest = Color(0xFFFBE9E7),
        error = Color(0xFFB71C1C),
        outline = Color(0xFFFFEBEE),
        outlineVariant = Color(0xFFE3F2FD),
    )
    val Dark = darkColorScheme(
        primary = Color(0xFFEF5350),
        primaryContainer = Color(0xFFC62828),
        secondary = Color(0xFF64B5F6),
        surface = Color(0xFF1E1E1E),
    )
}

// ============================================================
// OPÇÃO 5: "TECH ESPORTIVO" - Futurista Esportivo
// Roxo elétrico + Verde neon - Moderno, tech, gamer
// ============================================================
object Option5_TechEsportivo {
    val Light = lightColorScheme(
        primary = Color(0xFF6A1B9A),      // Roxo elétrico
        primaryContainer = Color(0xFFE1BEE7),
        secondary = Color(0xFF00E676),    // Verde neon
        secondaryContainer = Color(0xFFB9F6CA),
        tertiary = Color(0xFF2962FF),
        surface = Color.White,
        surfaceContainerHighest = Color(0xFFF3E5F5),
        error = Color(0xFFFF1744),
        outline = Color(0xFFEDE7F6),
        outlineVariant = Color(0xFFE8EAF6),
    )
    val Dark = darkColorScheme(
        primary = Color(0xFFCE93D8),
        primaryContainer = Color(0xFF6A1B9A),
        secondary = Color(0xFF69F0AE),
        surface = Color(0xFF0F0F0F),
    )
}

/**
 * CONFIGURAÇÕES DE TIPOGRAFIA POR OPÇÃO
 */
object TypographyOptions {
    // Opção 1-3: Roboto padrão (Material)
    // Opção 4: Mais expressiva (torcida)
    // Opção 5: Monoespaçada tech (tech esportivo)

    data class TypeConfig(
        val fontFamily: String = "Roboto",
        val displayLargeWeight: Int = 400,
        val headlineWeight: Int = 500,
        val bodyWeight: Int = 400,
        val letterSpacing: Float = 0f
    )

    val CLASSIC = TypeConfig() // Opção 1, 2, 3
    val EXPRESSIVE = TypeConfig(
        headlineWeight = 700,
        letterSpacing = 0.5f
    ) // Opção 4
    val MONO_TECH = TypeConfig(
        fontFamily = "Roboto Mono",
        displayLargeWeight = 500,
        headlineWeight = 600,
        bodyWeight = 400
    ) // Opção 5
}

/**
 * CONFIGURAÇÕES DE SHAPE (BORDAS) POR OPÇÃO
 */
object ShapeOptions {
    data class ShapeConfig(
        val cardCorner: Float = 16f,
        val buttonCorner: Float = 24f,
        val chipCorner: Float = 20f,
        val fabCorner: Float = 16f
    )

    val ROUNDED = ShapeConfig(16f, 24f, 20f, 16f)      // Opção 1, 3 - Suave
    val SHARP = ShapeConfig(8f, 12f, 16f, 12f)         // Opção 2 - Moderno
    val EXPRESSIVE = ShapeConfig(24f, 32f, 28f, 24f)   // Opção 4 - Bold
    val TECH = ShapeConfig(4f, 8f, 12f, 8f)            // Opção 5 - Geométrico
}

/**
 * CONFIGURAÇÕES DE ELEVAÇÃO/SOMBRA
 */
object ElevationOptions {
    data class ElevationConfig(
        val cardLevel1: Float = 1f,
        val cardLevel2: Float = 3f,
        val cardLevel3: Float = 6f,
        val fab: Float = 6f
    )

    val SUBTLE = ElevationConfig(0f, 1f, 3f, 4f)       // Opção 1, 3 - Flat design
    val MODERATE = ElevationConfig(1f, 3f, 6f, 6f)     // Opção 2 - Material padrão
    val BOLD = ElevationConfig(2f, 6f, 12f, 8f)        // Opção 4 - Destaque
    val LAYERED = ElevationConfig(1f, 4f, 8f, 12f)     // Opção 5 - Camadas
}

/**
 * EXEMPLO DE COMO APLICAR UMA OPÇÃO:
 *
 * No Color.kt, substitua por:
 *
 * import com.trackmyboot.app.ui.theme.Option3_GramadoPremium
 *
 * private val LightColorScheme = Option3_GramadoPremium.Light
 * private val DarkColorScheme = Option3_GramadoPremium.Dark
 *
 * No Theme.kt, ajuste shapes/typography se necessário
 */

@Composable
fun PreviewAllOptions() {
    // Use este composable para testar visualmente cada opção
    // Column { Option1... Option2... etc }
}