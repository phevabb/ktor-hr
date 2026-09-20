package com.hr.config




import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import java.net.URI

fun Application.configureCors() {

    install(CORS) {

        /**
         * Production frontend:
         *
         * Replace stoollands.com with your real production domain.
         */
        allowOrigins { origin ->
            val parsed = parseOrigin(origin)
                ?: return@allowOrigins false

            parsed.scheme == "https" &&
                    (
                            parsed.host == "stoollands.com" ||
                                    parsed.host == "www.stoollands.com"
                            )
        }

        /**
         * Local/testing frontend:
         *
         * http://localhost:8080
         * http://127.0.0.1:8080
         */
        allowOrigins { origin ->
            val parsed = parseOrigin(origin)
                ?: return@allowOrigins false

            parsed.scheme == "http" &&
                    parsed.port == 8080 &&
                    (
                            parsed.host == "localhost" ||
                                    parsed.host == "127.0.0.1"
                            )
        }

        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.Origin)
        allowHeader("X-Requested-With")

        exposeHeader(HttpHeaders.ContentDisposition)

        allowNonSimpleContentTypes = true
        allowCredentials = true

        maxAgeInSeconds = 3600
    }
}

private data class ParsedOrigin(
    val scheme: String,
    val host: String,
    val port: Int
)

private fun parseOrigin(
    origin: String
): ParsedOrigin? {

    return try {

        val uri = URI(origin)

        val scheme =
            uri.scheme?.lowercase()
                ?: return null

        val host =
            uri.host?.lowercase()
                ?: return null

        val port = when {
            uri.port != -1 -> uri.port
            scheme == "https" -> 443
            scheme == "http" -> 80
            else -> -1
        }

        ParsedOrigin(
            scheme = scheme,
            host = host,
            port = port
        )

    } catch (e: Exception) {
        null
    }
}
