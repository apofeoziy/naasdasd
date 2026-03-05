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
    
    // Route: /mrbify/{source}/{trackId...}
    // Двоеточие в URL-пути ненадёжно (некоторые HTTP-стеки его не принимают),
    // поэтому используем слеш как разделитель source/trackId в URL.
    // ID внутри приложения по-прежнему хранится как "source:trackId".
    override val routePath = "/mrbify/{source}/{trackId...}"
    override val routeParamName = "source" // не используется напрямую; парсинг ниже
    override val uriScheme = "mrbify"
    override val routePrefix = "/mrbify"

    /**
     * route param приходит как "source" — но нам нужны оба сегмента.
     * Реальный парсинг делается в createServer через call.parameters, поэтому
     * здесь parseRouteParam принимает уже собранную строку "source/trackId".
     * Мы конвертируем её обратно в "source:trackId" (внутренний формат).
     */
    override fun parseRouteParam(value: String): String? {
        // value приходит в двух форматах:
        // 1. "source/trackId"  — когда собран вручную из двух параметров
        // 2. "source:trackId"  — когда передаётся напрямую (resolveUri)
        return when {
            value.contains('/') -> {
                val slash = value.indexOf('/')
                val source = value.substring(0, slash)
                val trackId = value.substring(slash + 1)
                if (source.isBlank() || trackId.isBlank()) null else "$source:$trackId"
            }
            value.contains(':') -> value.takeIf { it.isNotBlank() }
            else -> null
        }
    }

    override fun validateId(id: String): Boolean {
        // ID format: "source:trackId" (trackId itself may contain colons, e.g. Spotify URIs)
        val colonIndex = id.indexOf(':')
        if (colonIndex <= 0) return false
        val source = id.substring(0, colonIndex)
        val trackId = id.substring(colonIndex + 1)
        return source.isNotBlank() && trackId.isNotBlank()
    }

    /**
     * Конвертируем внутренний формат "source:trackId" → URL-путь "source/trackId".
     * Это безопасно в URL-пути и нативно обрабатывается Ktor wildcard.
     */
    override fun formatIdForUrl(id: String): String {
        val colonIndex = id.indexOf(':')
        if (colonIndex <= 0) return id
        val source = id.substring(0, colonIndex)
        val trackId = id.substring(colonIndex + 1)
        return "$source/$trackId"
    }

    override suspend fun resolveStreamUrl(id: String): String? {
        val colonIndex = id.indexOf(':')
        if (colonIndex <= 0) return null
        val source = id.substring(0, colonIndex)
        val trackId = id.substring(colonIndex + 1)
        
        val response = repository.getStreamUrl(trackId, source).getOrNull()
        return response?.url
    }

    /**
     * mrbify://source:trackId
     */
    override fun extractIdFromUri(uri: Uri): String? {
        // URI format: mrbify://soundcloud:trackId
        // Android Uri parses host=soundcloud, port=trackId only when trackId is numeric.
        // For non-numeric IDs (e.g. Spotify), port == -1 and the full authority is "soundcloud:trackId".
        // Use the raw schemeSpecificPart which is always "//source:trackId" regardless of type.
        val ssp = uri.schemeSpecificPart?.removePrefix("//") ?: return null
        return ssp.takeIf { it.contains(":") && it.isNotBlank() }
    }

    /**
     * Ktor даёт нам {source} и {trackId...} как отдельные параметры.
     * Собираем их в "source/trackId" — parseRouteParam затем конвертирует в "source:trackId".
     */
    override fun extractIdFromCallParameters(
        params: io.ktor.server.routing.RoutingCall
    ): String? {
        val source = params.parameters["source"] ?: return null
        val trackId = params.parameters.getAll("trackId")?.joinToString("/") ?: return null
        if (source.isBlank() || trackId.isBlank()) return null
        return "$source/$trackId"
    }

    /**
     * Прогревает кэш стрим URL для данного mrbify URI, чтобы при обращении ExoPlayer
     * прокси реальный URL уже был в urlCache и не требовал нового API-запроса.
     */
    suspend fun warmUpStreamUrl(uriString: String) {
        val uri = android.net.Uri.parse(uriString)
        val id = extractIdFromUri(uri) ?: return
        val parsedId = parseRouteParam(id) ?: return
        if (!validateId(parsedId)) return
        getOrFetchStreamUrl(parsedId) // заполняет urlCache
    }

    fun resolveMrbifyUri(uriString: String): String? {
        // Delegate to the base class resolveUri which uses extractIdFromUri internally.
        // extractIdFromUri now correctly handles all ID formats via schemeSpecificPart.
        return resolveUri(uriString)
    }
}
