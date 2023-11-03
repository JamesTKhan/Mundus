package com.mbrlabs.mundus.pluginapi;

import org.pf4j.ExtensionPoint;

public interface EventExtension extends ExtensionPoint {

    void manageEvents(PluginEventManager pluginEventManager);
}
