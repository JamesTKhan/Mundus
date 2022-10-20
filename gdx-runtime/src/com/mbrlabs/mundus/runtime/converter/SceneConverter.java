/*
 * Copyright (c) 2021. See AUTHORS file.
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

package com.mbrlabs.mundus.runtime.converter;

import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.dto.GameObjectDTO;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.commons.env.CameraSettings;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.mapper.BaseLightConverter;
import com.mbrlabs.mundus.commons.mapper.DirectionalLightConverter;
import com.mbrlabs.mundus.commons.mapper.FogConverter;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.water.WaterResolution;
import com.mbrlabs.mundus.runtime.Shaders;

/**
 * The converter for scene.
 */
public class SceneConverter {

    /**
     * Converts {@link SceneDTO} to {@link Scene}.
     */
    public static Scene convert(SceneDTO dto, Shaders shaders, AssetManager assetManager) {
        Scene scene = new Scene();

        // meta
        scene.setId(dto.getId());
        scene.setName(dto.getName());
        scene.skyboxAssetId = dto.getSkyboxAssetId();

        // environment stuff
        FogConverter.convert(dto.getFog(), scene.environment);
        BaseLight ambientLight = BaseLightConverter.convert(dto.getAmbientLight());
        if (ambientLight != null) {
            scene.environment.setAmbientLight(ambientLight);
        }

        DirectionalLight light = DirectionalLightConverter.convert(scene, dto.getDirectionalLight());
        if (light != null) {
            DirectionalLightsAttribute directionalLight = scene.environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
            directionalLight.lights.clear();
            directionalLight.lights.add(light);
        }

        // Water stuff
        scene.settings.waterResolution = dto.getWaterResolution();
        if (scene.settings.waterResolution == null)
            scene.settings.waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION;

        scene.settings.waterHeight = dto.getWaterHeight();
        scene.settings.useFrustumCulling = dto.isUseFrustumCulling();
        scene.settings.enableWaterReflections = dto.isEnableWaterReflections();
        scene.settings.enableWaterRefractions = dto.isEnableWaterRefractions();

        // scene graph
        scene.sceneGraph = new SceneGraph(scene);
        for (GameObjectDTO descriptor : dto.getGameObjects()) {
            scene.sceneGraph.addGameObject(GameObjectConverter.convert(descriptor, scene.sceneGraph, shaders, assetManager));
        }

        // Set cam settings
        scene.cam.near = dto.getCamNearPlane() > 0 ? dto.getCamNearPlane() : CameraSettings.DEFAULT_NEAR_PLANE;
        scene.cam.far = dto.getCamFarPlane() > 0 ? dto.getCamFarPlane() : CameraSettings.DEFAULT_FAR_PLANE;
        scene.cam.fieldOfView = dto.getCamFieldOfView() > 0 ? dto.getCamFieldOfView() : CameraSettings.DEFAULT_FOV;
        scene.cam.update();

        return scene;
    }
}
