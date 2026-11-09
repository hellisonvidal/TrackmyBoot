package com.trackmyboot.app.data.model

import com.trackmyboot.app.util.normalized
import kotlinx.serialization.Serializable

/** Categorias de equipamentos de futebol monitoradas pelo app. */
enum class Category(
    val label: String,
    val emoji: String,
    /** Termo enviado às lojas quando a busca é feita por categoria. */
    val searchTerm: String,
    /** Palavras (sem acento) que precisam aparecer no título para a oferta entrar na categoria. */
    val keywords: List<String>,
) {
    CAMISAS("Camisas", "👕", "camisa", listOf("camisa", "camiseta", "jersey", "shirt")),
    CHUTEIRAS("Chuteiras", "👟", "chuteira", listOf("chuteira", "boot", "cleat")),
    BOLAS("Bolas", "⚽", "bola futebol", listOf("bola", "ball")),
    MEIOES("Meiões", "🧦", "meiao", listOf("meiao", "meia", "meias", "sock")),
    LUVAS("Luvas de goleiro", "🧤", "luva goleiro", listOf("luva", "glove")),
    CANELEIRAS("Caneleiras", "🛡️", "caneleira", listOf("caneleira", "shin")),
    SHORTS("Calções", "🩳", "calcao futebol", listOf("calcao", "short", "bermuda")),
    AGASALHOS("Agasalhos", "🧥", "jaqueta futebol", listOf("jaqueta", "agasalho", "moletom", "casaco", "corta vento", "jacket")),
    ACESSORIOS("Acessórios", "🎒", "acessorio futebol", listOf("mochila", "bolsa", "bone", "cachecol", "garrafa", "squeeze", "chaveiro", "bandeira", "toalha")),
    ;

    fun matches(title: String): Boolean {
        val t = title.normalized()
        return keywords.any { t.contains(it) }
    }

    companion object {
        fun detect(title: String): Category? = entries.firstOrNull { it.matches(title) }
    }
}

/** Clube (ou seleção) que o usuário pode seguir. */
data class Club(
    val id: String,
    val name: String,
    /** Grafias alternativas usadas pelas lojas ("Timão", "SPFC", "Galo"...). */
    val aliases: List<String>,
    val country: String,
    /** Cores do escudo estilizado (ARGB). */
    val primaryColor: Long,
    val secondaryColor: Long,
    /** Ids de lojas oficiais do clube em [com.trackmyboot.app.data.catalog.StoreCatalog]. */
    val officialStoreIds: List<String> = emptyList(),
) {
    val initials: String get() = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

    fun matches(title: String): Boolean {
        val t = title.normalized()
        return (aliases + name).any { alias ->
            val a = alias.normalized()
            Regex("\\b${Regex.escape(a)}\\b").containsMatchIn(t)
        }
    }
}

enum class StorePlatform { VTEX, SHOPIFY, MERCADO_LIVRE, DEMO }

enum class StoreKind(val label: String) { RETAILER("Varejistas"), MARKETPLACE("Marketplaces"), BRAND("Marcas"), CLUB("Lojas de clubes"), DEMO("Demonstração") }

/** Uma loja on-line de onde o app captura preços. */
data class StoreInfo(
    val id: String,
    val name: String,
    val baseUrl: String,
    val platform: StorePlatform,
    val kind: StoreKind,
    val enabledByDefault: Boolean = true,
)

/** Uma oferta capturada em uma loja. */
@Serializable
data class Offer(
    /** Identificador estável: "<storeId>:<productRef>". */
    val id: String,
    val storeId: String,
    val storeName: String,
    /** Referência que o conector usa para reconsultar o preço (id VTEX, handle Shopify, id ML...). */
    val productRef: String,
    val title: String,
    val brand: String?,
    val price: Double,
    val listPrice: Double?,
    val imageUrl: String?,
    val url: String,
    val available: Boolean = true,
    val isDemo: Boolean = false,
) {
    val discountPercent: Int
        get() = if (listPrice != null && listPrice > price && listPrice > 0) (((listPrice - price) / listPrice) * 100).toInt() else 0
}

enum class SortOrder(val label: String) { BEST_DISCOUNT("Maior desconto"), LOWEST_PRICE("Menor preço"), HIGHEST_PRICE("Maior preço") }
