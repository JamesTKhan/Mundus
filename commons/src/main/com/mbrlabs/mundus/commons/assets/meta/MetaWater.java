package com.mbrlabs.mundus.commons.assets.meta;

public class MetaWater {
    public static final String JSON_SIZE = "size";
    public static final String JSON_DUDV = "dudv";
    public static final String JSON_NORMAL_MAP = "normalMap";

    private int size;
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

    public void setNormalMap(String normalMap) {
        this.normalMap = normalMap;
    }

    @Override
    public String toString() {
        return "MetaWater{" +
                "size=" + size +
                ", dudvmap='" + dudvMap + '\'' +
                ", normalmap='" + normalMap + '\'' +
                '}';
    }
}
