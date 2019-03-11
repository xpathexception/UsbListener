package com.github.xpathexception.usblistener

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED
import android.util.Log
import java.lang.ref.WeakReference

class UsbListener(context: Context,
				  private val needPermission: Boolean,
				  private val usbDeviceListener: UsbDeviceListener) {
	private val weakContext = WeakReference<Context>(context)
	private val usbReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			when (intent.action) {
				ACTION_USB_DEVICE_ATTACHED -> synchronized(this) {
					val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE) ?: return
					val hasPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
					Log.v(TAG, String.format("%s with vendor ID %d attached, has permission? %s",
											 device.deviceName,
											 device.vendorId,
											 if (hasPermission) "Yes" else "No"))
					if (needPermission && !hasPermission) {
						Log.d(TAG, "Request permission for " + device.deviceName)
						val pendingIntent = PendingIntent.getBroadcast(context,
																	   0,
																	   Intent(ACTION_USB_PERMISSION),
																	   PendingIntent.FLAG_ONE_SHOT)
						context.usbManager.requestPermission(device, pendingIntent)
					} else {
						usbDeviceListener.onAttached(device)
					}
				}
				ACTION_USB_DEVICE_DETACHED -> synchronized(this) {
					val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
					usbDeviceListener.onDetached(device)
				}
				ACTION_USB_PERMISSION -> synchronized(this) {
					val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
					val hasPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
					Log.v(TAG, String.format("Received permission for %s, has permission? %s",
											 device.deviceName,
											 if (hasPermission) "Yes" else "No"))
					if (hasPermission) {
						usbDeviceListener.onAttached(device)
					}
				}
			}
		}
	}

	init {
		val intentFilter = IntentFilter().apply {
			addAction(ACTION_USB_DEVICE_ATTACHED)
			addAction(ACTION_USB_DEVICE_DETACHED)
			if (needPermission) addAction(ACTION_USB_PERMISSION)
		}
		context.registerReceiver(usbReceiver, intentFilter)
	}

	fun dispose() {
		weakContext.get()?.unregisterReceiver(usbReceiver)
	}

	interface UsbDeviceListener {
		fun onAttached(device: UsbDevice)
		fun onDetached(device: UsbDevice)
	}

	companion object {
		val TAG: String = UsbListener::class.java.simpleName
		val ACTION_USB_PERMISSION = "$TAG.action.USB_PERMISSION"
	}
}

val Context.usbManager: UsbManager
	get() = getSystemService(Context.USB_SERVICE) as UsbManager