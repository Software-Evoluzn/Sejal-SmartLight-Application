package com.example.etcdynamiclight

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SetAlarmFromDatabase {

    @SuppressLint("ScheduleExactAlarm")
    fun fetchDataFromDataBase(fetchingScheduleData: ArrayList<ScheduleModel>, context: Context) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (models in fetchingScheduleData) {
            val startDate = dateFormat.parse(models.sch_sDate)
            val endDate = dateFormat.parse(models.sch_eDate)
            val startTime = timeFormat.parse(models.sch_sTime)
            val endTime = timeFormat.parse(models.sch_eTime)


            if (startDate != null && endDate != null && startTime != null && endTime != null) {
                val calendar = Calendar.getInstance()
                var currentDate = startDate
                while (!currentDate .after(endDate)) {
                        calendar.time = currentDate

                        //start time alarm
                        calendar.set(Calendar.HOUR_OF_DAY, startTime.hours)
                        calendar.set(Calendar.MINUTE, startTime.minutes)
                        calendar.set(Calendar.SECOND, 0)

                        val startIntent = Intent(context, AlarmReceiver::class.java).apply {
                            action="ON_ALARM"
                            putExtra("message", "T:5:G:G:1")
                        }
                        val startPendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), startIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, startPendingIntent)

                        //end time alarm
                        calendar.set(Calendar.HOUR_OF_DAY, endTime.hours)
                        calendar.set(Calendar.MINUTE, endTime.minutes)
                        val endIntent = Intent(context, AlarmReceiver::class.java).apply {
                            action="OFF_ALARM"
                            putExtra("message", "T:5:G:G:0")
                        }
                        val endPendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt()+1, endIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, endPendingIntent)

                        // Move to the next day
                        calendar.time = currentDate
                        calendar.add(Calendar.DATE, 1)
                        currentDate = calendar.time
                    }
            }
        }
    }

}