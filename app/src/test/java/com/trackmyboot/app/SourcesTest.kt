package com.trackmyboot.app

import com.trackmyboot.app.data.catalog.StoreCatalog
import com.trackmyboot.app.data.model.StoreInfo
import com.trackmyboot.app.data.model.StoreKind
import com.trackmyboot.app.data.model.StorePlatform
import com.trackmyboot.app.data.source.Http
import com.trackmyboot.app.data.source.ShopifySource
import com.trackmyboot.app.data.source.VtexSource
import com.trackmyboot.app.data.source.WebPageSource
import com.trackmyboot.app.data.source.WebProductExtractor
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SourcesTest {
    private lateinit var server: MockWebServer

    @Before fun setUp() { server = MockWebServer().apply { start() } }
    @After fun tearDown() = server.shutdown()

    private fun store(platform: StorePlatform) =
        StoreInfo("t", "Teste", server.url("/").toString(), platform, StoreKind.RETAILER)

    @Test
    fun vtexPicksCheapestAvailableSku() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """
                [{"productId":"42","productName":"Chuteira Campo X","brand":"Marca","link":"https://loja/x/p",
                  "items":[
                    {"images":[{"imageUrl":"https://img/1.jpg"}],"sellers":[{"commertialOffer":{"Price":299.9,"ListPrice":399.9,"AvailableQuantity":0}}]},
                    {"images":[{"imageUrl":"https://img/2.jpg"}],"sellers":[{"commertialOffer":{"Price":319.9,"ListPrice":399.9,"AvailableQuantity":3}}]}
                  ]}]
                """.trimIndent(),
            ),
        )
        val offers = VtexSource(store(StorePlatform.VTEX), Http()).search("chuteira")
        assertEquals(1, offers.size)
        val o = offers.first()
        assertEquals("t:42", o.id)
        assertEquals(319.9, o.price, 0.001)
        assertEquals(399.9, o.listPrice!!, 0.001)
        assertTrue(o.available)
        assertEquals("https://img/2.jpg", o.imageUrl)
        assertTrue(server.takeRequest().path!!.startsWith("/api/catalog_system/pub/products/search?ft=chuteira"))
    }

    @Test
    fun shopifySearchAndRefresh() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"resources":{"results":{"products":[{"handle":"camisa-1","title":"Camisa Retrô","price":"189.90",
                "compare_at_price_max":"249.90","url":"/products/camisa-1","image":"//cdn/img.jpg","available":true,"vendor":"Loja"}]}}}""",
            ),
        )
        server.enqueue(MockResponse().setBody("""{"title":"Camisa Retrô","price":15990,"compare_at_price":24990,"available":false}"""))
        val source = ShopifySource(store(StorePlatform.SHOPIFY), Http())
        val found = source.search("camisa").single()
        assertEquals(189.9, found.price, 0.001)
        assertEquals("https://cdn/img.jpg", found.imageUrl)
        val refreshed = source.refresh(found.productRef)!!
        assertEquals(159.9, refreshed.price, 0.001)
        assertFalse(refreshed.available)
    }

    @Test
    fun webPageReadsJsonLdAndNextData() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """
                <html><head>
                <script type="application/ld+json">
                {"@context":"https://schema.org","@type":"ItemList","itemListElement":[
                  {"@type":"ListItem","item":{"@type":"Product","name":"Chuteira Society Pro","url":"/chuteira-society-pro",
                   "image":["https://img/c.jpg"],"brand":{"@type":"Brand","name":"Marca"},
                   "offers":{"@type":"Offer","price":"249.90","priceCurrency":"BRL","availability":"https://schema.org/InStock"}}}]}
                </script>
                <script id="__NEXT_DATA__" type="application/json">
                {"props":{"pageProps":{"search":{"products":[
                  {"productName":"Camisa Flamengo I 2026","link":"https://loja/camisa-fla","price":{"value":"R$ 349,90"},"listPrice":"R$ 449,90","imageUrl":"//cdn/fla.jpg"},
                  {"title":"Banner","link":"/promo"}
                ]}}}}
                </script>
                <script>window.__STATE__ = {"x":{"name":"Bola Campo Oficial","url":"/bola","salePrice":199.9,"originalPrice":299.9,"available":false}};</script>
                </head><body></body></html>
                """.trimIndent(),
            ),
        )
        val store = StoreInfo("w", "Web", server.url("/").toString(), StorePlatform.WEB, StoreKind.RETAILER, server.url("/").toString() + "busca/{slug}")
        val offers = WebPageSource(store, Http()).search("chuteira society")
        assertEquals("/busca/chuteira-society", server.takeRequest().path)
        assertEquals(3, offers.size)
        val boot = offers.first { it.title == "Chuteira Society Pro" }
        assertEquals(249.9, boot.price, 0.001)
        assertEquals("Marca", boot.brand)
        assertTrue(boot.url.endsWith("/chuteira-society-pro"))
        val shirt = offers.first { it.title.startsWith("Camisa") }
        assertEquals(349.9, shirt.price, 0.001)
        assertEquals(449.9, shirt.listPrice!!, 0.001)
        assertEquals("https://cdn/fla.jpg", shirt.imageUrl)
        val ball = offers.first { it.title.startsWith("Bola") }
        assertFalse(ball.available)
        assertEquals(33, ball.discountPercent)
    }

    @Test
    fun brazilianPriceFormats() {
        assertEquals(1299.9, WebProductExtractor.parsePrice("R$ 1.299,90")!!, 0.001)
        assertEquals(1299.9, WebProductExtractor.parsePrice("1,299.90")!!, 0.001)
        assertEquals(89.0, WebProductExtractor.parsePrice("89")!!, 0.001)
    }

    @Test
    fun searchLinksUseTemplates() {
        val amazon = StoreCatalog.builtIn.first { it.id == "amazon" }
        assertEquals("https://www.amazon.com.br/s?k=camisa+Gr%C3%AAmio", amazon.searchLink("camisa Grêmio"))
        val netshoes = StoreCatalog.builtIn.first { it.id == "netshoes" }
        assertEquals("https://www.netshoes.com.br/busca/camisa-gremio", netshoes.searchLink("camisa Grêmio"))
        assertTrue(StoreCatalog.builtIn.all { it.searchUrl != null })
    }
}
