package com.trackmyboot.app.data.source

import com.trackmyboot.app.data.catalog.ClubCatalog
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.StoreInfo
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Gera ofertas fictícias (sempre marcadas como "demonstração") para o app funcionar sem rede
 * ou enquanto as lojas reais estiverem indisponíveis. Os preços variam levemente a cada dia
 * para permitir testar o monitoramento.
 */
class DemoSource(override val store: StoreInfo) : StoreSource {

    private val brands = listOf("Nike", "Adidas", "Puma", "Umbro", "Penalty", "Topper", "Kappa", "New Balance")
    private val variants = mapOf(
        Category.CAMISAS to listOf("I 2026 Torcedor", "II 2026 Torcedor", "I 2026 Jogador", "Treino 2026", "Retrô"),
        Category.CHUTEIRAS to listOf("Campo Elite", "Society Pro", "Futsal Academy", "Campo Club", "Society Infantil"),
        Category.BOLAS to listOf("de Campo Oficial", "Society", "Futsal", "de Treino", "Mini"),
        Category.MEIOES to listOf("Oficial I", "Oficial II", "de Treino", "Antiderrapante", "Infantil"),
        Category.LUVAS to listOf("de Goleiro Pro", "de Goleiro Training", "de Goleiro Infantil", "de Goleiro Elite", "de Goleiro Grip"),
        Category.CANELEIRAS to listOf("Slip-in", "com Tornozeleira", "Pro Lite", "Infantil", "Match"),
        Category.SHORTS to listOf("I 2026", "II 2026", "de Treino", "Infantil", "Goleiro"),
        Category.AGASALHOS to listOf("Hino", "de Viagem", "Corta-vento", "Moletom", "Pré-jogo"),
        Category.ACESSORIOS to listOf("Mochila", "Boné", "Cachecol", "Squeeze", "Bandeira"),
    )
    private val basePrices = mapOf(
        Category.CAMISAS to 349.0, Category.CHUTEIRAS to 499.0, Category.BOLAS to 199.0,
        Category.MEIOES to 69.0, Category.LUVAS to 229.0, Category.CANELEIRAS to 89.0,
        Category.SHORTS to 179.0, Category.AGASALHOS to 449.0, Category.ACESSORIOS to 119.0,
    )

    override suspend fun search(term: String, limit: Int): List<Offer> {
        val club = ClubCatalog.clubs.firstOrNull { it.matches(term) }
        val categories = Category.detect(term)?.let(::listOf) ?: Category.entries
        val rnd = Random(term.lowercase().hashCode())
        val free = term.trim().replaceFirstChar { it.uppercase() }
        return categories.flatMap { category ->
            variants.getValue(category).map { variant ->
                val brand = brands[rnd.nextInt(brands.size)]
                val noun = when (category) {
                    Category.ACESSORIOS -> variant
                    Category.CAMISAS -> "Camisa"
                    Category.CHUTEIRAS -> "Chuteira $brand"
                    Category.BOLAS -> "Bola"
                    Category.MEIOES -> "Meião"
                    Category.LUVAS -> "Luva"
                    Category.CANELEIRAS -> "Caneleira"
                    Category.SHORTS -> "Calção"
                    Category.AGASALHOS -> "Jaqueta"
                }
                val subject = club?.name ?: if (Category.detect(term) == null) free else ""
                val title = listOf(noun, subject, if (category == Category.ACESSORIOS) "" else variant)
                    .filter { it.isNotBlank() }.joinToString(" ")
                make(title, brand, category)
            }
        }.shuffled(rnd).take(limit)
    }

    override suspend fun refresh(productRef: String): Offer? {
        val (encoded, brand, categoryName) = productRef.split("|").takeIf { it.size >= 3 } ?: return null
        val category = runCatching { Category.valueOf(categoryName) }.getOrNull() ?: return null
        return make(URLDecoder.decode(encoded, "UTF-8"), brand, category)
    }

    private fun make(title: String, brand: String, category: Category): Offer {
        val seed = abs(title.hashCode())
        val listPrice = (basePrices.getValue(category) * (0.8 + (seed % 50) / 100.0)).roundToInt() - 0.10
        // Desconto varia com o dia, simulando promoções.
        val day = (System.currentTimeMillis() / 86_400_000L).toInt()
        val discount = listOf(0, 5, 10, 15, 20, 25, 30, 40)[abs(seed + day) % 8]
        val price = ((listPrice * (100 - discount) / 100.0) * 100).roundToInt() / 100.0
        val ref = "${URLEncoder.encode(title, "UTF-8")}|$brand|${category.name}"
        return Offer(
            id = store.offerId(ref),
            storeId = store.id,
            storeName = store.name,
            productRef = ref,
            title = title,
            brand = brand,
            price = price,
            listPrice = listPrice.takeIf { discount > 0 },
            imageUrl = null,
            url = "https://www.google.com/search?tbm=shop&q=" + URLEncoder.encode(title, "UTF-8"),
            isDemo = true,
        )
    }
}
