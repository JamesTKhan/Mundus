package com.mbrlabs.mundus.commons.water.attributes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import com.mbrlabs.mundus.commons.MundusAttribute;
/**
 * @author JamesTKhan
 * @version October 16, 2022
 */
public class WaterTextureAttribute extends MundusAttribute {
    public final static String DudvAlias = "dudvTexture";
    public final static long Dudv = register(DudvAlias);

    public final static String NormalMapAlias = "waterNormalMap";
    public final static long NormalMap = register(NormalMapAlias);

    public final static String ReflectionAlias = "reflection";
    public final static long Reflection = register(ReflectionAlias);

    public final static String FoamAlias = "waterFoam";
    public final static long Foam = register(FoamAlias);

    public final static String RefractionAlias = "refraction";
    public final static long Refraction = register(RefractionAlias);

    public final static String RefractionDepthAlias = "refractionDepth";
    public final static long RefractionDepth = register(RefractionDepthAlias);

    protected static long Mask = Dudv | NormalMap | Reflection | Foam | Refraction | RefractionDepth;

    public final static boolean is (final long mask) {
        return (mask & Mask) != 0;
    }

    public final TextureDescriptor<Texture> textureDescription;
    public float offsetU = 0;
    public float offsetV = 0;
    public float scaleU = 1;
    public float scaleV = 1;
    /** The index of the texture coordinate vertex attribute to use for this TextureAttribute. Whether this value is used, depends
     * on the shader and {@link Attribute#type} value. For basic (model specific) types
     * etc.), this value is usually ignored and the first texture coordinate vertex attribute is used. */
    public int uvIndex = 0;

    public WaterTextureAttribute(final long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        textureDescription = new TextureDescriptor<Texture>();
    }

    public <T extends Texture> WaterTextureAttribute(final long type, final TextureDescriptor<T> textureDescription) {
        this(type);
        this.textureDescription.set(textureDescription);
    }

    public <T extends Texture> WaterTextureAttribute(final long type, final TextureDescriptor<T> textureDescription, float offsetU,
                                                     float offsetV, float scaleU, float scaleV, int uvIndex) {
        this(type, textureDescription);
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.scaleU = scaleU;
        this.scaleV = scaleV;
        this.uvIndex = uvIndex;
    }

    public <T extends Texture> WaterTextureAttribute(final long type, final TextureDescriptor<T> textureDescription, float offsetU,
                                                     float offsetV, float scaleU, float scaleV) {
        this(type, textureDescription, offsetU, offsetV, scaleU, scaleV, 0);
    }

    public WaterTextureAttribute(final long type, final Texture texture) {
        this(type);
        textureDescription.texture = texture;
    }

    public WaterTextureAttribute(final long type, final TextureRegion region) {
        this(type);
        set(region);
    }

    public WaterTextureAttribute(final WaterTextureAttribute copyFrom) {
        this(copyFrom.type, copyFrom.textureDescription, copyFrom.offsetU, copyFrom.offsetV, copyFrom.scaleU, copyFrom.scaleV,
                copyFrom.uvIndex);
    }

    public void set (final TextureRegion region) {
        textureDescription.texture = region.getTexture();
        offsetU = region.getU();
        offsetV = region.getV();
        scaleU = region.getU2() - offsetU;
        scaleV = region.getV2() - offsetV;
    }

    @Override
    public MundusAttribute copy () {
        return new WaterTextureAttribute(this);
    }

    @Override
    public int hashCode () {
        int result = super.hashCode();
        result = 991 * result + textureDescription.hashCode();
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetU);
        result = 991 * result + NumberUtils.floatToRawIntBits(offsetV);
        result = 991 * result + NumberUtils.floatToRawIntBits(scaleU);
        result = 991 * result + NumberUtils.floatToRawIntBits(scaleV);
        result = 991 * result + uvIndex;
        return result;
    }

    @Override
    public int compareTo (MundusAttribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        WaterTextureAttribute other = (WaterTextureAttribute)o;
        final int c = textureDescription.compareTo(other.textureDescription);
        if (c != 0) return c;
        if (uvIndex != other.uvIndex) return uvIndex - other.uvIndex;
        if (!MathUtils.isEqual(scaleU, other.scaleU)) return scaleU > other.scaleU ? 1 : -1;
        if (!MathUtils.isEqual(scaleV, other.scaleV)) return scaleV > other.scaleV ? 1 : -1;
        if (!MathUtils.isEqual(offsetU, other.offsetU)) return offsetU > other.offsetU ? 1 : -1;
        if (!MathUtils.isEqual(offsetV, other.offsetV)) return offsetV > other.offsetV ? 1 : -1;
        return 0;
    }
}

