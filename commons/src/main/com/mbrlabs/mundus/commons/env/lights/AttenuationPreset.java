package com.mbrlabs.mundus.commons.env.lights;

/**
 * Attenuation preset values for lighting. Chart from https://learnopengl.com/Lighting/Light-casters
 * distance     const   linear  exponential
 * 7 	        1.0 	0.7 	1.8
 * 13 	        1.0 	0.35 	0.44
 * 20 	        1.0 	0.22 	0.20
 * 32 	        1.0 	0.14 	0.07
 * 50 	        1.0 	0.09 	0.032
 * 65 	        1.0 	0.07 	0.017
 * 100 	        1.0 	0.045 	0.0075
 * 160 	        1.0 	0.027 	0.0028
 * 200 	        1.0 	0.022 	0.0019
 * 325 	        1.0 	0.014 	0.0007
 * 600 	        1.0 	0.007 	0.0002
 * 3250         1.0 	0.0014 	0.000007
 */

public enum AttenuationPreset {
    _7("7"),
    _13("13"),
    _20("20"),
    _32("32"),
    _65("65"),
    _100("100"),
    _160("160"),
    _200("200"),
    _325("325"),
    _600("600"),
    _3250("3250");
    
    private final String value;
    private static final float[] DISTANCE_7 = {1.0f, 0.7f, 1.8f};
    private static final float[] DISTANCE_13 = {1.0f, 0.35f, 0.44f};
    private static final float[] DISTANCE_20 = {1.0f, 0.22f, 0.20f};
    private static final float[] DISTANCE_32 = {1.0f, 0.14f, 0.07f};
    private static final float[] DISTANCE_50 = {1.0f, 0.09f, 0.032f};
    private static final float[] DISTANCE_65 = {1.0f, 0.07f, 0.017f};
    private static final float[] DISTANCE_100 = {1.0f, 0.045f, 0.0075f};
    private static final float[] DISTANCE_160 = {1.0f, 0.027f, 0.0028f};
    private static final float[] DISTANCE_200 = {1.0f, 0.022f, 0.0019f};
    private static final float[] DISTANCE_325 = {1.0f, 0.014f, 0.0007f};
    private static final float[] DISTANCE_600 = {1.0f, 0.007f, 0.0002f};
    private static final float[] DISTANCE_3250 = {1.0f, 0.0014f, 0.000007f};

    AttenuationPreset(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Attenuation getAttenuation() {
        float[] array;
        switch (this) {
            case _7:
                array = DISTANCE_7;
                break;
            case _13:
                array = DISTANCE_13;
                break;
            case _20:
                array = DISTANCE_20;
                break;
            case _32:
                array = DISTANCE_32;
                break;
            case _65:
                array = DISTANCE_65;
                break;
            case _100:
                array = DISTANCE_100;
                break;
            case _160:
                array = DISTANCE_160;
                break;
            case _200:
                array = DISTANCE_200;
                break;
            case _325:
                array = DISTANCE_325;
                break;
            case _600:
                array = DISTANCE_600;
                break;
            case _3250:
                array = DISTANCE_3250;
                break;
            default:
                array = DISTANCE_100;
        }
        
        return new Attenuation(array[0], array[1], array[2]);
    }

    public static AttenuationPreset valueFromString(String string) {
        for (AttenuationPreset preset : values()) {
            if (preset.value.equals(string)) {
                return preset;
            }
        }

        return _100;
    }


    public static String valueFromAttenuation(Attenuation attenuation) {
        for (AttenuationPreset preset : values()) {
            if (preset.getAttenuation().equals(attenuation)) {
                return preset.value;
            }
        }

        return null;
    }
}
