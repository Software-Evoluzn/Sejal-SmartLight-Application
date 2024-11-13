package com.example.etcdynamiclight.ReceiverClass

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.etcdynamiclight.R
import com.example.etcdynamiclight.databaseClass.DBHelpher
import com.example.etcdynamiclight.setAlarmClass.SetAlarmFromDatabase

class AlarmBootReceiver:BroadcastReceiver() {
    val CHANNEL_ID="alarm_channel"
    val NOTIFICATION_ID=1


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if(Intent.ACTION_BOOT_COMPLETED==intent?.action) {

            playSound(context)
            if (context != null) {
                createNotificationChannel(context)
            }
            if (context != null) {
                createNotification(context)
            }


            val mDbHelpher= context?.let { DBHelpher(it,null) }
            val fetchDataList=mDbHelpher?.fetchSchedulingData()
            val setAlarmFromDatabase= SetAlarmFromDatabase()
            if (fetchDataList != null) {
                setAlarmFromDatabase.fetchDataFromDataBase(fetchDataList,context)
            }



//            val serviceIntent=Intent(context, UsbConnectionService::class.java)
//            context?.startForegroundService(serviceIntent)
        }
    }

    fun playSound(context: Context?) {
        var alarmSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if(alarmSound==null){
            alarmSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        val ringtone=RingtoneManager.getRingtone(context,alarmSound)
        ringtone?.play()

    }
    private fun createNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.evoluznlogo)
            .setContentTitle("Evoluzn")
            .setContentText("Alarm Schedule!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManagerCompat = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if needed
            return
        }
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Notification"
            val descriptionText = "Notification for Alarm"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

}