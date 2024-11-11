package com.example.etcdynamiclight


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.google.android.material.datepicker.MaterialDatePicker
import androidx.core.util.Pair
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId


class DashBoardActivity : AppCompatActivity() {


    lateinit var masterSWITCH: SwitchCompat
    lateinit var masterSwitchCardView: CardView
    lateinit var  mUsbHandler:USBHandler
    lateinit var mUsbService:UsbService
    lateinit var spinnerDay:Spinner
    lateinit var timeAndDatePicker:ShowingDataAndTimePicker
    lateinit var OnTime:String
    lateinit var OffTime:String
    lateinit var startDate:String
    lateinit var endDate:String
    lateinit var mDbHelpher:DBHelpher
    lateinit var setAlarmFromDatabase:SetAlarmFromDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)
         mUsbHandler = USBHandler(this)
         mDbHelpher=DBHelpher(this,null)
         setAlarmFromDatabase= SetAlarmFromDatabase()
         masterSWITCH = findViewById(R.id.MasterSwitch)
         masterSwitchCardView = findViewById(R.id.cardMasterSwitch)
         spinnerDay=findViewById(R.id.spinnerDay)
         timeAndDatePicker=ShowingDataAndTimePicker(this,supportFragmentManager)
         mUsbService=UsbService()

         masterSwitchCardView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val sharePreference=getSharedPreferences("switchSharePreference", MODE_PRIVATE)
        masterSWITCH.isChecked=sharePreference.getBoolean("status",false)


        masterSWITCH.setOnCheckedChangeListener { buttonView, isChecked ->
            sharePreference.edit().putBoolean("status",isChecked).apply()

            val serviceIntent=Intent(this,UsbService::class.java)
            serviceIntent.action="SEND_DATA"
            if (isChecked) {
                serviceIntent.putExtra("message","T:5:G:G:1")
            } else {
                serviceIntent.putExtra("message","T:5:G:G:0")
            }
             startService(serviceIntent)
        }

        val arrSpinner= arrayOf("Select item","Today","Set Date","Weekly")
        val arrayAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,arrSpinner)
        spinnerDay.adapter=arrayAdapter



        spinnerDay.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                  var selectedItem=spinnerDay.getItemAtPosition(position)
                when(selectedItem){
                    "Today"->{

                        val current =LocalDate.now()
                        startDate= current.toString()
                        endDate=current.toString()
                        println("start data : $startDate and end data : $endDate")
                        Log.i("today date","date $startDate and $endDate")


                    }

                    "Set Date" -> {
////                        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
////                            .setSelection(Pair(System.currentTimeMillis(), System.currentTimeMillis()))
////                            .build()
////
////                        datePicker.addOnPositiveButtonClickListener { selection ->
////                            val startDateInMillis = selection.first
////                            val endDateInMillis = selection.second
////
////                            // Convert milliseconds to LocalDate properly
////                            val startDate1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDateInMillis), ZoneId.systemDefault()).toLocalDate().toString()
////                            val endDate1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDateInMillis), ZoneId.systemDefault()).toLocalDate().toString()
////
////                            Log.i("Set Date", "Start Date: $startDate1, End Date: $endDate1")
//
//                            startDate = startDate1
//                            endDate = endDate1
//                        }
//
//                        datePicker.show(supportFragmentManager, "date_range_picker")

                        startDate="2024-11-11"
                        endDate="2024-11-12"

                    }


                    "Weekly"->{
                        Toast.makeText(this@DashBoardActivity,"Weekly",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        val OnTimeButton:AppCompatButton=findViewById(R.id.selectOnTime)
        OnTimeButton.setOnClickListener{
            timeAndDatePicker.OpenTimePickerDialog { selectTime->
                OnTime=selectTime
                Log.i("onTime","on time : $OnTime")
            }

        }

        val OffTimeButton:AppCompatButton=findViewById(R.id.SelectOfTime)
        OffTimeButton.setOnClickListener{
            timeAndDatePicker.OpenTimePickerDialog {selectTime->
                OffTime=selectTime
                Log.i("Off time","OffTime : $OffTime")

            }

        }

        val scheduleButton:AppCompatButton=findViewById(R.id.ScheduleButton)

        scheduleButton.setOnClickListener{
            if(startDate.isNotEmpty() && endDate.isNotEmpty()) {
                if(OnTime.isNotEmpty() && OffTime.isNotEmpty()) {
                    mDbHelpher.ScheduleRegistration("123", startDate, endDate, OnTime, OffTime, "1")
                }else{
                    Toast.makeText(this@DashBoardActivity,"please select On time and Off time",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@DashBoardActivity,"select today",Toast.LENGTH_SHORT).show()
            }
            val fetchingScheduleData=mDbHelpher.fetchSchedulingData()
            for( i in fetchingScheduleData) {
                println("sch_everyday: ${i.sch_everyDay}")
                println("startdate: ${i.sch_sDate}")
                println("enddate: ${i.sch_eDate}")
                println("startTime: ${i.sch_sTime}")
                println("endTime: ${i.sch_eTime}")
                println("value: ${i.sch_value}")
                println("---------------------------------")

            }

            setAlarmFromDatabase.fetchDataFromDataBase(fetchingScheduleData,this)


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mUsbHandler.unRegisterReceiver()
    }
}