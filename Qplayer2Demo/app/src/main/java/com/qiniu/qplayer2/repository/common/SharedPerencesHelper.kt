package com.qiniu.qplayer2.repository.common

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager


class SharedPreferencesHelper {
    var applicationContext: Context
        private set
    var sharedPreferences: SharedPreferences
        private set

    constructor(
        context: Context,
        sharedPreferences: SharedPreferences
    ) {
        this.sharedPreferences = sharedPreferences
        applicationContext = context.applicationContext
    }

    constructor(context: Context, prefName: String) : this(
        context,
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    ) {

    }

    constructor(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        applicationContext = context.applicationContext
    }

    // ----- String -----
    fun optString(key: String?, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun optString(keyResId: Int, defaultValue: String?): String? {
        val key = applicationContext.getString(keyResId)
        return optString(key, defaultValue)
    }

    fun setString(key: String?, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    // ----- integer -----
    fun optInteger(key: String?, defaultValue: Int): Int {
        try {
            return sharedPreferences.getInt(key, defaultValue)
        } catch (e: ClassCastException) {
            try {
                val strValue = sharedPreferences.getString(key, null)
                return if (TextUtils.isEmpty(strValue)) defaultValue else try {
                    Integer.valueOf(strValue)
                } catch (e2: NumberFormatException) {
                    defaultValue
                }
            } catch (e1: ClassCastException) {
//                DebugLog.printStackTrace(e1)
            }
        }
        return defaultValue
    }

    // ----- float -----
    fun optFloat(key: String?, defaultValue: Float): Float {
        try {
            return sharedPreferences.getFloat(key, defaultValue)
        } catch (e: ClassCastException) {

        }
        return defaultValue
    }

    fun optInteger(keyResId: Int, defaultValue: Int): Int {
        val key = applicationContext.getString(keyResId)
        return optInteger(key, defaultValue)
    }

    fun setInteger(key: String?, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun setFloat(key: String?, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    // ----- boolean -----
    fun optBoolean(key: String?, defaultValue: Boolean): Boolean {
        try {
            return sharedPreferences.getBoolean(key, defaultValue)
        } catch (e: ClassCastException) {
            try {
                val strValue = sharedPreferences.getString(key, null)
                return if (TextUtils.isEmpty(strValue)) defaultValue else try {
                    java.lang.Boolean.valueOf(strValue)
                } catch (e2: NumberFormatException) {
                    defaultValue
                }
            } catch (e1: ClassCastException) {
//                DebugLog.printStackTrace(e1)
            }
        }
        return defaultValue
    }

    fun optBoolean(keyResId: Int, defaultValue: Boolean): Boolean {
        val key = applicationContext.getString(keyResId)
        return optBoolean(key, defaultValue)
    }

    fun setBoolean(key: String?, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun setBoolean(keyResId: Int, value: Boolean) {
        sharedPreferences.edit().putBoolean(applicationContext.getString(keyResId), value)
            .apply()
    }

    fun optLong(key: String?, value: Long): Long {
        return try {
            sharedPreferences.getLong(key, value)
        } catch (e: ClassCastException) {
            try {
                val strValue = sharedPreferences.getString(key, null)
                if (TextUtils.isEmpty(strValue)) return value
                try {
                    strValue!!.toLong()
                } catch (e2: NumberFormatException) {
                    value
                }
            } catch (e1: ClassCastException) {
                value
            }
        }
    }

    fun setLong(key: String?, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun remove(key: String?) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun remove(@StringRes keyResId: Int) {
        remove(applicationContext.getString(keyResId))
    }

    fun edit(): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
