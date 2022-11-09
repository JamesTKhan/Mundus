package com.mbrlabs.mundus.commons.assets.meta;

/**
 * Simple POJO for the loading and saving of Terrain Layers
 * @author JamesTKhan
 * @version November 09, 2022
 */
public class MetaTerrainLayer {
    private String name;
    private String textureAssetId;
    private boolean active;
    private float minHeight;
    private float maxHeight;
    private float slopeStrength;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextureAssetId() {
        return textureAssetId;
    }

    public void setTextureAssetId(String textureAssetId) {
        this.textureAssetId = textureAssetId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    public float getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    public float getSlopeStrength() {
        return slopeStrength;
    }

    public void setSlopeStrength(float slopeStrength) {
        this.slopeStrength = slopeStrength;
    }
}
