package com.mbrlabs.mundus.editor.preferences

/**
 * @author JamesTKhan
 * @version July 15, 2022
 */
interface PreferencesManager {
    fun set(key: String, value: Any)
    fun get(key: String, type: Class<*>): Any?

    fun getBoolean(key: String): Boolean
    fun getBoolean(key: String, defValue: Boolean): Boolean
    fun getInteger(key: String): Int
    fun getInteger(key: String, defValue: Int): Int
    fun getLong(key: String): Long
    fun getLong(key: String, defValue: Long): Long
    fun getFloat(key: String): Float
    fun getFloat(key: String, defValue: Float): Float
    fun getString(key: String): String
    fun getString(key: String?, defValue: String?): String

    fun contains(key: String): Boolean
    fun remove(key: String)
    fun clear()
}