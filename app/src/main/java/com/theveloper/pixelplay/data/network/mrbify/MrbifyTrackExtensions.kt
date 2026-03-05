package com.theveloper.pixelplay.data.network.mrbify

import androidx.core.net.toUri
import com.theveloper.pixelplay.data.model.Song
import com.theveloper.pixelplay.data.model.SongSource

/**
 * Extension methods to convert Mrbify API models to PixelPlayer internal models.
 */

fun MrbifyTrack.toSong(): Song {
    // Content URI scheme expected by MrbifyStreamProxy is "mrbify://source:trackId"
    // e.g., "mrbify://soundcloud:12345"
    val contentUriString = "mrbify://${this.source}:${this.id}"
    
    // Convert duration from seconds to milliseconds
    val durationMs = this.duration * 1000L

    return Song(
        id = contentUriString, // use URI as unique ID so it plays correctly
        title = this.title,
        trackNumber = 0,
        year = 0,
        duration = durationMs,
        data = contentUriString,
        dateModified = System.currentTimeMillis() / 1000,
        albumId = 0L,
        albumName = "Unknown Album",
        artistId = 0L,
        artistName = this.artist,
        composer = "",
        albumArtist = this.artist,
        contentUri = contentUriString.toUri(),
        mimeType = "audio/mpeg", // standard assumption for streams
        size = 0L,
        bitrate = 0,
        sampleRate = 0,
        format = "mp3",
        folderName = "Mrbify",
        albumArtUriString = this.coverUrl.takeIf { it.isNotBlank() },
        source = SongSource.NETEASE, // Temporary fallback if we need it, though custom stream proxy handles it. Actually we should use a custom source if possible, but NETEASE avoids MediaStore limits. Let's use NETEASE for now or define a new one if needed, but the proxy looks at the URI scheme.
        isFavorite = false // We'll handle this separately
    )
}
