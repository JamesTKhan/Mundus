package com.mbrlabs.mundus.pluginapi;

import com.badlogic.gdx.utils.OrderedMap;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.pluginapi.ui.RootWidget;
import org.pf4j.ExtensionPoint;

public interface ComponentExtension extends ExtensionPoint {

    Component.Type getComponentType();

    String getComponentName();

    Component createComponent(GameObject gameObject);

    void setupComponentInspectorWidget(Component component, RootWidget rootWidget);

    default OrderedMap<String, String> getComponentConfig(Component component) {
        return null;
    }

    default Component loadComponentConfig(GameObject gameObject, OrderedMap<String, String> config) {
        return null;
    }
}
