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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.SkyboxAsset;
import com.mbrlabs.mundus.commons.env.CameraSettings;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.rendering.DefaultSceneRenderer;
import com.mbrlabs.mundus.commons.rendering.SceneRenderer;
import com.mbrlabs.mundus.commons.scene3d.ModelCacheManager;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.shaders.DepthShader;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import com.mbrlabs.mundus.commons.shadows.ShadowResolution;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.commons.utils.LightUtils;
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

    public MundusDirectionalShadowLight dirLight;
    private String name;
    private long id;

    private SceneRenderer sceneRenderer;

    public SceneGraph sceneGraph;
    public SceneSettings settings;
    public MundusEnvironment environment;
    public Skybox skybox;
    public String skyboxAssetId;

    public Camera cam;
    public ModelBatch batch;
    public ModelBatch depthBatch;
    public ModelCacheManager modelCacheManager;

    public Scene() {
        environment = new MundusEnvironment();
        settings = new SceneSettings();
        modelCacheManager = new ModelCacheManager(this);
        sceneRenderer = new DefaultSceneRenderer();

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
        sceneRenderer.render(this, delta);
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

    public SceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    public void setSceneRenderer(SceneRenderer sceneRenderer) {
        this.sceneRenderer = sceneRenderer;
    }

    public void setDepthShader(DepthShader depthShader) {
        sceneRenderer.setDepthShader(depthShader);
    }

    /**
     * Set the water resolution to use for water reflection and refractions.
     * This will reinitialize the frame buffers with the given resolution.
     * @param resolution the resolution to use
     */
    public void setWaterResolution(WaterResolution resolution) {
        settings.waterResolution = resolution;
        sceneRenderer.updateWaterResolution(resolution);
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

    public void setClippingPlane(Vector3 plane, float clipHeight) {
        environment.setClippingHeight(clipHeight);
        environment.getClippingPlane().set(plane);
    }

    @Override
    public void dispose() {
        sceneGraph.dispose();

        if (skybox != null) {
            skybox.dispose();
        }
        modelCacheManager.dispose();
    }
}
