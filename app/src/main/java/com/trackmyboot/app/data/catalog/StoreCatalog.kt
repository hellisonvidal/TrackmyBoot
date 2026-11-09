package com.trackmyboot.app.data.catalog

import com.trackmyboot.app.data.model.StoreInfo
import com.trackmyboot.app.data.model.StoreKind
import com.trackmyboot.app.data.model.StorePlatform

/**
 * Lojas embutidas no app. Cada loja usa um conector conforme a plataforma de e-commerce:
 *  - VTEX: API pública de catálogo (/api/catalog_system/pub/products/search)
 *  - SHOPIFY: /search/suggest.json e /products/<handle>.js
 *  - MERCADO_LIVRE: API pública do Mercado Livre
 *
 * Lojas que mudarem de plataforma ou bloquearem o acesso simplesmente aparecem como
 * "indisponíveis" na busca. O usuário pode cadastrar outras lojas (inclusive lojas oficiais
 * de clubes) na aba "Lojas".
 */
object StoreCatalog {
    const val DEMO_ID = "demo"

    val builtIn: List<StoreInfo> = listOf(
        StoreInfo("centauro", "Centauro", "https://www.centauro.com.br", StorePlatform.VTEX, StoreKind.RETAILER),
        StoreInfo("futfanatics", "FutFanatics", "https://www.futfanatics.com.br", StorePlatform.VTEX, StoreKind.RETAILER),
        StoreInfo("decathlon", "Decathlon", "https://www.decathlon.com.br", StorePlatform.VTEX, StoreKind.RETAILER),
        StoreInfo("mercadolivre", "Mercado Livre", "https://api.mercadolibre.com", StorePlatform.MERCADO_LIVRE, StoreKind.MARKETPLACE),
        StoreInfo("penalty", "Penalty", "https://www.penalty.com.br", StorePlatform.VTEX, StoreKind.BRAND),
        StoreInfo("umbro", "Umbro", "https://www.umbro.com.br", StorePlatform.VTEX, StoreKind.BRAND),
        StoreInfo("topper", "Topper", "https://www.topper.com.br", StorePlatform.VTEX, StoreKind.BRAND),
        StoreInfo(DEMO_ID, "Ofertas de demonstração", "https://example.com", StorePlatform.DEMO, StoreKind.DEMO),
    )
}
