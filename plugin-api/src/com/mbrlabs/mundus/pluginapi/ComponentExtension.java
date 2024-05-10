package com.mbrlabs.mundus.pluginapi;

import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.pluginapi.ui.RootWidget;
import org.pf4j.ExtensionPoint;

public interface ComponentExtension extends ExtensionPoint {

    Component.Type getComponentType();

    String getComponentName();

    Component createComponent(GameObject gameObject);

    void setupComponentInspectorWidget(Component component, RootWidget rootWidget);

    CustomComponentConverter getConverter();
}
