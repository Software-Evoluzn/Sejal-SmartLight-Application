package com.example.etcdynamiclight

class SetAlarmFromDatabase {

    fun fetchDataFromDataBase(fetchingScheduleData: ArrayList<ScheduleModel>) {
        var startDateArr:Array<String>?=null

        for(models in fetchingScheduleData){
            startDateArr= models.sch_eDate.split("-").toTypedArray()


        }

        val startYear= startDateArr?.get(0)?.toInt()



    }
}