package ru.skillbranch.devintensive.extensions

fun String.truncate(endIndex: Int = 16): String{
    val trimmedMes = this.trim()
    val subLine = trimmedMes.substring(0, endIndex + 1).trim()
    return if (subLine.length < trimmedMes.length) "$subLine..." else subLine
}

fun String.stripHtml(): String{
    val htmlRegex = Regex("<.*?>|&amp;|&lt;|&gt;|&#39;|&quot;|&apos;")
    val spaceRegex = Regex("\\s{2,}")
    return this.replace(htmlRegex, "").replace(spaceRegex, " ")
}