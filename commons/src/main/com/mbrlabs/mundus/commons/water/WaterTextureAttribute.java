package com.mbrlabs.mundus.commons.water;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class WaterTextureAttribute extends Attribute {
    public final static String DudvAlias = "dudvTexture";
    public final static long Dudv = register(DudvAlias);

    public final static String NormalMapAlias = "waterNormalMap";
    public final static long NormalMap = register(NormalMapAlias);

    public final static String ReflectionAlias = "reflection";
    public final static long Reflection = register(ReflectionAlias);

    public final static String RefractionAlias = "refraction";
    public final static long Refraction = register(RefractionAlias);

    public final static String RefractionDepthAlias = "refractionDepth";
    public final static long RefractionDepth = register(RefractionDepthAlias);

    private Texture texture;

    protected WaterTextureAttribute(long type, Texture texture) {
        super(type);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Attribute copy() {
        //todo
        return null;
    }

    @Override
    public int compareTo(Attribute o) {
        return 0;
    }
}
