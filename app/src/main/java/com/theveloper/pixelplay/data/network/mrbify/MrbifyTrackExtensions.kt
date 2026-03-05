package com.theveloper.pixelplay.data.network.mrbify

import com.theveloper.pixelplay.data.model.Song

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
        artist = this.artist,
        artistId = 0L,
        album = "",
        albumId = 0L,
        albumArtist = this.artist,
        path = contentUriString,
        contentUriString = contentUriString,
        albumArtUriString = this.coverUrl.takeIf { it.isNotBlank() },
        duration = durationMs,
        mimeType = "audio/mpeg",
        bitrate = 0,
        sampleRate = 0,
        isFavorite = false,
        trackNumber = 0,
        year = 0,
        dateModified = System.currentTimeMillis() / 1000
    )
}
