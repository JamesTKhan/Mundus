package com.mbrlabs.mundus.editor.core.plugin

import org.pf4j.PluginManager

/**
 * Provider for PluginManager. Needed because dependency injection with interfaces does
 * not seem to work, so we provide this POJO to inject the PluginManager.
 */
class PluginManagerProvider(
    val pluginManager: PluginManager
)
