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
        FogConverter.convert(scene.environment);
        BaseLight ambientLight = BaseLightConverter.convert(dto.getAmbientLight());
        if (ambientLight != null) {
            scene.environment.setAmbientLight(ambientLight);
        }

        DirectionalLight light = DirectionalLightConverter.convert(dto.getDirectionalLight());
        if (light != null) {
            DirectionalLightsAttribute directionalLight = scene.environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
            directionalLight.lights.clear();
            directionalLight.lights.add(light);
        }

        // Water stuff
        scene.waterResolution = dto.getWaterResolution();
        if (scene.waterResolution == null)
            scene.waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION;

        scene.waterHeight = dto.getWaterHeight();

        scene.setShadowQuality(dto.getShadowResolution());

        // scene graph
        scene.sceneGraph = new SceneGraph(scene);
        for (GameObjectDTO descriptor : dto.getGameObjects()) {
            scene.sceneGraph.addGameObject(GameObjectConverter.convert(descriptor, scene.sceneGraph, shaders, assetManager));
        }

        return scene;
    }
}
