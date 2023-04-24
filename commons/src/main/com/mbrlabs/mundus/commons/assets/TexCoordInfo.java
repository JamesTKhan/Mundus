package com.mbrlabs.mundus.commons.assets;

/**
 * Storage for UV related Texture info.
 *
 * @author JamesTKhan
 * @version July 20, 2022
 */
public class TexCoordInfo {
    public final String PROP_UV;
    public final String PROP_OFFSET_U;
    public final String PROP_OFFSET_V;
    public final String PROP_SCALE_U;
    public final String PROP_SCALE_V;
    public final String PROP_ROTATION_UV;

    public String property;
    public int uvIndex = 0;
    public float offsetU = 0;
    public float offsetV = 0;
    public float scaleU = 1;
    public float scaleV = 1;
    public float rotationUV = 0;

    public TexCoordInfo(String property) {
        this.property = property;

        // Generate props based on given property string (diffuse.uvIndex, etc..)
        PROP_UV = property + ".uvIndex";
        PROP_OFFSET_U = property + ".offsetU";
        PROP_OFFSET_V = property + ".offsetV";
        PROP_SCALE_U = property + ".scaleU";
        PROP_SCALE_V = property + ".scaleV";
        PROP_ROTATION_UV = property + ".rotationUV";
    }

    public TexCoordInfo deepCopy() {
        TexCoordInfo copied = new TexCoordInfo(this.property);
        copied.uvIndex = this.uvIndex;
        copied.offsetU = this.offsetU;
        copied.offsetV = this.offsetV;
        copied.scaleU = this.scaleU;
        copied.scaleV = this.scaleV;
        copied.rotationUV = this.rotationUV;
        return copied;
    }
}
