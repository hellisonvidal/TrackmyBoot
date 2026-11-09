package com.trackmyboot.app.data.source

import com.trackmyboot.app.data.model.Offer
import com.trackmyboot.app.data.model.StoreInfo

/** Conector que sabe buscar produtos e reconsultar preços em uma loja. */
interface StoreSource {
    val store: StoreInfo

    suspend fun search(term: String, limit: Int = 24): List<Offer>

    /** Reconsulta o preço atual de um produto já capturado. Retorna null se sumiu da loja. */
    suspend fun refresh(productRef: String): Offer?
}

fun StoreInfo.offerId(ref: String) = "$id:$ref"
