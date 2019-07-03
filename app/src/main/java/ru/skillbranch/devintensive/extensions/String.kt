package ru.skillbranch.devintensive.extensions

fun String.truncate(endIndex: Int = 16): String{
    val subLine = this.substring(0, endIndex + 1).trim()
    return if (subLine.length < this.trim().length) "$subLine..." else subLine
}