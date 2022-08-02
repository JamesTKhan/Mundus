package com.mbrlabs.mundus.commons.shadows;

import com.badlogic.gdx.math.Vector2;

/**
 * Represents options for Shadow Mapping texture resolution. Higher resolutions will look
 * sharper but use more resources.
 *
 * @author JamesTKhan
 * @version June 12, 2022
 */
public enum ShadowResolution {
    _512("512x512"),
    _1024("1024x1024"),
    _2048("2048x2048"),
    _4096("4096x4096");

    public static final ShadowResolution DEFAULT_SHADOW_RESOLUTION = _2048;
    private final String value;

    ShadowResolution(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Vector2 getResolutionValues() {
        if (this == _1024) {
            return new Vector2(1024, 1024);
        }
        if (this == _512) {
            return new Vector2(512, 512);
        }
        if (this == _2048) {
            return new Vector2(2048, 2048);
        }
        if (this == _4096) {
            return new Vector2(4096, 4096);
        }

        return new Vector2(2048, 2048);
    }

    public static ShadowResolution valueFromString(String string) {
        for (ShadowResolution res : values()) {
            if (res.value.equals(string)) {
                return res;
            }
        }

        return DEFAULT_SHADOW_RESOLUTION;
    }

}
