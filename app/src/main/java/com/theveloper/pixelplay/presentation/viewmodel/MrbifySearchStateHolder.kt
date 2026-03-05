package com.theveloper.pixelplay.presentation.viewmodel

import android.util.Log
import com.theveloper.pixelplay.data.network.mrbify.MrbifySearchResponse
import com.theveloper.pixelplay.data.repository.MrbifyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages online search state for mrbify API.
 */
@Singleton
class MrbifySearchStateHolder @Inject constructor(
    private val repository: MrbifyRepository
) {
    private companion object {
        const val SEARCH_DEBOUNCE_MS = 500L
    }

    private data class SearchRequest(
        val query: String,
        val requestId: Long
    )

    private val _searchResults = MutableStateFlow<MrbifySearchResponse?>(null)
    val searchResults = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val searchRequests = MutableSharedFlow<SearchRequest>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val latestSearchRequestId = AtomicLong(0L)

    private var scope: CoroutineScope? = null
    private var searchJob: Job? = null

    fun initialize(scope: CoroutineScope) {
        this.scope = scope
        observeSearchRequests()
    }

    private fun observeSearchRequests() {
        searchJob?.cancel()
        searchJob = scope?.launch {
            searchRequests
                .debounce(SEARCH_DEBOUNCE_MS)
                .collectLatest { request ->
                    val normalizedQuery = request.query

                    if (normalizedQuery.isBlank()) {
                        _searchResults.value = null
                        _isSearching.value = false
                        return@collectLatest
                    }

                    _isSearching.value = true

                    try {
                        val responseResult = withContext(Dispatchers.IO) {
                            repository.searchOnline(normalizedQuery)
                        }

                        if (request.requestId != latestSearchRequestId.get()) {
                            return@collectLatest
                        }

                        _searchResults.value = responseResult.getOrNull()
                    } catch (_: CancellationException) {
                        // Superseded
                    } catch (e: Exception) {
                        if (request.requestId == latestSearchRequestId.get()) {
                            Log.e("MrbifySearch", "API search error", e)
                            _searchResults.value = null
                        }
                    } finally {
                        if (request.requestId == latestSearchRequestId.get()) {
                            _isSearching.value = false
                        }
                    }
                }
        }
    }

    fun performSearch(query: String) {
        val normalizedQuery = query.trim()
        val requestId = latestSearchRequestId.incrementAndGet()

        if (normalizedQuery.isBlank()) {
            _searchResults.value = null
            _isSearching.value = false
        } else {
            _isSearching.value = true
        }

        searchRequests.tryEmit(SearchRequest(normalizedQuery, requestId))
    }

    fun clearSearch() {
        latestSearchRequestId.incrementAndGet()
        _searchResults.value = null
        _isSearching.value = false
    }

    fun onCleared() {
        searchJob?.cancel()
        scope = null
    }
}
