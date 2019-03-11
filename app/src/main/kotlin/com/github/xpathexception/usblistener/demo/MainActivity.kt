package com.github.xpathexception.usblistener.demo

import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.xpathexception.usblistener.UsbListener

class MainActivity : AppCompatActivity() {
	private lateinit var tvMessage: TextView
	private lateinit var usbListener: UsbListener

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		tvMessage = findViewById(R.id.tvMessage)

		usbListener = UsbListener(this, true, object : UsbListener.UsbDeviceListener {
			override fun onAttached(device: UsbDevice) {
				Log.d(TAG, "onAttached: $device")
				tvMessage.text = "$device attached"
			}

			override fun onDetached(device: UsbDevice) {
				Log.d(TAG, "onDetached: $device")
				tvMessage.text = "$device detached"
			}
		})
	}

	override fun onDestroy() {
		usbListener.dispose()
		super.onDestroy()
	}

	companion object {
		val TAG: String = MainActivity::class.java.simpleName
	}
}
