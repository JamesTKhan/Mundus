package com.mbrlabs.mundus.commons.water;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class WaterFloatAttribute extends FloatAttribute {
    public static final String TilingAlias = "tiling";
    public static final long Tiling = register(TilingAlias);

    public static final String WaveStrengthAlias = "waveStrength";
    public static final long WaveStrength = register(WaveStrengthAlias);

    public static final String WaveSpeedAlias = "waveSpeed";
    public static final long WaveSpeed = register(WaveSpeedAlias);

    public WaterFloatAttribute(long type) {
        super(type);
    }

    public WaterFloatAttribute(long type, float value) {
        super(type, value);
    }
}
