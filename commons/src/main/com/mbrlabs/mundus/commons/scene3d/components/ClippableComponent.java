package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.math.Vector3;

/**
 * Represents a component that can be clipped by clipping planes
 */
public interface ClippableComponent {
    void render(float delta, Vector3 clippingPlane, float clipHeight);
}
