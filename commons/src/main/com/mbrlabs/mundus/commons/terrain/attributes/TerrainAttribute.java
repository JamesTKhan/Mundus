package com.mbrlabs.mundus.commons.terrain.attributes;

import com.mbrlabs.mundus.commons.MundusAttribute;

/**
 * @author JamesTKhan
 * @version August 13, 2022
 */
public class TerrainAttribute extends MundusAttribute {
    // Diffuse Textures
    public final static String DiffuseBaseAlias = "DiffuseBase";
    public final static long DiffuseBase = register(DiffuseBaseAlias);
    public final static String SplatMapAlias = "SplatMap";
    public final static long SplatMap = register(SplatMapAlias);
    public final static String DiffuseRAlias = "DiffuseR";
    public final static long DiffuseR = register(DiffuseRAlias);
    public final static String DiffuseGAlias = "DiffuseG";
    public final static long DiffuseG = register(DiffuseGAlias);
    public final static String DiffuseBAlias = "DiffuseB";
    public final static long DiffuseB = register(DiffuseBAlias);
    public final static String DiffuseAAlias = "DiffuseA";
    public final static long DiffuseA = register(DiffuseAAlias);

    // Normal Maps
    public final static String NormalMapBaseAlias = "normalMapBase";
    public final static long NormalMapBase = register(NormalMapBaseAlias);
    public final static String NormalMapRAlias = "normalMapR";
    public final static long NormalMapR = register(NormalMapRAlias);
    public final static String NormalMapGAlias = "normalMapG";
    public final static long NormalMapG = register(NormalMapGAlias);
    public final static String NormalMapBAlias = "normalMapB";
    public final static long NormalMapB = register(NormalMapBAlias);
    public final static String NormalMapAAlias = "normalMapA";
    public final static long NormalMapA = register(NormalMapAAlias);
    public final static String TriplanarAlias = "triplanar";
    public final static long Triplanar = register(TriplanarAlias);

    public TerrainAttribute(long type) {
        super(type);
    }

    @Override
    public MundusAttribute copy() {
        return new TerrainAttribute(type);
    }

    @Override
    public int compareTo(MundusAttribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        return 0;
    }
}
