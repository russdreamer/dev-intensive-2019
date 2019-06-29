package ru.skillbranch.devintensive.extensions

import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECONDS = 1000L
const val MINUTE = 60 * SECONDS
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECONDS
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time

    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val dif = this.time -  date.time

    var interval = when {
        abs(dif) / SECONDS <= 1  -> {"только что"}
        abs(dif) / SECONDS <= 45 -> {"несколько секунд"}
        abs(dif) / SECONDS <= 75 -> {"минуту"}
        abs(dif) / MINUTE <= 45 -> {"${(abs(dif) / MINUTE).toInt()} ${getNumForm((dif / MINUTE).toInt(), TimeUnits.MINUTE)}"}
        abs(dif) / MINUTE <= 75 -> {"час"}
        abs(dif) / HOUR <= 22 -> {"${(abs(dif) / HOUR).toInt()} ${getNumForm((dif / HOUR).toInt(), TimeUnits.HOUR)}"}
        abs(dif) / HOUR <= 26 -> {"день"}
        abs(dif) / DAY <= 360 -> {"${(abs(dif) / DAY).toInt()} ${getNumForm((dif / DAY).toInt(), TimeUnits.DAY)}"}
        else ->{""}
    }

    when{
        dif / DAY < -360 -> interval = "более года назад"
        dif / SECONDS < -1  -> interval += " назад"
        dif / SECONDS <= 1 -> {}
        dif / DAY <= 360  -> interval = "через $interval"
        else -> interval = "более чем через год"
    }

    return interval
}

private fun getNumForm(amount: Int, units: TimeUnits): String {
    val posAmount = abs(amount)

    return when(units){
        TimeUnits.MINUTE -> when(posAmount){
            0, in 5..19 -> "минут"
            1 -> "минуту"
            in 2..4 -> "минуты"
            else -> getNumForm(reduceDigitsNum(posAmount) ,TimeUnits.MINUTE)
        }
        TimeUnits.HOUR -> when(posAmount){
            0, in 5..19 -> "часов"
            1 -> "час"
            in 2..4 -> "часа"
            else -> getNumForm(reduceDigitsNum(posAmount) ,TimeUnits.HOUR)
        }
        TimeUnits.DAY -> when(posAmount){
            0, in 5..19 -> "дней"
            1 -> "день"
            in 2..4 -> "дня"
            else -> getNumForm(reduceDigitsNum(posAmount) ,TimeUnits.DAY)
        }
        else -> throw IllegalArgumentException()
    }
}

private fun reduceDigitsNum(number: Int): Int {
    return number % (10 * (number.toString().length - 1))
}

enum class TimeUnits{
    SECOND,
    MINUTE,
    HOUR,
    DAY
}

