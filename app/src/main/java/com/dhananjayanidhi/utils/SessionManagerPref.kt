package com.dhananjayanidhi.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.dhananjayanidhi.activities.HomeActivity
import com.dhananjayanidhi.activities.LoginActivity
import com.dhananjayanidhi.models.usermodel.DataUserLoginModel
import com.google.gson.Gson


class SessionManagerPref(
    // Context
    private val _context: Context?,
) {
    // Editor reference for Shared preferences
    private val editor: Editor

    // Check for login
    private fun isLogin(): Int {
        return preferences.getInt(IS_LOGIN, 0)
    }

    fun setLoginSession(userStatus: Int) {
        editor.putInt(IS_LOGIN, userStatus)
        editor.apply()
    }

    var getLoginModel: DataUserLoginModel?
        get() {
            val json = preferences.getString("LoginModel", "")
            return Gson().fromJson(json, DataUserLoginModel::class.java)
        }
        set(authModel) {
            val json = Gson().toJson(authModel)
            editor.putString("LoginModel", json)
            editor.apply()
        }

    fun checkLogin() {
        if (isLogin() == 1) {
            val intent = Intent(_context, HomeActivity::class.java)
            intent.flags =
                (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            _context!!.startActivity(intent)
        } else {
            val intent = Intent(_context, LoginActivity::class.java)
            intent.flags =
                (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            _context!!.startActivity(intent)
        }
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        val dontShow = AppController.instance?.sessionManager?.getBooleanValue(
            DON_T_SHOW_KEY, false
        )
        editor.clear()
        editor.apply()
        AppController.instance?.sessionManager?.addBooleanValue(
            DON_T_SHOW_KEY, dontShow ?: false
        )
        val intent = if (getBooleanValue(DON_T_SHOW_KEY, false)) {
            Intent(_context, LoginActivity::class.java)
        } else {
            Intent(_context, LoginActivity::class.java)
        }
        intent.flags =
            (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        _context!!.startActivity(intent)
    }

    companion object {
        // Shared pref  file name
        private const val PREF_NAME = "moonDigitalPayPref"

        // All Shared Preferences Keys
        private const val IS_LOGIN = "IsLoggedIn"
        const val LEARN_KEY = "Learn"
        const val DON_T_SHOW_KEY = "DontShow"
        const val TestAnswers = "TestAnswers"

        // Shared Preferences reference
        private lateinit var preferences: SharedPreferences
    }

    // Constructor
    init {
        val PRIVATE_MODE = 0
        preferences = _context!!.applicationContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences.edit()
    }


    fun addStringValue(keyName: String, value: String) {
        editor.putString(keyName, value)
        editor.commit()
    }

    fun getStringValue(keyName: String, defaultValue: String): String {
        return preferences.getString(keyName, defaultValue).toString()
    }

    fun addBooleanValue(keyName: String, value: Boolean) {
        editor.putBoolean(keyName, value)
        editor.commit()
    }

    fun getBooleanValue(keyName: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(keyName, defaultValue)
    }

    fun set(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }
}