package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.env.lights.LightType;

/**
 * @author JamesTKhan
 * @version June 07, 2022
 */
public class LightComponentDTO {
    private LightType lightType;

    // Spotlight
    private Vector3 direction;
    private float cutoff;

    // Point
    private Vector3 position;

    // Attenuation
    private float constant;
    private float linear;
    private float exponential;

    // Base
    private Color color;
    private float intensity;

    public LightType getLightType() {
        return lightType;
    }

    public void setLightType(LightType lightType) {
        this.lightType = lightType;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getExponential() {
        return exponential;
    }

    public void setExponential(float exponential) {
        this.exponential = exponential;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
