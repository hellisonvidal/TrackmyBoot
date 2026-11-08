package com.trackmyboot.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trackmyboot.app.data.db.converter.DateConverter
import com.trackmyboot.app.data.db.converter.ProductCategoryConverter
import com.trackmyboot.app.data.db.converter.StoreConverter
import kotlinx.serialization.Serializable
import java.util.Date

@Entity(tableName = "products")
@Serializable
@TypeConverters(DateConverter::class, ProductCategoryConverter::class, StoreConverter::class)
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String,
    val category: ProductCategory,
    val description: String,
    val imageUrl: String,
    val productUrl: String,
    val store: Store,
    val currentPrice: Double,
    val originalPrice: Double,
    val discountPercentage: Int,
    val currency: String = "BRL",
    val isAvailable: Boolean = true,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val lastUpdated: Date = Date(),
    val createdAt: Date = Date()
) {
    val isOnSale: Boolean
        get() = discountPercentage > 0

    val formattedCurrentPrice: String
        get() = String.format("%.2f", currentPrice).replace(".", ",")

    val formattedOriginalPrice: String
        get() = String.format("%.2f", originalPrice).replace(".", ",")
}

@Serializable
enum class ProductCategory(
    val displayName: String,
    val iconName: String
) {
    JERSEYS("Camisas", "ic_jersey"),
    BALLS("Bolas", "ic_ball"),
    CLEATS("Chuteiras", "ic_cleats"),
    ACCESSORIES("Acessórios", "ic_accessory"),
    GOALKEEPER("Goleiro", "ic_goalkeeper"),
    TRAINING("Treino", "ic_training"),
    SOCKS("Meias", "ic_socks"),
    GLOVES("Luvas", "ic_gloves"),
    BAGS("Bolsas", "ic_bag"),
    SHIN_GUARDS("Caneleiras", "ic_shin_guard")
}

