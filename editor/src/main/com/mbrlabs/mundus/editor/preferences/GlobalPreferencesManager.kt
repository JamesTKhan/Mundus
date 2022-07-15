package com.mbrlabs.mundus.editor.preferences

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

/**
 * Manages global preferences for Mundus regardless of current project
 *
 * @author James Pooley
 * @version July 14, 2022
 */
class GlobalPreferencesManager : PreferencesManager {
    companion object {
        private val TAG = GlobalPreferencesManager::class.java.simpleName

        var MUNDUS_VERSION = "version"
    }

    private var globalPrefs: Preferences = Gdx.app.getPreferences("mundus.global")

    /**
     * Set a value in preferences
     * Supported types: Boolean, String, Int, Long, Float
     */
    override fun set(key: String, value: Any) {
        when(value) {
            is Boolean -> globalPrefs.putBoolean(key, value)
            is String -> globalPrefs.putString(key, value)
            is Int -> globalPrefs.putInteger(key, value)
            is Long -> globalPrefs.putLong(key, value)
            is Float -> globalPrefs.putFloat(key, value)
            else -> {
                Gdx.app.error(TAG, "Invalid object type given for preferences.")
                return
            }
        }

        globalPrefs.flush()
    }

    fun set(value: MutableMap<String, *>) {
        globalPrefs.put(value)
    }

    override fun get(key: String, type: Class<*>): Any? {
        if (type == Boolean::class.java) {
            return globalPrefs.getBoolean(key, false)
        }
        return null
    }

    override fun getBoolean(key: String): Boolean {
        return globalPrefs.getBoolean(key)
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return globalPrefs.getBoolean(key, defValue)
    }

    override fun getInteger(key: String): Int {
        return globalPrefs.getInteger(key)
    }

    override fun getInteger(key: String, defValue: Int): Int {
        return globalPrefs.getInteger(key, defValue)
    }

    override fun getLong(key: String): Long {
        return globalPrefs.getLong(key)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return globalPrefs.getLong(key, defValue)
    }

    override fun getFloat(key: String): Float {
        return globalPrefs.getFloat(key)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return globalPrefs.getFloat(key, defValue)
    }

    override fun getString(key: String): String {
        return globalPrefs.getString(key)
    }

    override fun getString(key: String?, defValue: String?): String {
        globalPrefs.get()
        return globalPrefs.getString(key, defValue)
    }

    override fun contains(key: String): Boolean {
        return globalPrefs.contains(key)
    }

    override fun remove(key: String) {
        globalPrefs.remove(key)
    }

    override fun clear() {
        globalPrefs.clear()
    }

}