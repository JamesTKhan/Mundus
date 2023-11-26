package com.mbrlabs.mundus.commons.rendering;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.ModelCacheable;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.CullableComponent;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableComponent;
import com.mbrlabs.mundus.commons.shadows.ShadowResolution;
import com.mbrlabs.mundus.commons.water.WaterResolution;

/**
 * @author JamesTKhan
 * @version October 03, 2023
 */
public class DefaultSceneRenderer implements SceneRenderer {
    public static final Vector3 clippingPlaneDisable = new Vector3(0.0f, 0f, 0.0f);
    private WaterRenderer waterRenderer;
    private Shader depthShader;

    public DefaultSceneRenderer() {
        waterRenderer = new WaterRenderer();
    }

    @Override
    public void render(Scene scene, float delta) {
        waterRenderer.renderWaterFBOs(scene);
        renderShadowMap(scene);
        renderScene(scene, delta);
    }

    /**
     * Renders the actual 3D scene. This is called by the render method normally, but if using post-processing
     * you may want to call this method directly.
     *
     * @param delta time since last frame
     */
    public void renderScene(Scene scene, float delta) {
        scene.modelCacheManager.update(delta);
        scene.batch.begin(scene.cam);
        renderObjects(scene);
        renderSkybox(scene);
        scene.batch.end();
    }

    protected void renderObjects(Scene scene) {
        scene.setClippingPlane(clippingPlaneDisable, 0);
        waterRenderer.renderWater(scene, scene.sceneGraph.getRoot());
        renderComponents(scene, scene.batch, scene.sceneGraph.getRoot());
        scene.modelCacheManager.triggerBeforeRenderEvent();
        scene.batch.render(scene.modelCacheManager.modelCache, scene.environment);
    }

    /**
     * Renders all renderable components (except Water) of the given parent game objects children
     * recursively using default shaders.
     *
     * @param batch  the model batch to use
     * @param parent the parent game object
     */
    public void renderComponents(Scene scene, ModelBatch batch, GameObject parent) {
        renderComponents(scene, batch, parent, null, false);
    }

    /**
     * Renders all renderable components (except Water) of the given parent game objects children
     * recursively.
     *
     * @param batch       the model batch to use
     * @param parent      the parent game object
     * @param shader      the shader to use
     * @param isDepthPass whether this is a depth render pass
     */
    public void renderComponents(Scene scene, ModelBatch batch, GameObject parent, Shader shader, boolean isDepthPass) {
        for (GameObject go : parent.getChildren()) {
            renderComponent(scene, batch, go, shader, isDepthPass);
        }
    }

    /**
     * Render models to the shadow map .This is called by the render method normally, but if using post-processing
     * you may want to call this method directly.
     */
    public void renderShadowMap(Scene scene) {
        if (scene.dirLight == null) {
            scene.setShadowQuality(ShadowResolution.DEFAULT_SHADOW_RESOLUTION);
        }

        if (!scene.dirLight.isCastsShadows()) {
            scene.environment.shadowMap = null;
            return;
        }

        scene.environment.shadowMap = scene.dirLight;

        scene.dirLight.setCenter(scene.cam.position);
        scene.dirLight.begin();
        scene.depthBatch.begin(scene.dirLight.getCamera());
        scene.setClippingPlane(clippingPlaneDisable, 0);
        renderComponents(scene, scene.depthBatch, scene.sceneGraph.getRoot(), null, true);
        scene.modelCacheManager.triggerBeforeDepthRenderEvent();
        scene.depthBatch.render(scene.modelCacheManager.modelCache, scene.environment);
        scene.depthBatch.end();
        scene.dirLight.end();
    }

    public void renderSkybox(Scene scene) {
        if (scene.skybox != null && scene.skybox.active) {
            scene.batch.render(scene.skybox.getSkyboxInstance(), scene.environment, scene.skybox.shader);
        }
    }

    @Override
    public void updateWaterResolution(WaterResolution waterResolution) {
        waterRenderer.updateWaterResolution(waterResolution);
    }

    protected void renderComponent(Scene scene, ModelBatch batch, GameObject go, Shader shader, boolean isDepthPass) {
        if (!go.active) return;
        if (go.hasWaterComponent) return;

        // Render all renderable components
        for (Component component : go.getComponents()) {
            if (!(component instanceof RenderableComponent)) continue;

            if (component instanceof CullableComponent) {
                CullableComponent cullableComponent = (CullableComponent) component;
                if (cullableComponent.isCulled()) continue;

                if (isDepthPass) {
                    cullableComponent.triggerBeforeDepthRenderEvent();
                } else {
                    cullableComponent.triggerBeforeRenderEvent();
                }
            }

            if (component instanceof ModelCacheable) {
                // Don't render the component here if it's a model cacheable
                ModelCacheable modelCacheable = (ModelCacheable) component;
                if (modelCacheable.shouldCache()) continue;
            }

            if (shader != null) {
                // Render the component with the given shader
                batch.render(((RenderableComponent) component).getRenderableProvider(), scene.environment, shader);
                continue;
            }

            // Render with default shaders (Uses Provider)
            batch.render(((RenderableComponent) component).getRenderableProvider(), scene.environment);
        }

        // Render children recursively
        if (go.getChildren() != null) {
            renderComponents(scene, batch, go, shader, isDepthPass);
        }
    }

    @Override
    public void setDepthShader(Shader depthShader) {
        this.depthShader = depthShader;
    }

    public Shader getDepthShader() {
        return depthShader;
    }
}
