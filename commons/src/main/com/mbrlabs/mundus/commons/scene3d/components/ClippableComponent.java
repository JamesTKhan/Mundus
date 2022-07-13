package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a component that can be clipped by clipping planes
 */
public interface ClippableComponent {
    void render(float delta, Vector3 clippingPlane, float clipHeight);

    /**
     * For rendering refraction objects depth with custom depth shader to be captured in a depth texture.
     * Using custom depth shader to support GLES2.0 as it does not support depth texture attachments.
     * @param delta
     * @param clippingPlane
     * @param clipHeight
     * @param shader
     */
    void renderDepth(float delta, Vector3 clippingPlane, float clipHeight, Shader shader);
}
