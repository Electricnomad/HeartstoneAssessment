package com.tornado.hearthstonecards.common.data

import kotlinx.serialization.Serializable


enum class Rarity {
    Legendary,
    Epic,
    Rare,
    Common,
    Free,
    Uncommon,
}

@Serializable
data class CardsCollection(
    val setsWithCards: Map<String, List<Card>>
)

@Serializable
data class Card (
    val cardId: String,
    val name: String,
    val cardSet: String,
    val type: String,
    val text: String? = null,
    val playerClass: String? = null,
    val locale: String,
    val img: String? = null,
    val imgGold: String? = null,
    val mechanics: List<Mechanic> = emptyList(),
    val faction: String? = null,
    val rarity: Rarity = Rarity.Uncommon,
    val cost: Int = 0,
    val artist: String? = null,
    val howToGet: String? = null,
    val collectible: Boolean = false,
    val howToGetGold: String? = null,
    val attack: Int = 0,
    val health: Int = 0,
    val race: String? = null,
    val flavor: String? = null,
    val durability: Int = 0,
    val elite: Boolean = false,
    val multiClassGroup: String? = null,
    val classes: List<String> = emptyList()
)
@Serializable
data class Mechanic(
    val name: String
)

