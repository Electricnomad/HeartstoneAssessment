package com.tornado.hearthstonecards.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tornado.hearthstonecards.common.data.Card
import com.tornado.hearthstonecards.common.data.Rarity
import com.tornado.hearthstonecards.common.viewmodel.CardsUiState
import com.tornado.hearthstonecards.common.viewmodel.CardsViewModel
import com.tornado.hearthstonecards.common.viewmodel.FilterOptions

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App() {
    val viewModel = remember { CardsViewModel() }
    val currentFilter = viewModel.filter.collectAsState(FilterOptions())
    val cards = viewModel.cards.collectAsState(CardsUiState.Loading)

    Column {
        Row {
            TextField(
                value = currentFilter.value.nameFilter,
                onValueChange = {
                    viewModel.filterName(it)
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Name")
                },
            )
            TextField(
                value = currentFilter.value.mechanicFilter,
                onValueChange = {
                    viewModel.filterMechanic(it)
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Mechanic")
                },
            )
        }
        Row {
            if (currentFilter.value.rarityFilter == null) {
                Button(onClick = {
                    viewModel.filterRarity(null)
                }) {
                    Text("None")
                }
            } else {
                OutlinedButton(onClick = {
                    viewModel.filterRarity(null)
                }) {
                    Text("None")
                }
            }

            Rarity.values().forEach {
                if (currentFilter.value.rarityFilter == it) {
                    Button(onClick = {
                        viewModel.filterRarity(it)
                    }) {
                        Text(it.name)
                    }
                } else {
                    OutlinedButton(onClick = {
                        viewModel.filterRarity(it)
                    }) {
                        Text(it.name)
                    }
                }
            }
        }
        when (val value = cards.value) {
            is CardsUiState.Loading -> CircularProgressIndicator()
            is CardsUiState.Error -> Text("there was an error")
            is CardsUiState.Result -> {
                if (value.cardsCollection.setsWithCards.isEmpty()) {
                    Text("empty list, refine your search")
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(160.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    value.cardsCollection.setsWithCards.forEach {
                        items(items = it.value) { card ->
                            Card(
                                modifier = Modifier.padding(all = 4.dp).heightIn(min = 64.dp),
                                onClick = {},
                            ) {
                                Column(Modifier.padding(all = 4.dp)) {

                                    Text(card.name, style = TextStyle.Default.copy(
                                        fontSize = 16.sp,
                                        color = card.rarityColor,
                                        fontWeight = FontWeight.Bold)
                                    )
                                    card.playerClass?.also {
                                        Text(it, style = TextStyle.Default.copy(
                                            color = card.classColor,
                                            fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    card.text?.also { Text(it) }
                                    card.flavor?.also { Text(it, fontStyle = FontStyle.Italic) }
                                    if (card.mechanics.isNotEmpty()) {
                                        Text(card.mechanics.map { it.name }.joinToString(","), fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

val Card.classColor: Color
    get() = classColorMap[playerClass] ?: Color.Black

val Card.rarityColor: Color
    get() = rarityColorMap[rarity.name] ?: Color.Black

private val classColorMap = mapOf(
    "Death Knight" to Color(0xFFC41E3A),
    "Demon Hunter" to Color(0xFFA330C9),
    "Druid" to Color(0xFFFF7C0A),
    "Evoker" to Color(0xFF33937F),
    "Hunter" to Color(0xFFAAD372),
    "Mage" to Color(0xFF3FC7EB),
    "Monk" to Color(0xFF00FF98),
    "Paladin" to Color(0xFFF48CBA),
    "Priest" to Color(0xFFCCCCCC),
    "Rogue" to Color(0xFFFFF468),
    "Shaman" to Color(0xFF0070DD),
    "Warlock" to Color(0xFF8788EE),
    "Warrior" to Color(0xFFC69B6D),
)
private val rarityColorMap = mapOf(
    "Rare" to Color(0xff0070dd),
    "Epic" to Color(0xffa335ee),
    "Legendary" to Color(0xffff8000),
    "Uncommon" to Color(0xff1edf00),
)