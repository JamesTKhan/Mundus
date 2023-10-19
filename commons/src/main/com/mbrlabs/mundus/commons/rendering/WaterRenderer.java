package com.mbrlabs.mundus.commons.rendering;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;
import com.mbrlabs.mundus.commons.water.WaterResolution;

/**
 * @author JamesTKhan
 * @version October 03, 2023
 */
public class WaterRenderer {

    protected static final Vector3 clippingPlaneReflection = new Vector3(0.0f, 1f, 0.0f);
    protected static final Vector3 clippingPlaneRefraction = new Vector3(0.0f, -1f, 0.0f);

    protected FrameBuffer fboWaterReflection;
    protected FrameBuffer fboWaterRefraction;
    protected FrameBuffer fboDepthRefraction;

    private boolean isMRTRefraction = false;

    // FBO Depth Attachment index for MRT FBO
    private static final int DEPTH_ATTACHMENT = 1;

    private final Vector3 tmpCamUp = new Vector3();
    private final Vector3 tmpCamDir = new Vector3();
    private final Vector3 tmpCamPos = new Vector3();

    /**
     * Gets updated Reflection and Refraction textures for water, and captures depth for refraction if needed.
     */
    public void renderWaterFBOs(Scene scene) {
        if (fboWaterReflection == null) {
            Vector2 res = scene.settings.waterResolution.getResolutionValues();
            updateFBOS((int) res.x, (int) res.y);
        }

        if (scene.sceneGraph.isContainsWater()) {
            if (!isMRTRefraction) {
                captureDepth(scene);
            }
            captureReflectionFBO(scene);
            captureRefractionFBO(scene);
        }
    }

    /**
     * Renders all water components of the given parent game objects children recursively.
     *
     * @param parent the parent game object
     */
    public void renderWater(Scene scene, GameObject parent) {
        if (!scene.sceneGraph.isContainsWater()) return;
        for (GameObject go : parent.getChildren()) {
            if (!go.active) continue;

            for (Component component : go.getComponents()) {
                if (go.hasWaterComponent && component instanceof WaterComponent) {
                    WaterComponent waterComponent = (WaterComponent) component;

                    if (waterComponent.isCulled()) continue;
                    waterComponent.triggerBeforeRenderEvent();

                    waterComponent.getWaterAsset().setWaterReflectionTexture(getReflectionTexture(scene));
                    waterComponent.getWaterAsset().setWaterRefractionTexture(getRefractionTexture(scene));
                    waterComponent.getWaterAsset().setWaterRefractionDepthTexture(getRefractionDepthTexture());
                    scene.batch.render(waterComponent.getRenderableProvider(), scene.environment);
                }
            }

            if (go.getChildren() != null) {
                renderWater(scene, go);
            }
        }
    }

    protected void captureDepth(Scene scene) {
        // Render depth refractions to FBO
        fboDepthRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        scene.depthBatch.begin(scene.cam);
        scene.setClippingPlane(clippingPlaneRefraction, scene.settings.waterHeight + scene.settings.distortionEdgeCorrection);
        scene.getSceneRenderer().renderComponents(scene, scene.depthBatch, scene.sceneGraph.getRoot(), scene.getSceneRenderer().getDepthShader(), true);
        scene.depthBatch.render(scene.modelCacheManager.modelCache, scene.environment, scene.getSceneRenderer().getDepthShader());
        scene.depthBatch.end();
        fboDepthRefraction.end();
    }

    protected void captureRefractionFBO(Scene scene) {
        if (!scene.settings.enableWaterRefractions) return;
        // Render refractions to FBO
        fboWaterRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        scene.batch.begin(scene.cam);
        scene.setClippingPlane(clippingPlaneRefraction, scene.settings.waterHeight + scene.settings.distortionEdgeCorrection);
        scene.getSceneRenderer().renderComponents(scene, scene.batch, scene.sceneGraph.getRoot());
        scene.batch.render(scene.modelCacheManager.modelCache, scene.environment);
        scene.batch.end();
        fboWaterRefraction.end();
    }

    protected void captureReflectionFBO(Scene scene) {
        if (!scene.settings.enableWaterReflections) return;

        // Calc vertical distance for camera for reflection FBO
        float camReflectionDistance = 2 * (scene.cam.position.y - scene.settings.waterHeight);

        // Save current cam data
        tmpCamUp.set(scene.cam.up);
        tmpCamPos.set(scene.cam.position);
        tmpCamDir.set(scene.cam.direction);

        // Retains reflections on different camera orientations
        scene.cam.up.scl(-1, 1f, -1);
        // Invert the pitch of the scene.camera as it will be looking "up" from below current cam position
        scene.cam.direction.scl(1, -1, 1).nor();
        // Position the scene.camera below the water plane, looking "up"
        scene.cam.position.sub(0, camReflectionDistance, 0);
        scene.cam.update();

        // Render reflections to FBO
        fboWaterReflection.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        scene.batch.begin(scene.cam);
        scene.setClippingPlane(clippingPlaneReflection, -scene.settings.waterHeight + scene.settings.distortionEdgeCorrection);
        scene.getSceneRenderer().renderComponents(scene, scene.batch, scene.sceneGraph.getRoot());
        scene.batch.render(scene.modelCacheManager.modelCache, scene.environment);
        scene.getSceneRenderer().renderSkybox(scene);
        scene.batch.end();
        fboWaterReflection.end();

        // Restore camera data
        scene.cam.direction.set(tmpCamDir);
        scene.cam.position.set(tmpCamPos);
        scene.cam.up.set(tmpCamUp);
        scene.cam.update();
    }

    protected void updateFBOS(int width, int height) {
        if (fboWaterReflection != null) {
            fboWaterReflection.dispose();
        }
        if (fboWaterRefraction != null) {
            fboWaterRefraction.dispose();
        }
        if (fboDepthRefraction != null) {
            fboDepthRefraction.dispose();
        }

        fboWaterReflection = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);

        // Despite supporting MRT on WebGL2, the depth precision is far worse then doing a separate depth pass frustratingly.
        isMRTRefraction = Gdx.graphics.isGL30Available() && Gdx.app.getType() != Application.ApplicationType.WebGL;

        if (isMRTRefraction) {
            NestableFrameBuffer.NestableFrameBufferBuilder frameBufferBuilder = new NestableFrameBuffer.NestableFrameBufferBuilder(width, height);
            frameBufferBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGB888);
            frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT24, GL30.GL_UNSIGNED_INT);
            fboWaterRefraction = frameBufferBuilder.build();
        } else {
            fboWaterRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
            fboDepthRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
        }
    }

    private Texture getReflectionTexture(Scene scene) {
        return scene.settings.enableWaterReflections ? fboWaterReflection.getColorBufferTexture() : null;
    }

    private Texture getRefractionTexture(Scene scene) {
        return scene.settings.enableWaterRefractions ? fboWaterRefraction.getColorBufferTexture() : null;
    }

    private Texture getRefractionDepthTexture() {
        Texture refractionDepth;
        if (isMRTRefraction) {
            refractionDepth = fboWaterRefraction.getTextureAttachments().get(DEPTH_ATTACHMENT);
        } else {
            refractionDepth = fboDepthRefraction.getColorBufferTexture();
        }
        return refractionDepth;
    }

    public void updateWaterResolution(WaterResolution waterResolution) {
        Vector2 res = waterResolution.getResolutionValues();
        updateFBOS((int) res.x, (int) res.y);
    }
}
