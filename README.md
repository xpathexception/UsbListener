![Release](https://jitpack.io/v/xpathexception/UsbListener.svg?style=flat-square)

![MinSDK](https://img.shields.io/badge/minSdk-5.0%20(21)-brightgreen.svg?style=flat-square&logo=android)
![TargetSDK](https://img.shields.io/badge/targetSdk-9.0%20(28)-brightgreen.svg?style=flat-square&logo=android)

![Kotlin](https://img.shields.io/badge/Kotlin-1.3.21-brightgreen.svg?style=flat-square&logo=kotlin)

# UsbListener
Android library for listening USB device attached and detached event when in host mode.

## Setup
1.  In root build.gradle:
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
````

2.  In target module build.gradle
```
dependencies {
    compile 'com.github.xpathexception:UsbListener:1.0.0'
}
```

## Usage
Following sample code shows how to initialize listener with permission request:
``` kotlin
val usbListener = UsbListener(this, true, object : UsbListener.UsbDeviceListener {
    override fun onAttached(device: UsbDevice) {
        //to do
    }

    override fun onDetached(device: UsbDevice) {
        //to do
    }
})
```

Don't forget to dispose it when no longer need:
``` kotlin
usbListener.dispose()
```

Reworked from https://github.com/potterhsu/UsbListener
