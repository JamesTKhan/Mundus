/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.SkyboxAsset;
import com.mbrlabs.mundus.commons.env.CameraSettings;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.ModelCacheManager;
import com.mbrlabs.mundus.commons.scene3d.ModelCacheable;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.CullableComponent;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableComponent;
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent;
import com.mbrlabs.mundus.commons.shaders.DepthShader;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import com.mbrlabs.mundus.commons.shadows.ShadowResolution;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.commons.utils.LightUtils;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;
import com.mbrlabs.mundus.commons.water.WaterResolution;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable {
    public static boolean isRuntime = true;

    // FBO Depth Attachment index for MRT FBO
    private static final int DEPTH_ATTACHMENT = 1;

    private MundusDirectionalShadowLight dirLight;
    private String name;
    private long id;

    public SceneGraph sceneGraph;
    public SceneSettings settings;
    public MundusEnvironment environment;
    public Skybox skybox;
    public String skyboxAssetId;

    public Camera cam;
    public ModelBatch batch;
    public ModelBatch depthBatch;
    public ModelCacheManager modelCacheManager;

    protected FrameBuffer fboWaterReflection;
    protected FrameBuffer fboWaterRefraction;
    protected FrameBuffer fboDepthRefraction;
    private boolean isMRTRefraction = false;

    private DepthShader depthShader;

    protected Vector3 clippingPlaneDisable = new Vector3(0.0f, 0f, 0.0f);
    protected Vector3 clippingPlaneReflection = new Vector3(0.0f, 1f, 0.0f);
    protected Vector3 clippingPlaneRefraction = new Vector3(0.0f, -1f, 0.0f);

    private final Vector3 tmpCamUp = new Vector3();
    private final Vector3 tmpCamDir = new Vector3();
    private final Vector3 tmpCamPos = new Vector3();

    public Scene() {
        environment = new MundusEnvironment();
        settings = new SceneSettings();
        modelCacheManager = new ModelCacheManager(this);

        cam = new PerspectiveCamera(CameraSettings.DEFAULT_FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0, 1, -1);
        cam.near = CameraSettings.DEFAULT_NEAR_PLANE;
        cam.far = CameraSettings.DEFAULT_FAR_PLANE;

        dirLight = new MundusDirectionalShadowLight();
        dirLight.color.set(LightUtils.DEFAULT_COLOR);
        dirLight.intensity = LightUtils.DEFAULT_INTENSITY;
        dirLight.direction.set(LightUtils.DEFAULT_DIRECTION);
        dirLight.direction.nor();
        environment.add(dirLight);
        environment.set(ColorAttribute.createAmbientLight(Color.WHITE));

        initPBR();
        setShadowQuality(ShadowResolution.DEFAULT_SHADOW_RESOLUTION);

        sceneGraph = new SceneGraph(this);
    }

    public void initPBR() {
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(dirLight);
        Cubemap diffuseCubemap = iblBuilder.buildIrradianceMap(512);
        Cubemap specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        PBRTextureAttribute tex = (PBRTextureAttribute) environment.get(PBRTextureAttribute.BRDFLUTTexture);
        if (tex == null) {
            Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
            environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        }

        PBRCubemapAttribute specularEnv = (PBRCubemapAttribute) environment.get(PBRCubemapAttribute.SpecularEnv);
        if (specularEnv != null) {
            specularEnv.textureDescription.texture.dispose();
            specularEnv.textureDescription.texture = specularCubemap;
        } else {
            environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        }

        PBRCubemapAttribute diffuseEnv = (PBRCubemapAttribute) environment.get(PBRCubemapAttribute.DiffuseEnv);
        if (diffuseEnv != null) {
            diffuseEnv.textureDescription.texture.dispose();
            diffuseEnv.textureDescription.texture = diffuseCubemap;
        } else {
            environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        }
    }

    /**
     * This is the primary render method. It handles rendering everything. This should be used
     * unless you need more control over the rendering process.
     */
    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    /**
     * This is the primary render method. It handles rendering everything. This should be used
     * unless you need more control over the rendering process.
     * @param delta time since last frame
     */
    public void render(float delta) {
        renderWaterFBOs();
        renderShadowMap();
        renderScene(delta);
    }

    /**
     * Gets updated Reflection and Refraction textures for water, and captures depth for refraction if needed.
     */
    public void renderWaterFBOs() {
        if (fboWaterReflection == null) {
            Vector2 res = settings.waterResolution.getResolutionValues();
            initFrameBuffers((int) res.x, (int) res.y);
        }

        if (sceneGraph.isContainsWater()) {
            if (!isMRTRefraction) {
                captureDepth();
            }
            captureReflectionFBO();
            captureRefractionFBO();
        }
    }

    /**
     * Renders the actual 3D scene. This is called by the render method normally, but if using post-processing
     * you may want to call this method directly.
     * @param delta time since last frame
     */
    public void renderScene(float delta) {
        modelCacheManager.update(delta);
        batch.begin(cam);
        renderObjects();
        renderSkybox();
        batch.end();
    }

    protected void renderObjects() {
        setClippingPlane(clippingPlaneDisable, 0);
        renderWater(sceneGraph.getRoot());
        renderComponents(batch, sceneGraph.getRoot());
        modelCacheManager.triggerBeforeRenderEvent();
        batch.render(modelCacheManager.modelCache, environment);
    }

    private void setClippingPlane(Vector3 plane, float clipHeight) {
        environment.setClippingHeight(clipHeight);
        environment.getClippingPlane().set(plane);
    }

    /**
     * Renders all renderable components (except Water) of the given parent game objects children
     * recursively using default shaders.
     *
     * @param batch the model batch to use
     * @param parent the parent game object
     */
    protected void renderComponents(ModelBatch batch, GameObject parent) {
        renderComponents(batch, parent, null, false);
    }

    /**
     * Renders all renderable components (except Water) of the given parent game objects children
     * recursively.
     *
     * @param batch the model batch to use
     * @param parent the parent game object
     * @param shader the shader to use
     * @param isDepthPass whether this is a depth render pass
     */
    protected void renderComponents(ModelBatch batch, GameObject parent, Shader shader, boolean isDepthPass) {
        for (GameObject go : parent.getChildren()) {
            if (!go.active) continue;
            if (go.hasWaterComponent) continue;

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
                    batch.render(((RenderableComponent) component).getRenderableProvider(), environment, shader);
                    continue;
                }

                // Render with default shaders (Uses Provider)
                batch.render(((RenderableComponent) component).getRenderableProvider(), environment);
            }

            // Render children recursively
            if (go.getChildren() != null) {
                renderComponents(batch, go, shader, isDepthPass);
            }
        }
    }

    /**
     * Renders all water components of the given parent game objects children recursively.
     * @param parent the parent game object
     */
    protected void renderWater(GameObject parent) {
        if (!sceneGraph.isContainsWater()) return;
        for (GameObject go : parent.getChildren()) {
            if (!go.active) continue;

            for (Component component : go.getComponents()) {
                if (go.hasWaterComponent && component instanceof WaterComponent) {
                    WaterComponent waterComponent = (WaterComponent) component;

                    if (waterComponent.isCulled()) continue;
                    waterComponent.triggerBeforeRenderEvent();

                    waterComponent.getWaterAsset().setWaterReflectionTexture(getReflectionTexture());
                    waterComponent.getWaterAsset().setWaterRefractionTexture(getRefractionTexture());
                    waterComponent.getWaterAsset().setWaterRefractionDepthTexture(getRefractionDepthTexture());
                    batch.render(waterComponent.getRenderableProvider(), environment);
                }
            }

            if (go.getChildren() != null) {
                renderWater(go);
            }
        }
    }

    /**
     * Render models to the shadow map .This is called by the render method normally, but if using post-processing
     * you may want to call this method directly.
     */
    public void renderShadowMap() {
        if (dirLight == null) {
            setShadowQuality(ShadowResolution.DEFAULT_SHADOW_RESOLUTION);
        }

        if (!dirLight.isCastsShadows()) {
            environment.shadowMap = null;
            return;
        }

        environment.shadowMap = dirLight;

        dirLight.setCenter(cam.position);
        dirLight.begin();
        depthBatch.begin(dirLight.getCamera());
        setClippingPlane(clippingPlaneDisable, 0);
        renderComponents(depthBatch, sceneGraph.getRoot(), null, true);
        modelCacheManager.triggerBeforeDepthRenderEvent();
        depthBatch.render(modelCacheManager.modelCache, environment);
        depthBatch.end();
        dirLight.end();
    }

    protected void initFrameBuffers(int width, int height) {
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

    protected void captureReflectionFBO() {
        if (!settings.enableWaterReflections) return;

        // Calc vertical distance for camera for reflection FBO
        float camReflectionDistance = 2 * (cam.position.y - settings.waterHeight);

        // Save current cam data
        tmpCamUp.set(cam.up);
        tmpCamPos.set(cam.position);
        tmpCamDir.set(cam.direction);

        // Retains reflections on different camera orientations
        cam.up.scl(-1, 1f, -1);
        // Invert the pitch of the camera as it will be looking "up" from below current cam position
        cam.direction.scl(1, -1, 1).nor();
        // Position the camera below the water plane, looking "up"
        cam.position.sub(0, camReflectionDistance, 0);
        cam.update();

        // Render reflections to FBO
        fboWaterReflection.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(cam);
        setClippingPlane(clippingPlaneReflection, -settings.waterHeight + settings.distortionEdgeCorrection);
        renderComponents(batch, sceneGraph.getRoot());
        batch.render(modelCacheManager.modelCache, environment);
        renderSkybox();
        batch.end();
        fboWaterReflection.end();

        // Restore camera data
        cam.direction.set(tmpCamDir);
        cam.position.set(tmpCamPos);
        cam.up.set(tmpCamUp);
        cam.update();
    }

    protected void captureDepth() {
        // Render depth refractions to FBO
        fboDepthRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        depthBatch.begin(cam);
        setClippingPlane(clippingPlaneRefraction, settings.waterHeight + settings.distortionEdgeCorrection);
        renderComponents(depthBatch, sceneGraph.getRoot(), depthShader, true);
        depthBatch.render(modelCacheManager.modelCache, environment, depthShader);
        depthBatch.end();
        fboDepthRefraction.end();
    }

    protected void renderSkybox() {
        if (skybox != null && skybox.active) {
            batch.render(skybox.getSkyboxInstance(), environment, skybox.shader);
        }
    }

    protected void captureRefractionFBO() {
        if (!settings.enableWaterRefractions) return;
        // Render refractions to FBO
        fboWaterRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(cam);
        setClippingPlane(clippingPlaneRefraction, settings.waterHeight + settings.distortionEdgeCorrection);
        renderComponents(batch, sceneGraph.getRoot());
        batch.render(modelCacheManager.modelCache, environment);
        batch.end();
        fboWaterRefraction.end();
    }

    public String getName() {
        return name;
    }

    public void setDirectionalLight(MundusDirectionalShadowLight light) {
        this.dirLight = light;
        environment.remove(DirectionalLightsAttribute.Type);
        environment.add(light);
        initPBR();
    }

    public MundusDirectionalShadowLight getDirectionalLight() {
        return dirLight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDepthShader(DepthShader depthShader) {
        this.depthShader = depthShader;
    }

    private Texture getReflectionTexture() {
        return settings.enableWaterReflections ? fboWaterReflection.getColorBufferTexture() : null;
    }

    private Texture getRefractionTexture() {
        return settings.enableWaterRefractions ? fboWaterRefraction.getColorBufferTexture() : null;
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

    /**
     * Set the water resolution to use for water reflection and refractions.
     * This will reinitialize the frame buffers with the given resolution.
     * @param resolution the resolution to use
     */
    public void setWaterResolution(WaterResolution resolution) {
        settings.waterResolution = resolution;
        Vector2 res = settings.waterResolution.getResolutionValues();
        initFrameBuffers((int) res.x, (int) res.y);
    }

    /**
     * Set shadow quality for scenes DirectionalLight.
     *
     * @param shadowResolution the shadow resolution to use.
     */
    public void setShadowQuality(ShadowResolution shadowResolution) {
        MundusDirectionalShadowLight light = LightUtils.getDirectionalLight(environment);
        if (light == null || shadowResolution == null) return;

        light.setShadowResolution(shadowResolution);

        environment.shadowMap = light;
    }

    /**
     * Sets and switches the scenes skybox to the given SkyboxAsset.
     *
     * @param skyboxAsset the asset to use
     * @param skyboxShader the shader to use
     */
    public void setSkybox(SkyboxAsset skyboxAsset, Shader skyboxShader) {
        if (skyboxAsset == null) return;

        skyboxAssetId = skyboxAsset.getID();
        skybox =  new Skybox(skyboxAsset.positiveX.getFile(),
                skyboxAsset.negativeX.getFile(),
                skyboxAsset.positiveY.getFile(),
                skyboxAsset.negativeY.getFile(),
                skyboxAsset.positiveZ.getFile(),
                skyboxAsset.negativeZ.getFile(),
                skyboxShader);

        skybox.setRotateSpeed(skyboxAsset.rotateSpeed);
        skybox.setRotateEnabled(skyboxAsset.rotateEnabled);

    }

    @Override
    public void dispose() {
        if (skybox != null) {
            skybox.dispose();
        }
        modelCacheManager.dispose();
    }
}
