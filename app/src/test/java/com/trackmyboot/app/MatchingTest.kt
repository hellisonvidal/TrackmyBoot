package com.trackmyboot.app

import com.trackmyboot.app.data.OfferQuery
import com.trackmyboot.app.data.catalog.ClubCatalog
import com.trackmyboot.app.data.model.Category
import com.trackmyboot.app.data.model.Offer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchingTest {
    private fun offer(title: String) = Offer("s:1", "s", "Loja", "1", title, null, 100.0, 150.0, null, "https://x")

    @Test
    fun categoriesIgnoreAccents() {
        assertTrue(Category.MEIOES.matches("Meião Oficial Nike"))
        assertTrue(Category.SHORTS.matches("Calção de treino"))
        assertEquals(Category.CHUTEIRAS, Category.detect("Chuteira Society Adidas"))
    }

    @Test
    fun clubAliasesMatchWholeWords() {
        val atletico = ClubCatalog.find("atletico-mg")!!
        assertTrue(atletico.matches("Camisa do Galo 2026"))
        assertTrue(atletico.matches("Camisa Atlético Mineiro I"))
        val inter = ClubCatalog.find("internacional")!!
        assertFalse("'Inter' não pode casar com 'interna'", inter.matches("Palmilha interna"))
        assertTrue(ClubCatalog.find("sao-paulo")!!.matches("Camisa SPFC III"))
    }

    @Test
    fun queryBuildsTermAndFilters() {
        val fla = ClubCatalog.find("flamengo")!!
        val q = OfferQuery(category = Category.CAMISAS, club = fla)
        assertEquals("camisa Flamengo", q.searchTerm)
        assertTrue(q.accepts(offer("Camisa Flamengo I 2026")))
        assertFalse(q.accepts(offer("Camisa Vasco I 2026")))
        assertFalse(q.accepts(offer("Bola Flamengo")))
    }

    @Test
    fun discountPercent() {
        assertEquals(33, offer("x").discountPercent)
        assertEquals(0, offer("x").copy(listPrice = null).discountPercent)
    }
}
