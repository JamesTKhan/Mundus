package com.mbrlabs.mundus.commons.shadows;

import com.badlogic.gdx.graphics.g3d.Attribute;

/**
 * @author JamesTKhan
 * @version October 07, 2023
 */
public class ShadowStrategyAttribute extends Attribute {
    public static final String ClassicShadowMapAlias = "classicShadowMap";
    public static final long ClassicShadowMap = register(ClassicShadowMapAlias);

    public static final String VarianceShadowMapAlias = "varianceShadowMap";
    public static final long VarianceShadowMap = register(VarianceShadowMapAlias);

    public ShadowStrategyAttribute(final long type) {
        super(type);
    }
    @Override
    public int compareTo(Attribute o) {
        return (int)(type - o.type);
    }

    @Override
    public Attribute copy() {
        return new ShadowStrategyAttribute(type);
    }

}