@Serializable
enum class Store(
    val displayName: String,
    val baseUrl: String,
    val logoUrl: String
) {
    // Grandes varejistas esportivos
    NETSHOES("Netshoes", "https://www.netshoes.com.br", "https://logo.netshoes.com.br/logo.png"),
    CENTAURO("Centauro", "https://www.centauro.com.br", "https://logo.centauro.com.br/logo.png"),
    DAZUL("DaZul", "https://www.dazul.com.br", "https://logo.dazul.com.br/logo.png"),
    FUTFANATICS("FutFanatics", "https://www.futfanatics.com.br", "https://logo.futfanatics.com.br/logo.png"),
    KANUI("Kanui", "https://www.kanui.com.br", "https://logo.kanui.com.br/logo.png"),

    // Marketplaces
    AMAZON("Amazon", "https://www.amazon.com.br", "https://logo.amazon.com.br/logo.png"),
    MERCADOLIVRE("Mercado Livre", "https://www.mercadolivre.com.br", "https://logo.mercadolivre.com.br/logo.png"),
    MAGAZINE_LUIZA("Magazine Luiza", "https://www.magazineluiza.com.br", "https://logo.magazineluiza.com.br/logo.png"),
    SHOPEE("Shopee", "https://shopee.com.br", "https://logo.shopee.com.br/logo.png"),

    // Lojas especializadas em futebol
    FUTEBOL_SHOP("Futebol Shop", "https://www.futebolshop.com.br", "https://logo.futebolshop.com.br/logo.png"),
    PRODIRECT("Pro:Direct Soccer", "https://www.prodirectsoccer.com.br", "https://logo.prodirectsoccer.com.br/logo.png"),
    CLASSIC_FOOTBALL_SHIRTS("Classic Football Shirts", "https://www.classicfootballshirts.co.br", "https://logo.classicfootballshirts.co.br/logo.png"),
    SOCCER_BIBLE("Soccer Bible", "https://www.soccerbible.com.br", "https://logo.soccerbible.com.br/logo.png"),
    FOOTY_HEADLINES("Footy Headlines", "https://www.footyheadlines.com.br", "https://logo.footyheadlines.com.br/logo.png"),

    // Lojas de clubes oficiais
    LOJA_FLAMENGO("Loja Flamengo", "https://loja.flamengo.com.br", "https://logo.flamengo.com.br/logo.png"),
    LOJA_CORINTHIANS("Loja Corinthians", "https://loja.corinthians.com.br", "https://logo.corinthians.com.br/logo.png"),
    LOJA_PALMEIRAS("Loja Palmeiras", "https://loja.palmeiras.com.br", "https://logo.palmeiras.com.br/logo.png"),
    LOJA_SAO_PAULO("Loja São Paulo", "https://loja.saopaulofc.net", "https://logo.saopaulofc.net/logo.png"),
    LOJA_SANTOS("Loja Santos", "https://loja.santosfc.com.br", "https://logo.santosfc.com.br/logo.png"),
    LOJA_GREMIO("Loja Grêmio", "https://loja.gremio.net", "https://logo.gremio.net/logo.png"),
    LOJA_INTERNACIONAL("Loja Internacional", "https://loja.internacional.com.br", "https://logo.internacional.com.br/logo.png"),
    LOJA_ATLETICO_MG("Loja Atlético-MG", "https://loja.atletico.com.br", "https://logo.atletico.com.br/logo.png"),
    LOJA_CRUZEIRO("Loja Cruzeiro", "https://loja.cruzeiro.com.br", "https://logo.cruzeiro.com.br/logo.png"),
    LOJA_VASCO("Loja Vasco", "https://loja.vasco.com.br", "https://logo.vasco.com.br/logo.png"),
    LOJA_BOTAFOGO("Loja Botafogo", "https://loja.botafogo.com.br", "https://logo.botafogo.com.br/logo.png"),
    LOJA_FLUENCENSE("Loja Fluminense", "https://loja.fluminense.com.br", "https://logo.fluminense.com.br/logo.png"),
    LOJA_BAHIA("Loja Bahia", "https://loja.bahia.com.br", "https://logo.bahia.com.br/logo.png"),
    LOJA_SPORT("Loja Sport", "https://loja.sport.com.br", "https://logo.sport.com.br/logo.png"),
    LOJA_NAUTICO("Loja Náutico", "https://loja.nautico.com.br", "https://logo.nautico.com.br/logo.png"),
    LOJA_CEARA("Loja Ceará", "https://loja.ceara.com.br", "https://logo.ceara.com.br/logo.png"),
    LOJA_FORTALEZA("Loja Fortaleza", "https://loja.fortaleza.com.br", "https://logo.fortaleza.com.br/logo.png"),

    // Marcas diretas
    NIKE("Nike", "https://www.nike.com.br", "https://logo.nike.com.br/logo.png"),
    ADIDAS("Adidas", "https://www.adidas.com.br", "https://logo.adidas.com.br/logo.png"),
    PUMA("Puma", "https://www.puma.com.br", "https://logo.puma.com.br/logo.png"),
    UMBRO("Umbro", "https://www.umbro.com.br", "https://logo.umbro.com.br/logo.png"),
    NEW_BALANCE("New Balance", "https://www.newbalance.com.br", "https://logo.newbalance.com.br/logo.png"),
    MIZUNO("Mizuno", "https://www.mizuno.com.br", "https://logo.mizuno.com.br/logo.png"),
    ASICS("Asics", "https://www.asics.com.br", "https://logo.asics.com.br/logo.png"),
    PENALTY("Penalty", "https://www.penalty.com.br", "https://logo.penalty.com.br/logo.png"),
    TOPPER("Topper", "https://www.topper.com.br", "https://logo.topper.com.br/logo.png"),
    KAPPA("Kappa", "https://www.kappa.com.br", "https://logo.kappa.com.br/logo.png"),
    DIADORA("Diadora", "https://www.diadora.com.br", "https://logo.diadora.com.br/logo.png"),
    LOTTO("Lotto", "https://www.lotto.com.br", "https://logo.lotto.com.br/logo.png"),

    // Outras lojas esportivas
    DECATLHON("Decathlon", "https://www.decathlon.com.br", "https://logo.decathlon.com.br/logo.png"),
    ESPORTE_CLUBE("Esporte Clube", "https://www.esporteclube.com.br", "https://logo.esporteclube.com.br/logo.png"),
    TENYS_PE_BARRAS("Tenys Pé Baruel", "https://www.tenyspebaruel.com.br", "https://logo.tenyspebaruel.com.br/logo.png"),
    PAO_DE_ACUCAR("Pão de Açúcar", "https://www.paodeacucar.com", "https://logo.paodeacucar.com/logo.png"),
    CARREFOUR("Carrefour", "https://www.carrefour.com.br", "https://logo.carrefour.com.br/logo.png"),
    EXTRA("Extra", "https://www.extra.com.br", "https://logo.extra.com.br/logo.png"),
    AMERICANAS("Americanas", "https://www.americanas.com.br", "https://logo.americanas.com.br/logo.png"),
    SUBMARINO("Submarino", "https://www.submarino.com.br", "https://logo.submarino.com.br/logo.png"),
    SHOPTIME("Shoptime", "https://www.shoptime.com.br", "https://logo.shoptime.com.br/logo.png")
}