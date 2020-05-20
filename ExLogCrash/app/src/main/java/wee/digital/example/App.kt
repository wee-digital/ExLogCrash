package wee.digital.example

import android.app.Application
import wee.digital.log.LogBook


class App : Application() {

    companion object {
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        LogBook.app = this
        LogBook.crashLog = true
    }

}