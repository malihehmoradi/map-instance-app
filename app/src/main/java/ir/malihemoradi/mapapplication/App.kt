package ir.malihemoradi.mapapplication

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import ir.malihemoradi.mapapplication.data.AppRoomDatabase

class App :MultiDexApplication() {

    val database:AppRoomDatabase by lazy { AppRoomDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}