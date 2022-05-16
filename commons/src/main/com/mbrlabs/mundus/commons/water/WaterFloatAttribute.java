package com.mbrlabs.mundus.commons.water;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class WaterFloatAttribute extends FloatAttribute {
    public static final String TilingAlias = "tiling";
    public static final long Tiling = register(TilingAlias);

    public static final String WaveStrengthAlias = "waveStrength";
    public static final long WaveStrength = register(WaveStrengthAlias);

    public static final String WaveSpeedAlias = "waveSpeed";
    public static final long WaveSpeed = register(WaveSpeedAlias);

    public static final String ReflectivityAlias = "reflectivity";
    public static final long Reflectivity = register(ReflectivityAlias);

    public static final String ShineDamperAlias = "shineDamper";
    public static final long ShineDamper = register(ShineDamperAlias);

    public static final String FoamPatternScaleAlias = "foamPatternScale";
    public static final long FoamPatternScale = register(FoamPatternScaleAlias);

    public static final String FoamEdgeBiasAlias = "foamEdgeBias";
    public static final long FoamEdgeBias = register(FoamEdgeBiasAlias);

    public static final String FoamScrollSpeedAlias = "foamScrollSpeed";
    public static final long FoamScrollSpeed = register(FoamScrollSpeedAlias);

    public static final String FoamFallOffDistanceAlias = "foamFallOffDistance";
    public static final long FoamFallOffDistance = register(FoamFallOffDistanceAlias);

    public static final String FoamEdgeDistanceAlias = "foamEdgeDistance";
    public static final long FoamEdgeDistance = register(FoamEdgeDistanceAlias);

    public WaterFloatAttribute(long type, float value) {
        super(type, value);
    }
}
