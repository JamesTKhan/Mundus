package com.mbrlabs.mundus.commons.shadows.strategy;

import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.shaders.DepthShader;
import com.mbrlabs.mundus.commons.shadows.ShadowStrategyAttribute;

/**
 * @author JamesTKhan
 * @version October 03, 2023
 */
public class ClassicShadowMap extends BaseShadowMap {
    private final DepthShader shader;

    public ClassicShadowMap() {
        attribute = new ShadowStrategyAttribute(ShadowStrategyAttribute.ClassicShadowMap);
        shader = new DepthShader();
        shader.init();
    }

    @Override
    public void renderShadowMap(Scene scene) {
        renderShadowMap(scene, shader);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }
}
