package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String, String>{
        val parts = fullName?.split(" ")
        val firstName = parts?.getOrNull(0).orEmpty().trim().ifEmpty { "null" }
        val lastName = parts?.getOrNull(1).orEmpty().trim().ifEmpty { "null" }

        return firstName to lastName
    }

    fun transliteration(payload: String, devider: String = " "): String {
        TODO("implement")
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        TODO("implement")
    }
}