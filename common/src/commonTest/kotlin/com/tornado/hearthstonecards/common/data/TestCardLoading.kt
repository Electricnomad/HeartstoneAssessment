package com.tornado.hearthstonecards.common.data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class TestCardLoading {

    @Test
    fun testLoadsCorrectly() {
        val data = TestCardLoading::class.java.getResourceAsStream("/cards.json")!!.bufferedReader().use {
            it.readText()
        }
        val cards = Json.decodeFromString<Map<String, List<Card>>>(data)
        CardsCollection(cards)
    }

}