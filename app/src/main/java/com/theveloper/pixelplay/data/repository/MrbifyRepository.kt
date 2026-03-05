package com.theveloper.pixelplay.data.repository

import com.theveloper.pixelplay.data.network.mrbify.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MrbifyRepository @Inject constructor(
    private val apiService: MrbifyApiService
) {

    // ─── Auth ──────────────────────────────────────────────────────────

    suspend fun login(request: MrbifyLoginRequest): Result<MrbifyUser> = runCatching {
        apiService.login(request)
    }

    suspend fun register(request: MrbifyRegisterRequest): Result<MrbifyUser> = runCatching {
        apiService.register(request)
    }

    // ─── Music ─────────────────────────────────────────────────────────

    suspend fun searchOnline(query: String, yandexToken: String? = null): Result<MrbifySearchResponse> = runCatching {
        apiService.search(query, yandexToken)
    }

    suspend fun getStreamUrl(trackId: String, source: String, yandexToken: String? = null): Result<MrbifyStreamResponse> = runCatching {
        apiService.stream(trackId, source, yandexToken)
    }

    suspend fun findTrack(request: MrbifyFindRequest): Result<MrbifyFindResponse> = runCatching {
        apiService.findTrack(request)
    }

    suspend fun getArtistDetail(artistId: String, source: String): Result<MrbifyArtistDetail> = runCatching {
        apiService.getArtist(artistId, source)
    }

    suspend fun getPlaylistDetail(playlistId: String, source: String): Result<MrbifyPlaylistDetail> = runCatching {
        apiService.getPlaylist(playlistId, source)
    }

    suspend fun getRecommendations(): Result<MrbifyRecommendationsResponse> = runCatching {
        apiService.getRecommendations()
    }

    // ─── Library ───────────────────────────────────────────────────────

    suspend fun getLikes(userId: String): Result<List<MrbifyTrack>> = runCatching {
        apiService.getLikes(userId)
    }

    suspend fun addLike(userId: String, track: MrbifyTrack): Result<MrbifyLikeResponse> = runCatching {
        apiService.addLike(MrbifyLikeRequest(userId, track))
    }

    suspend fun getPlaylists(userId: String): Result<List<MrbifyPlaylist>> = runCatching {
        apiService.getPlaylists(userId)
    }

    suspend fun createPlaylist(userId: String, name: String, description: String? = null): Result<MrbifyPlaylist> = runCatching {
        apiService.createPlaylist(MrbifyPlaylistCreateRequest(userId, name, description))
    }

    // ─── History ───────────────────────────────────────────────────────

    suspend fun getHistory(userId: String): Result<List<MrbifyTrack>> = runCatching {
        apiService.getLibraryHistory(userId)
    }

    suspend fun addHistory(userId: String, track: MrbifyTrack): Result<MrbifySuccessResponse> = runCatching {
        apiService.addLibraryHistory(MrbifyHistoryRequest(userId, track))
    }
}
