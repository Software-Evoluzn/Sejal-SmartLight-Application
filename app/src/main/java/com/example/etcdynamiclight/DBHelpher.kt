package com.example.etcdynamiclight

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelpher(context: Context,factory:SQLiteDatabase.CursorFactory?):
    SQLiteOpenHelper(context,DATABASE_NAME,factory,DATABASE_VERSION) {

        companion object{
            private val DATABASE_NAME="Lights"
            private val DATABASE_VERSION=2


            val tableName="devices_query"
            val srNo_col="Sr_No"
            val device_id_col="Device_id"

            val tableSchedule="scheduling"
            val scheduling_Sr_No="Sch_Id"
            val scheduling_everyDay="Sch_everyDay"
            val scheduling_startDate="startDate"
            val scheduling_endDate="endDate"
            val scheduling_startTime="startTime"
            val scheduling_endTime="endTime"
            val scheduling_value="Scheduling"


        }


    override fun onCreate(db: SQLiteDatabase) {
        val devices_query="CREATE TABLE $tableName($srNo_col Integer PRIMARY KEY,$device_id_col TEXT)"
        val schedule_query="CREATE TABLE $tableSchedule($scheduling_Sr_No INTEGER PRIMARY KEY AUTOINCREMENT,$scheduling_everyDay TEXT,$scheduling_startDate TEXT,$scheduling_endDate TEXT,$scheduling_startTime TEXT,$scheduling_endTime TEXT,$scheduling_value TEXT)"
        db.execSQL(devices_query)
        db.execSQL(schedule_query)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $tableName")
        db.execSQL("DROP TABLE IF EXISTS $tableSchedule")
        onCreate(db)

    }

    fun ScheduleRegistration(sch_everyday:String,sDate:String,eDate:String,sTime:String,eTime:String,sch_value:String){
        val db=this.writableDatabase
        val contentValues=ContentValues().apply {
            put(scheduling_everyDay,sch_everyday)
            put(scheduling_startDate, sDate)
            put(scheduling_endDate,eDate)
            put(scheduling_startTime,sTime)
            put(scheduling_endTime,eTime)
            put(scheduling_value,sch_value)


        }

        db.insert(tableSchedule,null,contentValues)
        db.close()

    }



    fun RegisterUserHelpher(srNo:Int,device_id:String){
        val db=this.writableDatabase
        val cursor=db.rawQuery("SELECT * FROM $tableName WHERE $srNo_col=?" , arrayOf(srNo.toString()))
        val contentValues=ContentValues().apply {
            put(srNo_col,srNo)
            put(device_id_col,device_id,)
        }
        if(cursor.count==0){
             db.insert(tableName,null,contentValues)
        }else{
             db.update(tableName,contentValues, "Sr_No=?", arrayOf(srNo.toString()))
         }
        db.close()

    }


    fun fetchSchedulingData():ArrayList<ScheduleModel>{
        val db=this.readableDatabase
        val fetchQuery="SELECT * FROM $tableSchedule"
        val cursor=db.rawQuery(fetchQuery,null)
        val arrContacts=ArrayList<ScheduleModel>()
        while(cursor.moveToNext()){
            val scheduleModel=ScheduleModel().apply {
                sch_everyDay=cursor.getString(1)
                sch_sDate=cursor.getString(2)
                sch_eDate=cursor.getString(3)
                sch_sTime=cursor.getString(4)
                sch_eTime=cursor.getString(5)
                sch_value=cursor.getString(6)


            }
            arrContacts.add(scheduleModel)

        }
        cursor.close()
        return arrContacts
    }

    fun fetchData():ArrayList<ContactModel>{
        val db=this.readableDatabase
        val fetchQuery="SELECT * FROM $tableName"
        val cursor=db.rawQuery(fetchQuery,null)
        val arrContacts= ArrayList<ContactModel>()
        while(cursor.moveToNext()){
            val contact=ContactModel().apply{
                Sr_No=cursor.getInt(0)
                device_id=cursor.getString(1)
            }
            arrContacts.add(contact)
        }
        cursor.close()
        return arrContacts
    }

    }