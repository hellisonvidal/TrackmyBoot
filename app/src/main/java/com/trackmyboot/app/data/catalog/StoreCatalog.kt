package com.trackmyboot.app.data.catalog

import com.trackmyboot.app.data.model.StoreInfo
import com.trackmyboot.app.data.model.StoreKind
import com.trackmyboot.app.data.model.StorePlatform

/**
 * Lojas embutidas no app. O conector usado em cada uma depende da plataforma:
 *  - VTEX: API pública de catálogo (/api/catalog_system/pub/products/search)
 *  - SHOPIFY: /search/suggest.json e /products/<handle>.js
 *  - MERCADO_LIVRE: API pública do Mercado Livre
 *  - WEB: página de busca da loja, lendo os dados de produto embutidos no HTML
 *  - LINK: sem captura automática (a loja bloqueia ou exige credenciais); o app abre a busca na loja
 *
 * Se uma loja mudar de plataforma ou bloquear o acesso, ela aparece como "indisponível" na busca
 * e continua acessível pelo atalho "Ver na loja". O usuário pode cadastrar outras lojas
 * na aba Configurações.
 */
object StoreCatalog {
    private fun vtexSearch(base: String) = "$base/{q}?_q={q}&map=ft"

    val builtIn: List<StoreInfo> = listOf(
        // Varejistas esportivos
        StoreInfo("netshoes", "Netshoes", "https://www.netshoes.com.br", StorePlatform.WEB, StoreKind.RETAILER, "https://www.netshoes.com.br/busca/{slug}"),
        StoreInfo("centauro", "Centauro", "https://www.centauro.com.br", StorePlatform.WEB, StoreKind.RETAILER, "https://www.centauro.com.br/busca/{slug}"),
        StoreInfo("futfanatics", "FutFanatics", "https://www.futfanatics.com.br", StorePlatform.VTEX, StoreKind.RETAILER, vtexSearch("https://www.futfanatics.com.br")),
        StoreInfo("decathlon", "Decathlon", "https://www.decathlon.com.br", StorePlatform.VTEX, StoreKind.RETAILER, vtexSearch("https://www.decathlon.com.br")),
        StoreInfo("dafiti", "Dafiti", "https://www.dafiti.com.br", StorePlatform.WEB, StoreKind.RETAILER, "https://www.dafiti.com.br/catalog/?q={q}"),

        // Marketplaces
        StoreInfo("mercadolivre", "Mercado Livre", "https://api.mercadolibre.com", StorePlatform.MERCADO_LIVRE, StoreKind.MARKETPLACE, "https://lista.mercadolivre.com.br/{slug}"),
        StoreInfo("magalu", "Magazine Luiza", "https://www.magazineluiza.com.br", StorePlatform.WEB, StoreKind.MARKETPLACE, "https://www.magazineluiza.com.br/busca/{q}/"),
        StoreInfo("amazon", "Amazon", "https://www.amazon.com.br", StorePlatform.LINK, StoreKind.MARKETPLACE, "https://www.amazon.com.br/s?k={q}"),
        StoreInfo("shopee", "Shopee", "https://shopee.com.br", StorePlatform.LINK, StoreKind.MARKETPLACE, "https://shopee.com.br/search?keyword={q}"),

        // Marcas
        StoreInfo("nike", "Nike", "https://www.nike.com.br", StorePlatform.WEB, StoreKind.BRAND, "https://www.nike.com.br/busca?q={q}"),
        StoreInfo("adidas", "Adidas", "https://www.adidas.com.br", StorePlatform.WEB, StoreKind.BRAND, "https://www.adidas.com.br/search?q={q}"),
        StoreInfo("puma", "Puma", "https://br.puma.com", StorePlatform.WEB, StoreKind.BRAND, "https://br.puma.com/search?q={q}"),
        StoreInfo("penalty", "Penalty", "https://www.penalty.com.br", StorePlatform.VTEX, StoreKind.BRAND, vtexSearch("https://www.penalty.com.br")),
        StoreInfo("umbro", "Umbro", "https://www.umbro.com.br", StorePlatform.VTEX, StoreKind.BRAND, vtexSearch("https://www.umbro.com.br")),
        StoreInfo("topper", "Topper", "https://www.topper.com.br", StorePlatform.VTEX, StoreKind.BRAND, vtexSearch("https://www.topper.com.br")),
    )
}
