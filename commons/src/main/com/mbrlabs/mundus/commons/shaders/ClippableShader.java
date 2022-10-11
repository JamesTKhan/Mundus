package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.math.Vector3;

/**
 * For use with shaders that implement a clipping plane, extend this shader class.
 */
public abstract class ClippableShader extends BaseShader {
    protected final int UNIFORM_CLIP_PLANE = register(new Uniform("u_clipPlane"));

    // By default, clipping is disabled
    protected Vector3 clippingPlane = new Vector3(0.0f,0.0f, 0.0f);
    protected float clippingHeight = 0f;

    @Override
    public void render(Renderable renderable) {
        // Set clipping plane uniforms
        set(UNIFORM_CLIP_PLANE, clippingPlane.x, clippingPlane.y, clippingPlane.z, clippingHeight);
    }

    public void setClippingPlane(Vector3 clippingPlane) {
        this.clippingPlane = clippingPlane;
    }

    public void setClippingHeight(float clippingHeight) {
        this.clippingHeight = clippingHeight;
    }
}
