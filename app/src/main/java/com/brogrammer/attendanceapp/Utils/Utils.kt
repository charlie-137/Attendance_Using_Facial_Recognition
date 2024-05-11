package com.brogrammer.imageclassificationlivefeed.Utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    @JvmStatic
    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }



    fun generateDateList(startDate: Int, endDate: Int): List<String> {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val dateList = mutableListOf<String>()
        calendar.time = dateFormat.parse(startDate.toString()) ?: Date()

        while (calendar.time <= dateFormat.parse(endDate.toString()) ?: Date()) {
            dateList.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateList
    }



}