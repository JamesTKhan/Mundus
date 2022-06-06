package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.math.Vector3;

/**
 * @author James Pooley
 * @version June 02, 2022
 */
public class SpotLight extends PointLight {
    public static final Vector3 DEFAULT_DIRECTION = Vector3.X.cpy();
    public static final float DEFAULT_CUTOFF = 40.0f;

    public Vector3 direction;
    public float cutoff;

    public SpotLight() {
        super();
        this.lightType = LightType.SPOT_LIGHT;
        this.direction = DEFAULT_DIRECTION;
        this.cutoff = DEFAULT_CUTOFF;
    }

    public SpotLight(Vector3 direction, float cutoff) {
        super();
        this.lightType = LightType.SPOT_LIGHT;
        this.direction = direction;
        this.cutoff = cutoff;
    }
}
