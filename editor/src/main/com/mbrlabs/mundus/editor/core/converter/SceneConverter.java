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

package com.mbrlabs.mundus.editor.core.converter;

import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.GameObjectDTO;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.mapper.BaseLightConverter;
import com.mbrlabs.mundus.commons.mapper.FogConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.water.WaterResolution;
import com.mbrlabs.mundus.editor.core.EditorScene;

import java.util.Map;

/**
 * The converter for scene.
 */
public class SceneConverter {

    /**
     * Converts {@link Scene} to {@link SceneDTO}.
     */
    public static SceneDTO convert(Scene scene) {
        SceneDTO dto = new SceneDTO();

        // meta
        dto.setName(scene.getName());
        dto.setId(scene.getId());

        // scene graph
        for (GameObject go : scene.sceneGraph.getGameObjects()) {
            dto.getGameObjects().add(GameObjectConverter.convert(go));
        }

        // environment stuff
        dto.setFog(FogConverter.convert(scene.environment.getFog()));
        dto.setAmbientLight(BaseLightConverter.convert(scene.environment.getAmbientLight()));
        dto.setWaterResolution(scene.waterResolution);

        // camera
        dto.setCamPosX(scene.cam.position.x);
        dto.setCamPosY(scene.cam.position.y);
        dto.setCamPosZ(scene.cam.position.z);
        dto.setCamDirX(scene.cam.direction.x);
        dto.setCamDirY(scene.cam.direction.y);
        dto.setCamDirZ(scene.cam.direction.z);
        return dto;
    }

    /**
     * Converts {@link SceneDTO} to {@link Scene}.
     */
    public static EditorScene convert(SceneDTO dto, Map<String, Asset> assets) {
        EditorScene scene = new EditorScene();

        // meta
        scene.setId(dto.getId());
        scene.setName(dto.getName());

        // environment stuff
        scene.environment.setFog(FogConverter.convert(dto.getFog()));
        BaseLight ambientLight = BaseLightConverter.convert(dto.getAmbientLight());
        if (ambientLight != null) {
            scene.environment.setAmbientLight(ambientLight);
        }

        // Water stuff
        scene.waterResolution = dto.getWaterResolution();
        if (scene.waterResolution == null)
            scene.waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION;

        // scene graph
        scene.sceneGraph = new SceneGraph(scene);
        for (GameObjectDTO descriptor : dto.getGameObjects()) {
            scene.sceneGraph.addGameObject(GameObjectConverter.convert(descriptor, scene.sceneGraph, assets));
        }

        // camera
        scene.cam.position.x = dto.getCamPosX();
        scene.cam.position.y = dto.getCamPosY();
        scene.cam.position.z = dto.getCamPosZ();
        scene.cam.direction.set(dto.getCamDirX(), dto.getCamDirY(), dto.getCamDirZ());
        scene.cam.update();

        return scene;
    }

}
