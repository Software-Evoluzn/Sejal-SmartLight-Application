package com.example.etcdynamiclight.activityFolder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.etcdynamiclight.R
import com.example.etcdynamiclight.adapterClass.ToggleButtonAdapter
import com.example.etcdynamiclight.databaseClass.DBHelpher
import com.example.etcdynamiclight.deviceConnectionclass.USBHandler

class MainActivity : AppCompatActivity() {
    lateinit var mDbHelpher: DBHelpher
    lateinit var mUsbHandler: USBHandler
   private lateinit var toggleButtonAdapter: ToggleButtonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        mDbHelpher= DBHelpher(this,null)
        mUsbHandler= USBHandler(this)

        insertData()

        val deviceList=mDbHelpher.fetchData()
        val recyclerView: RecyclerView =findViewById(R.id.ButtonRecyclerView)
        toggleButtonAdapter= ToggleButtonAdapter(deviceList,mUsbHandler)
        recyclerView.layoutManager=GridLayoutManager(this,3)
        val adapter=toggleButtonAdapter
        recyclerView.adapter=adapter
    }

    private fun insertData() {
        //device1
        mDbHelpher.RegisterUserHelpher(1, "T:5:1:1:")
        mDbHelpher.RegisterUserHelpher(2, "T:5:1:2:")
        mDbHelpher.RegisterUserHelpher(3, "T:5:1:3:")
        mDbHelpher.RegisterUserHelpher(4, "T:5:1:4:")

        //device2
        mDbHelpher.RegisterUserHelpher(5, "T:5:2:1:")
        mDbHelpher.RegisterUserHelpher(6, "T:5:2:2:")
        mDbHelpher.RegisterUserHelpher(7, "T:5:2:3:")
        mDbHelpher.RegisterUserHelpher(8, "T:5:2:4:")

        //device3
        mDbHelpher.RegisterUserHelpher(9, "T:5:3:1:")
        mDbHelpher.RegisterUserHelpher(10, "T:5:3:2:")
        mDbHelpher.RegisterUserHelpher(11, "T:5:3:3:")
        mDbHelpher.RegisterUserHelpher(12, "T:5:3:4:")
    }
}