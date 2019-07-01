package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

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
        abs(dif) / SECONDS.toDouble() <= 1  -> {"только что"}
        abs(dif) / SECONDS.toDouble() <= 45 -> {"несколько секунд"}
        abs(dif) / SECONDS.toDouble() <= 75 -> {"минуту"}
        abs(dif) / MINUTE.toDouble() <= 45 -> {"${(abs(dif) / MINUTE).toInt()} ${getNumForm((dif / MINUTE).toInt(), TimeUnits.MINUTE)}"}
        abs(dif) / MINUTE.toDouble() <= 75 -> {"час"}
        abs(dif) / HOUR.toDouble() <= 22 -> {"${(abs(dif) / HOUR).toInt()} ${getNumForm((dif / HOUR).toInt(), TimeUnits.HOUR)}"}
        abs(dif) / HOUR.toDouble() <= 26 -> {"день"}
        abs(dif) / DAY.toDouble() <= 360 -> {"${(abs(dif) / DAY).toInt()} ${getNumForm((dif / DAY).toInt(), TimeUnits.DAY)}"}
        else ->{""}
    }

    when{
        dif / DAY.toDouble() < -360 -> interval = "более года назад"
        dif / SECONDS.toDouble() < -1  -> interval += " назад"
        dif / SECONDS.toDouble() <= 1 -> {}
        dif / DAY.toDouble() <= 360  -> interval = "через $interval"
        else -> interval = "более чем через год"
    }

    return interval
}

 fun getNumForm(amount: Int, units: TimeUnits): String {
    val posAmount = abs(amount)

    return when(posAmount){
        0, in 5..19 -> Plurals.MANY.get(units)
        1 -> Plurals.ONE.get(units)
        in 2..4 -> Plurals.FEW.get(units)
        else -> getNumForm(reduceDigitsNum(posAmount) ,units)
    }
}

private fun reduceDigitsNum(number: Int): Int {
    return number % (10.0.pow((number.toString().length - 1).toDouble())).toInt()
}

enum class Plurals(private val minute: String, private val hour: String, private val day: String){
    ONE("минуту", "час", "день"),
    FEW("минуты", "часа", "дня"),
    MANY("минут", "часов", "дней");

    fun get(unit: TimeUnits): String {
        return when(unit){
            TimeUnits.MINUTE -> minute
            TimeUnits.HOUR -> hour
            TimeUnits.DAY -> day
            else -> ""
        }
    }
}

enum class TimeUnits{
    SECOND,
    MINUTE,
    HOUR,
    DAY
}

