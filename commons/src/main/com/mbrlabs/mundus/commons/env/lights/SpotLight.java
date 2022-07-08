package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * @author James Pooley
 * @version June 02, 2022
 */
public class SpotLight extends PointLight {
    public static final Vector3 DEFAULT_DIRECTION = Vector3.X.cpy();
    public static final float DEFAULT_CUTOFF = 40.0f;

    public Vector3 direction;
    private float cutoff;

    // For PBR Shader only
    public float exponent;
    public float cutoffAngle;

    public SpotLight() {
        super();
        this.lightType = LightType.SPOT_LIGHT;
        this.direction = DEFAULT_DIRECTION.cpy();
        this.cutoff = DEFAULT_CUTOFF;

        setConeDeg(cutoff, cutoff / 2f);
    }

    public SpotLight(Vector3 direction, float cutoff) {
        super();
        this.lightType = LightType.SPOT_LIGHT;
        this.direction = direction;
        this.cutoff = cutoff;

        setConeDeg(cutoff, cutoff / 2f);
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
        setConeDeg(cutoff, cutoff / 2f);
    }

    // From gdx-gltf SpotlightEX
    private void setConeDeg(float outerConeAngleDeg, float innerConeAngleDeg)
    {
        setConeRad(outerConeAngleDeg * MathUtils.degreesToRadians, innerConeAngleDeg * MathUtils.degreesToRadians);
    }

    // From gdx-gltf SpotlightEX
    private void setConeRad(float outerConeAngleRad, float innerConeAngleRad)
    {
        // from https://github.com/KhronosGroup/glTF/blob/master/extensions/2.0/Khronos/KHR_lights_punctual/README.md#inner-and-outer-cone-angles
        float cosOuterAngle = (float)Math.cos(outerConeAngleRad);
        float cosInnerAngle = (float)Math.cos(innerConeAngleRad);
        float lightAngleScale = 1.0f / Math.max(0.001f, cosInnerAngle - cosOuterAngle);
        float lightAngleOffset = -cosOuterAngle * lightAngleScale;

        cutoffAngle = lightAngleOffset;
        exponent = lightAngleScale;
    }
}
