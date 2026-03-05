package com.theveloper.pixelplay.data.stream

import android.net.Uri
import com.theveloper.pixelplay.data.repository.MrbifyRepository
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MrbifyStreamProxy @Inject constructor(
    private val repository: MrbifyRepository,
    okHttpClient: OkHttpClient
) : CloudStreamProxy<String>(okHttpClient) {

    // mrbify streams from Soundcloud, Yandex, Spotify
    override val allowedHostSuffixes = setOf(
        "sndcdn.com",
        "soundcloud.com",
        "yandex.net",
        "spotify.com",
        "scdn.co"
    )

    override val cacheExpirationMs = 30L * 60 * 1000 // 30 minutes
    override val proxyTag = "MrbifyStreamProxy"
    
    // Route matching: /mrbify/source/id
    override val routePath = "/mrbify/{sourceAndId...}"
    override val routeParamName = "sourceAndId"
    override val uriScheme = "mrbify"
    override val routePrefix = "/mrbify"

    /** 
     * Parses the route param "source/trackId"
     * Since Ktor path parameters can contain slashes if using wildcard,
     * we expect "{source}/{id}" merged. We will encode it as "{source}:{id}" 
     * in the route param to be simpler.
     */
    override fun parseRouteParam(value: String): String? =
        value.takeIf { it.isNotBlank() }

    override fun validateId(id: String): Boolean {
        // ID format: "source:trackId"
        val parts = id.split(":")
        return parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()
    }

    override fun formatIdForUrl(id: String): String = id

    override suspend fun resolveStreamUrl(id: String): String? {
        val parts = id.split(":")
        if (parts.size != 2) return null
        val source = parts[0]
        val trackId = parts[1]
        
        val response = repository.getStreamUrl(trackId, source).getOrNull()
        return response?.url
    }

    /**
     * mrbify://source:trackId
     */
    override fun extractIdFromUri(uri: Uri): String? {
        val host = uri.host ?: return null
        val port = uri.port.takeIf { it != -1 } ?: return null
        // e.g. mrbify://soundcloud:12345
        // uri.host = soundcloud, uri.port = 12345
        return "$host:$port" // Actually, if it's mrbify://soundcloud:12345, the authority handles it natively.
    }

    fun resolveMrbifyUri(uriString: String): String? {
        // Ensure proper parsing. "mrbify://soundcloud:12345"
        // android.net.Uri parse might treat "soundcloud" as host, port as 12345.
        // Let's use a simpler path based approach: mrbify:///soundcloud/12345
        if (uriString.startsWith("mrbify://")) {
            val uri = Uri.parse(uriString)
            val host = uri.host
            val pathId = uri.path?.removePrefix("/")
            if (host != null) {
                if (pathId.isNullOrBlank()) {
                    // It might have been parsed differently
                    val schemeSpecificPart = uri.schemeSpecificPart.removePrefix("//")
                    // e.g. schemeSpecificPart is "soundcloud:12345"
                    return resolveUri("mrbify", schemeSpecificPart)
                }
            }
        }
        return resolveUri(uriString)
    }
    
    /** Custom resolve helper. */
    private fun resolveUri(scheme: String, id: String): String? {
        if (!validateId(id)) return null
        return getProxyUrl(id)
    }
}
