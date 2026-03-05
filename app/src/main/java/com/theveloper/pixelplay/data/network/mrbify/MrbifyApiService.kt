package com.theveloper.pixelplay.data.network.mrbify

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the mrbify API.
 * Base URL: https://mrbify.vercel.app/api/
 */
interface MrbifyApiService {

    // ─── Auth ──────────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: MrbifyLoginRequest): MrbifyUser

    @POST("auth/register")
    suspend fun register(@Body request: MrbifyRegisterRequest): MrbifyUser

    // ─── Music ─────────────────────────────────────────────────────────

    @GET("music/search")
    suspend fun search(
        @Query("q") query: String,
        @Header("x-yandex-token") yandexToken: String? = null
    ): MrbifySearchResponse

    @GET("music/stream")
    suspend fun stream(
        @Query("id") trackId: String,
        @Query("source") source: String,
        @Header("x-yandex-token") yandexToken: String? = null
    ): MrbifyStreamResponse

    @POST("music/find")
    suspend fun findTrack(@Body request: MrbifyFindRequest): MrbifyFindResponse

    @GET("music/artist")
    suspend fun getArtist(
        @Query("id") artistId: String,
        @Query("source") source: String
    ): MrbifyArtistDetail

    @GET("music/playlist")
    suspend fun getPlaylist(
        @Query("id") playlistId: String,
        @Query("source") source: String
    ): MrbifyPlaylistDetail

    @GET("music/recommendations")
    suspend fun getRecommendations(): MrbifyRecommendationsResponse

    @POST("music/import/link")
    suspend fun importLink(@Body request: MrbifyImportLinkRequest): MrbifyPlaylistDetail

    @POST("music/yandex/import")
    suspend fun importYandex(@Header("x-yandex-token") yandexToken: String): MrbifyYandexImportResponse

    @GET("music/yandex/playlists")
    suspend fun getYandexPlaylists(@Header("x-yandex-token") yandexToken: String): MrbifyYandexPlaylistsResponse

    // ─── Library: Likes ────────────────────────────────────────────────

    @GET("library/likes")
    suspend fun getLikes(@Query("userId") userId: String): List<MrbifyTrack>

    @POST("library/likes")
    suspend fun addLike(@Body request: MrbifyLikeRequest): MrbifyLikeResponse

    @PUT("library/likes")
    suspend fun replaceLike(@Body request: MrbifyReplaceLikeRequest): MrbifySuccessResponse

    // ─── Library: Playlists ────────────────────────────────────────────

    @GET("library/playlists")
    suspend fun getPlaylists(@Query("userId") userId: String): List<MrbifyPlaylist>

    @POST("library/playlists")
    suspend fun createPlaylist(@Body request: MrbifyPlaylistCreateRequest): MrbifyPlaylist

    @DELETE("library/playlists")
    suspend fun deletePlaylist(@Query("id") playlistId: String): MrbifySuccessResponse

    @PATCH("library/playlists")
    suspend fun updatePlaylist(@Body request: MrbifyPlaylistUpdateRequest): MrbifyPlaylist

    // ─── Library: Playlist Tracks ──────────────────────────────────────

    @POST("library/playlists/tracks")
    suspend fun addTrackToPlaylist(@Body request: MrbifyAddTrackToPlaylistRequest): MrbifyPlaylist

    @DELETE("library/playlists/tracks")
    suspend fun removeTrackFromPlaylist(
        @Query("playlistId") playlistId: String,
        @Query("trackId") trackId: String
    ): MrbifyPlaylist

    @PUT("library/playlists/tracks")
    suspend fun replaceTrackInPlaylist(@Body request: MrbifyReplaceTrackInPlaylistRequest): MrbifyPlaylist

    // ─── Library: History ──────────────────────────────────────────────

    @GET("library/history")
    suspend fun getLibraryHistory(@Query("userId") userId: String): List<MrbifyTrack>

    @POST("library/history")
    suspend fun addLibraryHistory(@Body request: MrbifyHistoryRequest): MrbifySuccessResponse

    // ─── Library: Import ───────────────────────────────────────────────

    @POST("library/import")
    suspend fun importLibrary(@Body request: MrbifyLibraryImportRequest): MrbifySuccessResponse

    // ─── User ──────────────────────────────────────────────────────────

    @GET("user/profile")
    suspend fun getProfile(@Query("userId") userId: String): MrbifyUser

    @PATCH("user/profile")
    suspend fun updateProfile(@Body request: MrbifyProfileUpdateRequest): MrbifyUser

    @GET("user/settings")
    suspend fun getSettings(@Query("userId") userId: String): Map<String, Any>

    @POST("user/settings")
    suspend fun saveSettings(@Body request: MrbifySettingsSaveRequest): MrbifySuccessResponse

    @GET("user/state")
    suspend fun getState(@Query("userId") userId: String): Map<String, Any>

    @POST("user/state")
    suspend fun saveState(@Body request: MrbifyStateSaveRequest): MrbifySuccessResponse

    @GET("user/history")
    suspend fun getUserHistory(@Query("userId") userId: String): List<MrbifyTrack>

    @POST("user/history")
    suspend fun addUserHistory(@Body request: MrbifyUserHistoryRequest): MrbifySuccessResponse

    // ─── Themes ────────────────────────────────────────────────────────

    @GET("themes")
    suspend fun getThemes(@Query("sort") sort: String = "new"): List<MrbifyTheme>

    @POST("themes")
    suspend fun publishTheme(@Body request: MrbifyThemePublishRequest): MrbifyTheme

    @POST("themes/{id}/install")
    suspend fun installTheme(@Path("id") themeId: String): MrbifyTheme
}
