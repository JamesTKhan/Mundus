package com.mbrlabs.mundus.commons.shaders;

import com.mbrlabs.mundus.commons.shadows.ShadowStrategyAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * @author JamesTKhan
 * @version October 02, 2023
 */
public class VarianceDepthShader extends DepthShader {
    protected static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/depth.vsm.frag.glsl";

    public VarianceDepthShader() {
        String prefix = "#define " + ShadowStrategyAttribute.VarianceShadowMapAlias + "Flag\n";
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, this, prefix);
    }

}
