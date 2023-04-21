package com.tornado.hearthstonecards.common.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import com.tornado.hearthstonecards.common.data.CardsCollection
import com.tornado.hearthstonecards.common.data.Rarity
import com.tornado.hearthstonecards.common.repository.CardRepository
import com.tornado.hearthstonecards.common.repository.RepositoryResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CardsViewModel(
    private val repository: CardRepository = CardRepository.createInstance(),
    // normally on android this would be the android viewmodelScope
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default) {

    init {
        CoroutineScope(dispatcher).launch {
            repository.loadCards()
        }
    }

    private val _filter = MutableStateFlow(FilterOptions())
    val filter: Flow<FilterOptions>
        get() = _filter

    private val _allCards =
        repository.cards.combineTransform(_filter) { cards, filter ->
            emit(cards.toUiState(filter))
        }
    val cards: Flow<CardsUiState>
        get() = _allCards

    fun filterMechanic(filter: String) {
        CoroutineScope(dispatcher).launch {
            if (_filter.value.mechanicFilter != filter) {
                _filter.emit(
                    _filter.value.copy(mechanicFilter = filter)
                )
            }
        }
    }
    fun filterRarity(rarity: Rarity?) {
        CoroutineScope(dispatcher).launch {
            if (_filter.value.rarityFilter != rarity) {
                _filter.emit(
                    _filter.value.copy(rarityFilter = rarity)
                )
            }
        }
    }
    fun filterName(name: String) {
        println("filter name: $name")
        CoroutineScope(dispatcher).launch {
            if (_filter.value.nameFilter != name) {
                _filter.emit(
                    _filter.value.copy(nameFilter = name)
                )
            }
        }
    }

    private fun RepositoryResult<CardsCollection>.toUiState(filter: FilterOptions): CardsUiState {
        return when (this) {
            is RepositoryResult.Error -> CardsUiState.Error
            is RepositoryResult.Loading -> CardsUiState.Loading
            is RepositoryResult.Success -> {
                val result = filterCollection(collection, filter)
                CardsUiState.Result(result)
            }
        }
    }

    private fun filterCollection(collection: CardsCollection, filter: FilterOptions): CardsCollection {
        if (filter.isEmpty()) return collection
        val map = buildMap {
            collection.setsWithCards.forEach { entry ->
                val filteredCards = entry.value.filter { card ->

                    filter.nameFilter.isBlank() ||
                            card.name.contains(
                                filter.nameFilter,
                                ignoreCase = true
                            )

                }.filter { card ->

                    filter.mechanicFilter.isBlank() ||
                            card.mechanics.any {
                                it.name.contains(filter.mechanicFilter, ignoreCase = true)
                            }

                }.filter { card ->
                    filter.rarityFilter == null || card.rarity == filter.rarityFilter
                }
                if (filteredCards.isNotEmpty()) {
                    put(entry.key, filteredCards)
                }
            }
        }
        return CardsCollection(map)
    }
}

data class FilterOptions(
    // "" implies no filter
    val nameFilter: String = "",
    val mechanicFilter: String = "",
    val rarityFilter: Rarity? = null,
) {
    fun isEmpty(): Boolean {
        return nameFilter.isBlank() && mechanicFilter.isBlank() && rarityFilter == null
    }
}

sealed class CardsUiState {
    object Loading: CardsUiState()
    object Error: CardsUiState()
    class Result(val cardsCollection: CardsCollection): CardsUiState()
}