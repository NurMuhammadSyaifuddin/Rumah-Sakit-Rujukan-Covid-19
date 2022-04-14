package com.project.rumahsakitrujukancovid_19.utils

import android.content.Context
import android.os.Build
import com.project.rumahsakitrujukancovid_19.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class RegistrationDateFormatter {

    fun getRangeDayOfRegistrationDate(context: Context): List<String> {
        val formatter = SimpleDateFormat("dd M yyyy", Locale.US)
        val currentDay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().dayOfWeek.value + 1
        } else {
            Calendar.getInstance().time.day + 1
        }

        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = currentDay
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }

        val dates = mutableListOf<String>()

        for (i in calendar.firstDayOfWeek..Calendar.FRIDAY) {
            dates.add(formatter.format(calendar.time))
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        val days = mutableListOf<String>()

        val newDates = mutableListOf<String>()

        if (currentDay != 1 || currentDay != 7) {
            for (i in currentDay..Calendar.FRIDAY) {
                when (i) {
                    2 -> days.add(context.getString(R.string.monday))
                    3 -> days.add(context.getString(R.string.tuesday))
                    4 -> days.add(context.getString(R.string.wednesday))
                    5 -> days.add(context.getString(R.string.thursday))
                    6 -> days.add(context.getString(R.string.friday))
                }
            }

            val months = mutableListOf<String>()
            dates
                .map {
                    it.split("").toTypedArray()[1].toInt()
                }
                .forEach { month ->
                    when (month - 1) {
                        Calendar.JANUARY -> months.add(context.getString(R.string.january))
                        Calendar.FEBRUARY -> months.add(context.getString(R.string.february))
                        Calendar.MARCH -> months.add(context.getString(R.string.march))
                        Calendar.APRIL -> months.add(context.getString(R.string.april))
                        Calendar.MAY -> months.add(context.getString(R.string.may))
                        Calendar.JUNE -> months.add(context.getString(R.string.june))
                        Calendar.JULY -> months.add(context.getString(R.string.july))
                        Calendar.AUGUST -> months.add(context.getString(R.string.august))
                        Calendar.SEPTEMBER -> months.add(context.getString(R.string.september))
                        Calendar.OCTOBER -> months.add(context.getString(R.string.october))
                        Calendar.NOVEMBER -> months.add(context.getString(R.string.november))
                        Calendar.DECEMBER -> months.add(context.getString(R.string.december))
                    }
                }

            for (i in 0 until dates.size) {
                val dateSplit = dates[i].split(" ").toTypedArray()
                val day = dateSplit[0]
                val month = months[i]
                val year = dateSplit[2]

                val finalDate = listOf("${days[i]},", day, month, year).joinToString(" ")
                newDates.add(finalDate)
            }

        }
        return newDates
    }

    fun isWeekdays(context: Context): Boolean =
        getRangeDayOfRegistrationDate(context).isNotEmpty()

}