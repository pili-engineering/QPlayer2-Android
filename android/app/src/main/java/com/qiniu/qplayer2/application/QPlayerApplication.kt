package com.qiniu.qplayer2.application

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.tencent.bugly.crashreport.CrashReport

class QPlayerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        QPlayerApplicationContext.applicationContext = applicationContext
        CrashReport.initCrashReport(applicationContext, "adadee00c2", false);
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

    }
}