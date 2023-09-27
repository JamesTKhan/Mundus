package com.mbrlabs.mundus.commons.assets.meta;

import com.badlogic.gdx.graphics.Texture;

/**
 * @author JamesTKhan
 * @version September 19, 2023
 */
public class MetaTexture {
    public static final Texture.TextureFilter DEFAULT_MIN_FILTER = Texture.TextureFilter.MipMapLinearLinear;
    public static final Texture.TextureFilter DEFAULT_MAG_FILTER = Texture.TextureFilter.Linear;

    public static final String JSON_MIN_FILTER = "minFilter";
    public static final String JSON_MAG_FILTER = "magFilter";
    private int minFilter = DEFAULT_MIN_FILTER.getGLEnum();
    private int magFilter = DEFAULT_MAG_FILTER.getGLEnum();

    public int getMinFilter() {
        return minFilter;
    }

    public void setMinFilter(int minFilter) {
        this.minFilter = minFilter;
    }

    public void setMinFilter(Texture.TextureFilter minFilter) {
        this.minFilter = minFilter.getGLEnum();
    }

    public int getMagFilter() {
        return magFilter;
    }

    public void setMagFilter(int magFilter) {
        this.magFilter = magFilter;
    }

    public void setMagFilter(Texture.TextureFilter magFilter) {
        this.magFilter = magFilter.getGLEnum();
    }

    public Texture.TextureFilter getMinFilterEnum() {
        for (Texture.TextureFilter filter : Texture.TextureFilter.values()) {
            if (filter.getGLEnum() == minFilter) {
                return filter;
            }
        }
        return DEFAULT_MIN_FILTER;
    }

    public Texture.TextureFilter getMagFilterEnum() {
        for (Texture.TextureFilter filter : Texture.TextureFilter.values()) {
            if (filter.getGLEnum() == magFilter) {
                return filter;
            }
        }
        return DEFAULT_MAG_FILTER;
    }

    @Override
    public String toString() {
        return "MetaTexture{" +
                ", minFilter='" + minFilter + '\'' +
                ", magFilter='" + magFilter + '\'' +
                '}';
    }
}
