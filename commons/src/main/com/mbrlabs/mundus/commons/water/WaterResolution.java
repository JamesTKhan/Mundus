package com.mbrlabs.mundus.commons.water;

import com.badlogic.gdx.math.Vector2;

public enum WaterResolution {
    _256("256x256"),
    _512("512x512"),
    _1024("1024x1024"),
    _2048("2048x2048"),

    //TODO Remove these values eventually, but kept here to support existing projects
    @Deprecated
    _1280("1280x720"),
    @Deprecated
    _1600("1600x900"),
    @Deprecated
    _1920("1920x1080");

    public static final WaterResolution DEFAULT_WATER_RESOLUTION = _512;
    private final String value;

    WaterResolution(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Vector2 getResolutionValues() {
        if (this == _256) {
            return new Vector2(256, 256);
        }
        if (this == _512) {
            return new Vector2(512, 512);
        }
        if (this == _1024) {
            return new Vector2(1024, 1024);
        }
        if (this == _2048) {
            return new Vector2(2048, 2048);
        }

        return new Vector2(512, 512);
    }

    public static WaterResolution valueFromString(String string) {
        if (_256.value.equals(string)) {
            return _256;
        }
        if (_512.value.equals(string)) {
            return _512;
        }
        if (_1024.value.equals(string)) {
            return _1024;
        }
        if (_2048.value.equals(string)) {
            return _2048;
        }
        return DEFAULT_WATER_RESOLUTION;
    }
}
