package com.test.edu.kotlin_fb_demo

import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import android.app.Application


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RxPaparazzo.register(this)
    }
}