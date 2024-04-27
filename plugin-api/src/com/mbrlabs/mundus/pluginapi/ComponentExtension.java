package com.mbrlabs.mundus.pluginapi;

import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import org.pf4j.ExtensionPoint;

public interface ComponentExtension extends ExtensionPoint {

    String getComponentName();

    Component createComponent(GameObject gameObject);
}
