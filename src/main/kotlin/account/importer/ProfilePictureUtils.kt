package com.hr.account.importer

object ProfilePictureUtils {

    fun extractUrl(value: String?): String? {

        if (value.isNullOrBlank()) {
            return null
        }

        val regex = Regex("""https://[^\s"<>]+""")

        return regex.find(value)?.value
    }

    fun extractPublicId(url: String?): String? {

        if (url.isNullOrBlank()) {
            return null
        }

        val marker = "/upload/"

        val index = url.indexOf(marker)

        if (index == -1) {
            return null
        }

        return url.substring(index + marker.length)
            .replace(Regex("^v\\d+/"), "")
    }
}