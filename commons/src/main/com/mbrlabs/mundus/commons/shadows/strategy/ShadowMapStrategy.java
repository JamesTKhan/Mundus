package com.mbrlabs.mundus.commons.shadows.strategy;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.Scene;

/**
 * @author JamesTKhan
 * @version October 03, 2023
 */
public interface ShadowMapStrategy extends Disposable {

    /**
     * Render models to the shadow map .This is called by the render method normally, but if using post-processing
     * you may want to call this method directly.
     */
    void renderShadowMap(Scene scene);

    /**
     * Returns the attribute that is used to identify the shadow map strategy.
     * @return the attribute
     */
    Attribute getAttribute();
}
