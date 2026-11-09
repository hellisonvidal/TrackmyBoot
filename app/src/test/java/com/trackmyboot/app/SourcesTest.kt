package com.trackmyboot.app

import com.trackmyboot.app.data.catalog.StoreCatalog
import com.trackmyboot.app.data.model.StoreInfo
import com.trackmyboot.app.data.model.StoreKind
import com.trackmyboot.app.data.model.StorePlatform
import com.trackmyboot.app.data.source.DemoSource
import com.trackmyboot.app.data.source.Http
import com.trackmyboot.app.data.source.ShopifySource
import com.trackmyboot.app.data.source.VtexSource
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
    fun demoOffersAreFlaggedAndRefreshable() = runTest {
        val demo = DemoSource(StoreCatalog.builtIn.first { it.id == StoreCatalog.DEMO_ID })
        val offers = demo.search("camisa Flamengo")
        assertTrue(offers.isNotEmpty())
        assertTrue(offers.all { it.isDemo && it.title.contains("Flamengo") && it.title.startsWith("Camisa") })
        val again = demo.refresh(offers.first().productRef)
        assertNotNull(again)
        assertEquals(offers.first().id, again!!.id)
    }
}
