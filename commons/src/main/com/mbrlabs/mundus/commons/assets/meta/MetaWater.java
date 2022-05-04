package com.mbrlabs.mundus.commons.assets.meta;

public class MetaWater {
    public static final String JSON_SIZE = "size";
    public static final String JSON_DUDV = "dudv";
    public static final String JSON_NORMAL_MAP = "normalMap";
    public static final String JSON_TILING = "tiling";
    public static final String JSON_WAVE_STRENGTH = "waveStrength";
    public static final String JSON_WAVE_SPEED = "waveSpeed";

    private int size;
    private float tiling;
    private float waveStrength;
    private float waveSpeed;
    private String dudvMap;
    private String normalMap;

    public String getDudvMap() {
        return dudvMap;
    }

    public void setDudvMap(String dudvMap) {
        this.dudvMap = dudvMap;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getNormalMap() {
        return normalMap;
    }

    public float getTiling() {
        return tiling;
    }

    public void setTiling(float tiling) {
        this.tiling = tiling;
    }

    public float getWaveStrength() {
        return waveStrength;
    }

    public void setWaveStrength(float waveStrength) {
        this.waveStrength = waveStrength;
    }

    public void setNormalMap(String normalMap) {
        this.normalMap = normalMap;
    }

    public float getWaveSpeed() {
        return waveSpeed;
    }

    public void setWaveSpeed(float waveSpeed) {
        this.waveSpeed = waveSpeed;
    }

    @Override
    public String toString() {
        return "MetaWater{" +
                "size=" + size +
                ", dudvmap='" + dudvMap + '\'' +
                ", normalmap='" + normalMap + '\'' +
                ", tiling='" + tiling + '\'' +
                ", waveStrength='" + waveStrength + '\'' +
                ", waveSpeed='" + waveSpeed + '\'' +
                '}';
    }
}
