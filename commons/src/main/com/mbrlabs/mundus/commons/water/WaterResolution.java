package com.mbrlabs.mundus.commons.water;

import com.badlogic.gdx.math.Vector2;

public enum WaterResolution {
    _1024("1024x576"),
    _1280("1280x720"),
    _1600("1600x900"),
    _1920("1920x1080");

    public static final WaterResolution DEFAULT_WATER_RESOLUTION = _1280;
    private final String value;

    WaterResolution(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Vector2 getResolutionValues() {
        if (this == _1024) {
            return new Vector2(1024, 576);
        }
        if (this == _1280) {
            return new Vector2(1280, 720);
        }
        if (this == _1600) {
            return new Vector2(1600, 900);
        }
        if (this == _1920) {
            return new Vector2(1920, 1080);
        }

        return new Vector2(1280, 720);
    }

    public static WaterResolution valueFromString(String string) {
        if (_1024.value.equals(string)) {
            return _1024;
        }
        if (_1280.value.equals(string)) {
            return _1280;
        }
        if (_1600.value.equals(string)) {
            return _1600;
        }
        if (_1920.value.equals(string)) {
            return _1920;
        }
        return DEFAULT_WATER_RESOLUTION;
    }
}
