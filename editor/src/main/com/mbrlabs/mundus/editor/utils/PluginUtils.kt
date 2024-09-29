package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter
import com.mbrlabs.mundus.pluginapi.ComponentExtension
import org.pf4j.PluginManager

/**
 * The util methods for plugin.
 */
object PluginUtils {

    /**
     * Gets CustomComponentConverter for all plugins.
     */
    fun getCustomComponentConverters(pluginManager: PluginManager): Array<CustomComponentConverter> {
        val customComponentConverters = Array<CustomComponentConverter>()
        pluginManager.getExtensions(ComponentExtension::class.java).forEach {
            val customComponentConverter = it.converter
            if (customComponentConverter != null) {
                customComponentConverters.add(customComponentConverter)
            }
        }

        return customComponentConverters
    }
}
