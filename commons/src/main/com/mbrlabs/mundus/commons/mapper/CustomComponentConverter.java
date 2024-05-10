package com.mbrlabs.mundus.commons.mapper;

import com.badlogic.gdx.utils.OrderedMap;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public interface CustomComponentConverter {

    Component.Type getComponentType();

    OrderedMap<String, String> convert(Component component);

    Component convert(GameObject gameObject, OrderedMap<String, String> componentProperties);
}
