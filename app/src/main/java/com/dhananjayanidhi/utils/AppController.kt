package com.dhananjayanidhi.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate

class AppController : Application() , Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        
        // Force light mode - disable dark mode for entire app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        instance = this
        context = this
    }

    private var pref: SessionManagerPref? = null


    companion object {
        val TAG = AppController::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        var instance: AppController? = null
            private set

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }


    val sessionManager: SessionManagerPref
        get() {
            if (pref == null) {
                pref = SessionManagerPref(this)
            }
            return pref!!
        }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}