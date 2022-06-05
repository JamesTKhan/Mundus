package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class PointLight extends BaseLight {
    public static final float DEFAULT_INTENSITY = 5.0f;
    public static final Color DEFAULT_COLOR = Color.WHITE.cpy();

    public final Vector3 position = new Vector3();
    public Attenuation attenuation;

    public PointLight() {
        lightType = LightType.POINT_LIGHT;
        intensity = DEFAULT_INTENSITY;
        color.set(DEFAULT_COLOR);
        attenuation = new Attenuation();
    }
}
