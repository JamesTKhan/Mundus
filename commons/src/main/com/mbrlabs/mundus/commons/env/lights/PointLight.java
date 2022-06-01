package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.math.Vector3;

public class PointLight extends BaseLight {
    public final Vector3 position = new Vector3();
    public Attenuation attenuation;

    public PointLight() {
        attenuation = new Attenuation();
    }
}
