package com.example.etcdynamiclight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class AlarmBootReceiver:BroadcastReceiver() {

    lateinit var setAlarmFromDatabase:SetAlarmFromDatabase
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if(Intent.ACTION_BOOT_COMPLETED==intent?.action) {
            val mDbHelpher= context?.let { DBHelpher(it,null) }
            val fetchDataList=mDbHelpher?.fetchSchedulingData()
            val setAlarmFromDatabase=SetAlarmFromDatabase()
            if (fetchDataList != null) {
                setAlarmFromDatabase.fetchDataFromDataBase(fetchDataList,context)
            }

            val serviceIntent=Intent(context,UsbConnectionService::class.java)
            context?.startForegroundService(serviceIntent)



        }

    }


}