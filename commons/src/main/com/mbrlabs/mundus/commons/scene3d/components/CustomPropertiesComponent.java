package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.utils.OrderedMap;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class CustomPropertiesComponent extends AbstractComponent {

    private final OrderedMap<String, String> customProperties;

    public CustomPropertiesComponent(final GameObject go) {
        super(go);
        this.customProperties = new OrderedMap<>(5);
        type = Type.CUSTOM_PROPERTIES;
    }

    @Override
    public void render(final float delta) {
        // NOOP
    }

    @Override
    public void update(final float delta) {
        // NOOP
    }

    @Override
    public Component clone(final GameObject go) {
        return null;
    }

    public OrderedMap<String, String> getCustomProperties() {
        return customProperties;
    }
}
