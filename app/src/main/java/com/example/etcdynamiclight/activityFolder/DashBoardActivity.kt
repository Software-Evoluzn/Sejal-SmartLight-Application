package com.example.etcdynamiclight.activityFolder


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.example.etcdynamiclight.R
import com.example.etcdynamiclight.databaseClass.DBHelpher
import com.example.etcdynamiclight.deviceConnectionclass.USBHandler
import com.example.etcdynamiclight.serviceClassFolder.UsbService
import com.example.etcdynamiclight.setAlarmClass.SetAlarmFromDatabase
import com.example.etcdynamiclight.setAlarmClass.ShowingDataAndTimePicker
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class DashBoardActivity : AppCompatActivity() {


    lateinit var masterSWITCH: SwitchCompat
    lateinit var masterSwitchCardView: CardView
    lateinit var  mUsbHandler: USBHandler
    lateinit var mUsbService: UsbService
    lateinit var spinnerDay:Spinner
    lateinit var timeAndDatePicker: ShowingDataAndTimePicker
    lateinit var OnTime:String
    lateinit var OffTime:String
    lateinit var startDate:String
    lateinit var endDate:String
    lateinit var mDbHelpher: DBHelpher
    lateinit var setAlarmFromDatabase: SetAlarmFromDatabase
    lateinit var setDateRange:TextView
    lateinit var OnTimeButton:AppCompatButton
    lateinit var OffTimeButton:AppCompatButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)


        val sharePreference=getSharedPreferences("switchSharePreference", MODE_PRIVATE)


         mUsbHandler = USBHandler(this)
         mDbHelpher= DBHelpher(this,null)
         setAlarmFromDatabase= SetAlarmFromDatabase()
         masterSWITCH = findViewById(R.id.MasterSwitch)
         masterSwitchCardView = findViewById(R.id.cardMasterSwitch)
         spinnerDay=findViewById(R.id.spinnerDay)
         timeAndDatePicker= ShowingDataAndTimePicker(this,supportFragmentManager)
         mUsbService= UsbService()
        setDateRange=findViewById(R.id.setDateRange)
         OnTimeButton=findViewById(R.id.selectOnTime)
         OffTimeButton=findViewById(R.id.SelectOfTime)

        //set the dange range
        val saveStartDate=sharePreference.getString("startDate","")
        val saveEndDate=sharePreference.getString("endDate","")

        if(!saveStartDate.isNullOrEmpty() && !saveEndDate.isNullOrEmpty()){
            setDateRange.setText(saveStartDate+" to "+saveEndDate)
        }

        val saveOnTime=sharePreference.getString("OnTime","")
        val saveOffTime=sharePreference.getString("OffTime","")
        if(!saveOnTime.isNullOrEmpty()){
            OnTimeButton.setText(saveOnTime)

        }

        if(!saveOffTime.isNullOrEmpty()){
            OffTimeButton.setText(saveOffTime)
        }

         masterSwitchCardView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val editor=sharePreference.edit()


        //set the date range
        masterSWITCH.isChecked=sharePreference.getBoolean("status",false)

        val filter=IntentFilter("com.example.update_master_switch")

        val masterSwitchReceiver=object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val status = intent?.getBooleanExtra("status", false) ?: false
                masterSWITCH.isChecked = status
            }

        }
        registerReceiver(masterSwitchReceiver,filter)


        masterSWITCH.setOnCheckedChangeListener { buttonView, isChecked ->
            sharePreference.edit().putBoolean("status",isChecked).apply()

            val serviceIntent=Intent(this, UsbService::class.java)
            serviceIntent.action="SEND_DATA"
            if (isChecked) {
                serviceIntent.putExtra("message","T:5:G:G:1")
            } else {
                serviceIntent.putExtra("message","T:5:G:G:0")
            }
             startService(serviceIntent)

        }

        val arrSpinner= arrayOf("Select item","Today","Set Date")
        val arrayAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,arrSpinner)
        spinnerDay.adapter=arrayAdapter
        val savedSpinnerItem=sharePreference.getString("selectedSpinnerItem","")
        val savedPosition=arrSpinner.indexOf(savedSpinnerItem)
        if(savedPosition>=0){
            spinnerDay.setSelection(savedPosition)
        }
        spinnerDay.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selectedItem = spinnerDay.getItemAtPosition(position) as String
                editor.putString("selectedSpinnerItem",selectedItem).apply()
                when (selectedItem) {

                    "Today" -> {
                        val current = LocalDate.now()
                        startDate = current.toString()
                        endDate = current.toString()
                        setDateRange.text = startDate + " to " + endDate
                        editor.putString("startDate",startDate)
                        editor.putString("endDate",endDate)
                    }
                    "Set Date" -> {
                       val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select Start Date and End Date")
                            .build()
                        dateRangePicker.show(supportFragmentManager, "date_range_picker")
                        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
                            val startDateMillis = datePicked.first
                            val endDateMillis = datePicked.second
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            startDate = dateFormat.format(Date(startDateMillis))
                            endDate = dateFormat.format(Date(endDateMillis))
                            setDateRange.text = "$startDate to $endDate"
                            editor.putString("startDate",startDate)
                            editor.putString("endDate",endDate)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        OnTimeButton.setOnClickListener{
            timeAndDatePicker.OpenTimePickerDialog { selectTime->
                OnTime=selectTime
                OnTimeButton.setText(OnTime)
                editor.putString("OnTime",OnTime)
                editor.apply()
            }
        }

        val OffTimeButton:AppCompatButton=findViewById(R.id.SelectOfTime)
        OffTimeButton.setOnClickListener{
            timeAndDatePicker.OpenTimePickerDialog {selectTime->
                OffTime=selectTime
                OffTimeButton.setText(OffTime)
                editor.putString("OffTime",OffTime)
                editor.apply()
            }
        }

        val scheduleButton:AppCompatButton=findViewById(R.id.ScheduleButton)
        scheduleButton.setOnClickListener{
            if(startDate.isNotEmpty() && endDate.isNotEmpty()) {
                if(OnTime.isNotEmpty() && OffTime.isNotEmpty()) {
                    mDbHelpher.ScheduleRegistration("123", startDate, endDate, OnTime, OffTime, "1")
                }else{
                    val alertDialog=AlertDialog.Builder(this)
                    alertDialog.setIcon(R.drawable.logos1)
                    alertDialog.setTitle("Evoluzn")
                    alertDialog.setMessage("Select On Time and Off Time !")

                    alertDialog.show()
                }
            }else{
                val alertDialog=AlertDialog.Builder(this)
                alertDialog.setIcon(R.drawable.logos1)
                alertDialog.setTitle("Evoluzn")
                alertDialog.setMessage("Select Start Date  and End Date !")

                alertDialog.show()
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
            val alertDialog=AlertDialog.Builder(this)
            alertDialog.setIcon(R.drawable.logos1)
            alertDialog.setTitle("Evoluzn")
            alertDialog.setMessage("Alarm Schedule!")

            alertDialog.show()








        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mUsbHandler.unRegisterReceiver()
    }

}