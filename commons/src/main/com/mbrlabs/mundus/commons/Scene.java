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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.SkyboxAsset;
import com.mbrlabs.mundus.commons.env.CameraSettings;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.shaders.DepthShader;
import com.mbrlabs.mundus.commons.shaders.ShadowMapShader;
import com.mbrlabs.mundus.commons.shadows.ShadowMapper;
import com.mbrlabs.mundus.commons.shadows.ShadowResolution;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.commons.utils.LightUtils;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;
import com.mbrlabs.mundus.commons.water.WaterResolution;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable {

    private String name;
    private long id;

    public SceneGraph sceneGraph;
    public SceneSettings settings;
    public MundusEnvironment environment;
    public Skybox skybox;
    public String skyboxAssetId;

    public PerspectiveCamera cam;
    public ModelBatch batch;
    public ModelBatch depthBatch;

    private FrameBuffer fboWaterReflection;
    private FrameBuffer fboWaterRefraction;
    private FrameBuffer fboDepthRefraction;

    private DepthShader depthShader;
    private ShadowMapShader shadowMapShader;
    private ShadowMapper shadowMapper = null;

    protected Vector3 clippingPlaneDisable = new Vector3(0.0f, 0f, 0.0f);
    protected Vector3 clippingPlaneReflection = new Vector3(0.0f, 1f, 0.0f);
    protected Vector3 clippingPlaneRefraction = new Vector3(0.0f, -1f, 0.0f);

    public Scene() {
        environment = new MundusEnvironment();
        settings = new SceneSettings();

        cam = new PerspectiveCamera(CameraSettings.DEFAULT_FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0, 1, -1);
        cam.near = CameraSettings.DEFAULT_NEAR_PLANE;
        cam.far = CameraSettings.DEFAULT_FAR_PLANE;

        DirectionalLight dirLight = new DirectionalLight();
        dirLight.color.set(DirectionalLight.DEFAULT_COLOR);
        dirLight.intensity = DirectionalLight.DEFAULT_INTENSITY;
        dirLight.direction.set(DirectionalLight.DEFAULT_DIRECTION);
        dirLight.direction.nor();
        environment.add(dirLight);
        environment.getAmbientLight().intensity = 0.8f;
        environment.set(ColorAttribute.createAmbientLight(Color.WHITE));

        initPBR();

        setShadowQuality(ShadowResolution.DEFAULT_SHADOW_RESOLUTION);

        sceneGraph = new SceneGraph(this);
    }

    private void initPBR() {
        DirectionalLightEx directionalLightEx = new DirectionalLightEx();
        directionalLightEx.intensity = DirectionalLight.DEFAULT_INTENSITY;
        directionalLightEx.setColor(DirectionalLight.DEFAULT_COLOR);
        directionalLightEx.direction.set(DirectionalLight.DEFAULT_DIRECTION);

        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(directionalLightEx);
        Cubemap diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        Cubemap specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .3f, .3f, .3f, 1));
        environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
    }

    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        if (fboWaterReflection == null) {
            Vector2 res = settings.waterResolution.getResolutionValues();
            initFrameBuffers((int) res.x, (int) res.y);
        }

        if (sceneGraph.isContainsWater()) {
            captureDepth(delta);
            captureReflectionFBO(delta);
            captureRefractionFBO(delta);
        }

        renderSkybox();
        renderShadowMap(delta);
        renderWater(delta);
        renderObjects(delta);
    }

    private void renderObjects(float delta) {
        // Render objects
        batch.begin(cam);
        sceneGraph.render(delta, clippingPlaneDisable, 0);
        batch.end();
    }

    private void renderWater(float delta) {
        if (sceneGraph.isContainsWater()) {
            Texture refraction = fboWaterRefraction.getColorBufferTexture();
            Texture reflection = fboWaterReflection.getColorBufferTexture();
            Texture refractionDepth = fboDepthRefraction.getColorBufferTexture();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // Render Water
            batch.begin(cam);
            sceneGraph.renderWater(delta, reflection, refraction, refractionDepth);
            batch.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void renderShadowMap(float delta) {
        if (shadowMapper == null) {
            setShadowQuality(ShadowResolution.DEFAULT_SHADOW_RESOLUTION);
        }

        DirectionalLight light = LightUtils.getDirectionalLight(environment);
        if (light == null || !light.castsShadows) return;

        shadowMapper.setCenter(cam.position);
        shadowMapper.begin(light.direction);
        depthBatch.begin(shadowMapper.getCam());
        sceneGraph.renderDepth(delta, clippingPlaneDisable, 0, shadowMapShader);
        depthBatch.end();
        shadowMapper.end();
    }

    private void initFrameBuffers(int width, int height) {
        fboWaterReflection = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
        fboWaterRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
        fboDepthRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
    }

    private void captureReflectionFBO(float delta) {
        // Calc vertical distance for camera for reflection FBO
        float camReflectionDistance = 2 * (cam.position.y - settings.waterHeight);

        // Save current cam positions
        Vector3 camPos = cam.position.cpy();
        Vector3 camDir = cam.direction.cpy();

        // Position camera for reflection capture
        cam.direction.scl(1, -1, 1).nor();
        cam.position.sub(0, camReflectionDistance, 0);
        cam.update();

        // Render reflections to FBO
        fboWaterReflection.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderSkybox();
        batch.begin(cam);
        sceneGraph.render(delta, clippingPlaneReflection, -settings.waterHeight + settings.distortionEdgeCorrection);
        batch.end();
        fboWaterReflection.end();

        // Restore camera positions
        cam.direction.set(camDir);
        cam.position.set(camPos);
        cam.update();
    }

    private void captureDepth(float delta) {
        // Render depth refractions to FBO
        fboDepthRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        depthBatch.begin(cam);
        sceneGraph.renderDepth(delta, clippingPlaneRefraction, settings.waterHeight + settings.distortionEdgeCorrection, depthShader);
        depthBatch.end();
        fboDepthRefraction.end();
    }

    private void renderSkybox() {
        if (skybox != null) {
            batch.begin(cam);
            batch.render(skybox.getSkyboxInstance(), environment, skybox.shader);
            batch.end();
        }
    }

    private void captureRefractionFBO(float delta) {
        // Render refractions to FBO
        fboWaterRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderSkybox();
        batch.begin(cam);
        sceneGraph.render(delta, clippingPlaneRefraction, settings.waterHeight + settings.distortionEdgeCorrection);
        batch.end();
        fboWaterRefraction.end();
    }

    public String getName() {
        return name;
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

    public ShadowMapper getShadowMapper() {
        return shadowMapper;
    }

    public void setShadowMapper(ShadowMapper shadowMapperToSet) {
        if (shadowMapper != null) {
            shadowMapper.dispose();
        }

        this.shadowMapper = shadowMapperToSet;
        environment.shadowMap = shadowMapperToSet;
    }

    public void setDepthShader(DepthShader depthShader) {
        this.depthShader = depthShader;
    }

    public void setShadowMapShader(ShadowMapShader shadowMapShader) {
        this.shadowMapShader = shadowMapShader;
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
        DirectionalLight light = LightUtils.getDirectionalLight(environment);
        if (light == null || shadowResolution == null) return;

        if (shadowMapper == null) {
            shadowMapper = new ShadowMapper(shadowResolution);
        } else {
            shadowMapper.setShadowResolution(shadowResolution);
        }

        environment.shadowMap = shadowMapper;
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
    }
}
