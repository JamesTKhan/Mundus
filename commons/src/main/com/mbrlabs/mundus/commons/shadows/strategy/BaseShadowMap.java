package com.mbrlabs.mundus.commons.shadows.strategy;

import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.rendering.DefaultSceneRenderer;
import com.mbrlabs.mundus.commons.shadows.ShadowResolution;
import com.mbrlabs.mundus.commons.shadows.ShadowStrategyAttribute;

/**
 * @author JamesTKhan
 * @version October 04, 2023
 */
public abstract class BaseShadowMap implements ShadowMapStrategy, Disposable {

    protected ShadowStrategyAttribute attribute;
    private boolean attributeSet = false;

    protected void renderShadowMap(Scene scene, Shader depthShader) {
        if (attribute == null) {
            throw new IllegalStateException("ShadowMapStrategyAttribute not set");
        }

        if (scene.dirLight == null) {
            scene.setShadowQuality(ShadowResolution.DEFAULT_SHADOW_RESOLUTION);
        }

        if (!scene.dirLight.isCastsShadows()) {
            scene.environment.shadowMap = null;
            return;
        }

        if (!attributeSet) {
            scene.environment.set(attribute);
            attributeSet = true;
        }

        scene.environment.shadowMap = scene.dirLight;

        scene.dirLight.setCenter(scene.cam.position);
        scene.dirLight.begin();
        scene.depthBatch.begin(scene.dirLight.getCamera());
        scene.setClippingPlane(DefaultSceneRenderer.clippingPlaneDisable, 0);
        scene.getSceneRenderer().renderComponents(scene, scene.depthBatch, scene.sceneGraph.getRoot(), depthShader, true);
        scene.modelCacheManager.triggerBeforeDepthRenderEvent();
        scene.depthBatch.render(scene.modelCacheManager.modelCache, scene.environment);
        scene.depthBatch.end();
        scene.dirLight.end();
    }

    public ShadowStrategyAttribute getAttribute() {
        return attribute;
    }
}
