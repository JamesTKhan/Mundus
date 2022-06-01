package com.mbrlabs.mundus.commons.env.lights;

public class Attenuation {
    public static final float DEFAULT_CONSTANT = 1.0f;
    public static final float DEFAULT_LINEAR = 0.045f;
    public static final float DEFAULT_EXPONENTIAL = 0.0075f;

    public float constant;
    public float linear;
    public float exponential;

    public Attenuation() {
        this.constant = DEFAULT_CONSTANT;
        this.linear = DEFAULT_LINEAR;
        exponential = DEFAULT_EXPONENTIAL;
    }

}
