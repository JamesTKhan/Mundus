package com.mbrlabs.mundus.commons.rendering.shadows;

import com.mbrlabs.mundus.commons.Scene;

/**
 * @author JamesTKhan
 * @version October 03, 2023
 */
public class ClassicShadowMap extends BaseShadowMap {

    @Override
    public void renderShadowMap(Scene scene) {
        // Passing null as depthShader will use the default depth shader
        renderShadowMap(scene, null);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }
}
