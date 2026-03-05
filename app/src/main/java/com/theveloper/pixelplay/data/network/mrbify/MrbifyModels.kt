package com.theveloper.pixelplay.data.network.mrbify

import com.google.gson.annotations.SerializedName

// ─── Auth ──────────────────────────────────────────────────────────

data class MrbifyLoginRequest(
    val username: String,
    val password: String
)

data class MrbifyRegisterRequest(
    val username: String,
    val password: String,
    val token: String? = null,
    val avatarUrl: String? = null
)

data class MrbifyUser(
    val id: String,
    val username: String,
    val token: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String? = null
)

// ─── Music: Tracks / Artists / Playlists ────────────────────────────

data class MrbifyTrack(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String = "",
    val duration: Int = 0,       // seconds
    val source: String = "",     // "soundcloud", "yandex", "spotify"
    val audioUrl: String = ""
)

data class MrbifyArtistSummary(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val followers: Long = 0,
    val source: String = ""
)

data class MrbifyPlaylistSummary(
    val id: String,
    val name: String,
    val coverUrl: String = "",
    val description: String = "",
    val source: String = "",
    val isSystem: Boolean = false,
    val tracks: List<MrbifyTrack> = emptyList()
)

// ─── Music: Search ─────────────────────────────────────────────────

data class MrbifySearchResponse(
    val tracks: List<MrbifyTrack> = emptyList(),
    val artists: List<MrbifyArtistSummary> = emptyList(),
    val playlists: List<MrbifyPlaylistSummary> = emptyList()
)

// ─── Music: Stream ─────────────────────────────────────────────────

data class MrbifyStreamResponse(
    val url: String? = null,
    val error: String? = null
)

// ─── Music: Find (cross-service) ───────────────────────────────────

data class MrbifyFindRequest(
    val title: String,
    val artist: String,
    val targetSource: String
)

data class MrbifyFindResponse(
    val results: List<MrbifyTrack> = emptyList()
)

// ─── Music: Artist Detail ──────────────────────────────────────────

data class MrbifyArtistDetail(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val followers: Long = 0,
    val city: String = "",
    val tracks: List<MrbifyTrack> = emptyList(),
    val albums: List<MrbifyPlaylistSummary> = emptyList()
)

// ─── Music: Playlist Detail ────────────────────────────────────────

data class MrbifyPlaylistDetail(
    val id: String,
    val name: String,
    val coverUrl: String = "",
    val description: String = "",
    val tracks: List<MrbifyTrack> = emptyList(),
    val source: String = ""
)

// ─── Music: Recommendations ────────────────────────────────────────

data class MrbifyRecommendationsResponse(
    val title: String = "",
    val playlists: List<MrbifyPlaylistSummary> = emptyList()
)

// ─── Music: Import Link ────────────────────────────────────────────

data class MrbifyImportLinkRequest(
    val url: String
)

// ─── Music: Yandex Playlists ───────────────────────────────────────

data class MrbifyYandexPlaylist(
    val id: String,
    val title: String,
    val coverUrl: String = "",
    val trackCount: Int = 0,
    val uid: String = ""
)

data class MrbifyYandexPlaylistsResponse(
    val playlists: List<MrbifyYandexPlaylist> = emptyList(),
    val uid: String = ""
)

// ─── Music: Yandex Import ──────────────────────────────────────────

data class MrbifyYandexImportResponse(
    val importedPlaylists: List<MrbifyPlaylistDetail> = emptyList(),
    val importedLikes: List<MrbifyTrack> = emptyList()
)

// ─── Library: Likes ────────────────────────────────────────────────

data class MrbifyLikeRequest(
    val userId: String,
    val track: MrbifyTrack
)

data class MrbifyLikeResponse(
    val liked: Boolean = false
)

data class MrbifyReplaceLikeRequest(
    val userId: String,
    val oldTrackId: String,
    val newTrack: MrbifyTrack
)

// ─── Library: Playlists ────────────────────────────────────────────

data class MrbifyPlaylistCreateRequest(
    val userId: String,
    val name: String,
    val description: String? = null
)

data class MrbifyPlaylistUpdateRequest(
    val id: String,
    val name: String,
    val description: String? = null,
    val coverUrl: String? = null
)

data class MrbifyPlaylist(
    val id: String,
    val name: String,
    val description: String? = null,
    val coverUrl: String? = null,
    val userId: String? = null,
    val isSystem: Boolean = false,
    val createdAt: String? = null,
    val tracks: List<MrbifyTrack> = emptyList()
)

// ─── Library: Playlist Tracks ──────────────────────────────────────

data class MrbifyAddTrackToPlaylistRequest(
    val playlistId: String,
    val track: MrbifyTrack
)

data class MrbifyReplaceTrackInPlaylistRequest(
    val playlistId: String,
    val oldTrackId: String,
    val newTrack: MrbifyTrack
)

// ─── Library: History ──────────────────────────────────────────────

data class MrbifyHistoryRequest(
    val userId: String,
    val track: MrbifyTrack
)

// ─── Library: Import ───────────────────────────────────────────────

data class MrbifyLibraryImportRequest(
    val userId: String,
    val playlists: List<MrbifyPlaylistSummary>? = null,
    val likedTracks: List<MrbifyTrack>? = null
)

// ─── User: Profile ─────────────────────────────────────────────────

data class MrbifyProfileUpdateRequest(
    val userId: String,
    val token: String? = null,
    val avatarUrl: String? = null
)

// ─── User: Settings ────────────────────────────────────────────────

data class MrbifySettingsSaveRequest(
    val userId: String,
    val settings: Map<String, Any?>
)

// ─── User: Player State ────────────────────────────────────────────

data class MrbifyStateSaveRequest(
    val userId: String,
    val state: Map<String, Any?>
)

// ─── User: History ─────────────────────────────────────────────────

data class MrbifyUserHistoryRequest(
    val userId: String,
    val track: MrbifyTrack
)

// ─── Themes ────────────────────────────────────────────────────────

data class MrbifyTheme(
    val id: String,
    val name: String,
    val config: Any? = null,       // JSON config
    val authorId: String? = null,
    val author: MrbifyThemeAuthor? = null,
    val downloads: Int = 0,
    val createdAt: String? = null
)

data class MrbifyThemeAuthor(
    val username: String
)

data class MrbifyThemePublishRequest(
    val name: String,
    val config: Any
)

// ─── Generic Success ───────────────────────────────────────────────

data class MrbifySuccessResponse(
    val success: Boolean = false,
    val trackId: String? = null,
    val message: String? = null
)

data class MrbifyErrorResponse(
    val error: String? = null
)
