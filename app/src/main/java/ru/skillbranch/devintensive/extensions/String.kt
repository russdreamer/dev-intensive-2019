package ru.skillbranch.devintensive.extensions

fun String.truncate(endIndex: Int = 16): String{
    val trimmedMes = this.trim()
    return if (trimmedMes.length <= endIndex + 1) trimmedMes else trimmedMes.substring(0, endIndex + 1).trim() + "..."
}

fun String.stripHtml(): String{
    val htmlRegex = Regex("(<.*?>)|(&[^ а-я]{1,4}?;)")
    val spaceRegex = Regex(" {2,}")
    return this.replace(htmlRegex, "").replace(spaceRegex, " ")
}