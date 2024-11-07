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
            private val DATABASE_VERSION=1
            val tableName="devices_query"
            val srNo_col="Sr_No"
            val device_id_col="Device_id"

        }


    override fun onCreate(db: SQLiteDatabase) {
        val devices_query="CREATE TABLE $tableName($srNo_col Integer PRIMARY KEY,$device_id_col TEXT)"
        db.execSQL(devices_query)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $tableName")
        onCreate(db)

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