package com.mbrlabs.mundus.commons.env.lights;

import java.util.Objects;

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
        this.exponential = DEFAULT_EXPONENTIAL;
    }

    public Attenuation(float constant, float linear, float exponential) {
        this.constant = constant;
        this.linear = linear;
        this.exponential = exponential;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attenuation that = (Attenuation) o;
        return Float.compare(that.constant, constant) == 0 && Float.compare(that.linear, linear) == 0 && Float.compare(that.exponential, exponential) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constant, linear, exponential);
    }
}
