package com.tornado.hearthstonecards.common.repository

import com.tornado.hearthstonecards.common.data.Card
import com.tornado.hearthstonecards.common.data.CardsCollection
import com.tornado.hearthstonecards.common.viewmodel.CardsUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface CardRepository {
    val cards: Flow<RepositoryResult<CardsCollection>>
    suspend fun loadCards()
    companion object {
        fun createInstance(): CardRepository = CardRepositoryImpl()
    }
}
class CardRepositoryImpl(val dispatcher: CoroutineDispatcher = Dispatchers.IO): CardRepository {

    private val loading = RepositoryResult.Loading<CardsCollection>()
    private val _cards = MutableStateFlow<RepositoryResult<CardsCollection>?>(loading)
    override val cards: Flow<RepositoryResult<CardsCollection>> = _cards.filterNotNull()

    override suspend fun loadCards() {
        _cards.emit(loading)
        CoroutineScope(dispatcher).launch {
            try {
                val data = CardRepository::class.java.getResourceAsStream("/cards.json")!!.bufferedReader().use {
                    it.readText()
                }

                val cards = Json.decodeFromString<Map<String, List<Card>>>(data)
                _cards.emit(RepositoryResult.Success(CardsCollection(cards)))
            } catch (e: Exception) {
                // This printStackTrace would not be in production code..
                e.printStackTrace()
                _cards.emit(RepositoryResult.Error(e))
            }
        }
    }
}

sealed class RepositoryResult<T> {
    class Error<T>(val e: Exception): RepositoryResult<T>()
    class Success<T>(val collection: CardsCollection): RepositoryResult<T>()
    class Loading<T>(): RepositoryResult<T>()
}