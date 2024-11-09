package com.example.etcdynamiclight

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SetAlarmFromDatabase {

    fun fetchDataFromDataBase(fetchingScheduleData: ArrayList<ScheduleModel>) {

        val dateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat=SimpleDateFormat("hh:mm a", Locale.getDefault()) //input time format
        val timeFormat24Hour=SimpleDateFormat("HH:mm",Locale.getDefault())//24-hour time format

        for(models in fetchingScheduleData){

            //convert start date and end date to date format
            val startDate: Date? =dateFormat.parse(models.sch_sDate)
            val endDate:Date?=dateFormat.parse(models.sch_eDate)

            //convert start time end time in 24 hour format
            val startTime:Date?=timeFormat.parse(models.sch_sTime)
            val endTime:Date?=timeFormat.parse(models.sch_eTime)

            println("start date  $startDate")
            println("end date $endDate")
            println("start time $startTime")
            println("end time $endTime")


            //extract the specific part
            //start date
            val calendar=Calendar.getInstance()


            calendar.time=startDate?:Date()
            val startYear=calendar.get(Calendar.YEAR)
            val startMonth=calendar.get(Calendar.MONTH)+1
            val startDay=calendar.get(Calendar.DAY_OF_MONTH)




            //end date
            calendar.time=endDate?:Date()
            val endYear=calendar.get(Calendar.YEAR)
            val endMonth=calendar.get(Calendar.MONTH)+1
            val endDay=calendar.get(Calendar.DAY_OF_MONTH)


            //start time
            calendar.time=startTime?:Date()
            val startHour=calendar.get(Calendar.HOUR)
            val startMinute=calendar.get(Calendar.MINUTE)


            //end time
            calendar.time=endTime?:Date()
            val endHour=calendar.get(Calendar.HOUR)
            val endMinute=calendar.get(Calendar.MINUTE)

            calendar.set(startYear,startMonth, startDay,startHour,startMinute)


            println("$startYear  $startMonth $startDay $endYear $endMonth $endDay $startHour $startMinute $endHour $endMinute")






        }


    }
}