package com.example.etcdynamiclight

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface

class USBHandler(private  val context:Context){

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var device: UsbDevice? = null
    private var connection: UsbDeviceConnection? = null
    var serial: UsbSerialDevice? = null
    private val ACTION_USB_PERMISSION = "permission"

    val filter = IntentFilter().apply {
        addAction(ACTION_USB_PERMISSION)
        addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_USB_PERMISSION -> {
                    val granted = intent.extras?.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED) ?: false
                    if (granted) {
                        connection = usbManager.openDevice(device)
                        serial = UsbSerialDevice.createUsbSerialDevice(device, connection)
                        serial?.apply {
                            if (open()) {
                                setBaudRate(9600)
                                setDataBits(UsbSerialInterface.DATA_BITS_8)
                                setStopBits(UsbSerialInterface.STOP_BITS_1)
                                setParity(UsbSerialInterface.PARITY_NONE)
                                read(readCallback)
                                setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
                            } else {
                                Log.i("UsbHelper", "Port not open")
                            }
                        }
                    } else {
                        Log.i("UsbHelper", "Permission not granted")
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> startUsbConnection()
                UsbManager.ACTION_USB_DEVICE_DETACHED -> disconnect()
            }
        }
    }


    init {
        context.registerReceiver(broadcastReceiver, filter)
        //automatically start UsbConnection
        startUsbConnection()
    }

    fun startUsbConnection() {
        val usbDevices = usbManager.deviceList
        if (usbDevices.isNotEmpty()) {
            usbDevices.forEach { entry ->
                device = entry.value
                val deviceVendorId = device?.vendorId
                if (deviceVendorId == 1155) {
                    val intent = PendingIntent.getBroadcast(
                        context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE)
                    usbManager.requestPermission(device, intent)
                    Toast.makeText(context, "Connection Successful", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        } else {
            Log.i("UsbHelper", "No USB device connected")
        }
    }

    fun sendData(input: String) {
        if (serial == null) {
            Log.i("UsbHelper", "Serial is not initialized; attempting to reconnect.")
            startUsbConnection()
            Handler(Looper.getMainLooper()).postDelayed({
                serial?.write(input.toByteArray()) ?: Log.i("UsbHelper", "Failed to send; Serial still null.")
            }, 500) // delay to allow USB initialization
        } else {
            serial?.write(input.toByteArray())
            Log.i("UsbHelper", "Sending data: $input")
        }
    }


    fun disconnect() {
        serial?.close()
    }

    private val readCallback = UsbSerialInterface.UsbReadCallback { data ->
        val dataStr = data?.let { String(it, Charsets.UTF_8) }
        Log.i("UsbHelper", "Data received: $dataStr")
        // Add code here to handle data if needed, like updating a UI component
    }
    fun unRegisterReceiver(){
        context.unregisterReceiver(broadcastReceiver)
    }
}
