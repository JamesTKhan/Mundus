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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.GameObjectDTO;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.commons.env.CameraSettings;
import com.mbrlabs.mundus.commons.mapper.BaseLightConverter;
import com.mbrlabs.mundus.commons.mapper.DirectionalLightConverter;
import com.mbrlabs.mundus.commons.mapper.FogConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import com.mbrlabs.mundus.commons.utils.LightUtils;
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
        dto.setSkyboxAssetId(scene.skyboxAssetId);

        // scene graph
        for (GameObject go : scene.sceneGraph.getGameObjects()) {
            dto.getGameObjects().add(GameObjectConverter.convert(go));
        }

        // environment stuff
        dto.setFog(FogConverter.convert(scene.environment));
        dto.setAmbientLight(BaseLightConverter.convert(scene.environment.getAmbientLight()));

        MundusDirectionalShadowLight directionalLightEx = LightUtils.getDirectionalLight(scene.environment);
        dto.setDirectionalLight(DirectionalLightConverter.convert(scene, directionalLightEx));

        // Water
        dto.setWaterResolution(scene.settings.waterResolution);
        dto.setWaterHeight(scene.settings.waterHeight);
        dto.setEnableWaterReflections(scene.settings.enableWaterReflections);
        dto.setEnableWaterRefractions(scene.settings.enableWaterRefractions);

        dto.setUseFrustumCulling(scene.settings.useFrustumCulling);

        // camera
        dto.setCamPosX(scene.cam.position.x);
        dto.setCamPosY(scene.cam.position.y);
        dto.setCamPosZ(scene.cam.position.z);
        dto.setCamDirX(scene.cam.direction.x);
        dto.setCamDirY(scene.cam.direction.y);
        dto.setCamDirZ(scene.cam.direction.z);
        dto.setCamNearPlane(scene.cam.near);
        dto.setCamFarPlane(scene.cam.far);
        if (scene.cam instanceof PerspectiveCamera) {
            dto.setCamFieldOfView(((PerspectiveCamera) scene.cam).fieldOfView);
        }
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
        scene.skyboxAssetId = dto.getSkyboxAssetId();

        // environment stuff
        FogConverter.convert(dto.getFog(), scene.environment);
        Color ambientLight = BaseLightConverter.convert(dto.getAmbientLight());
        if (ambientLight != null) {
            scene.environment.setAmbientLight(ambientLight);
        }

        MundusDirectionalShadowLight light = DirectionalLightConverter.convert(scene, dto.getDirectionalLight());
        if (light != null) {
            scene.setDirectionalLight(light);
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
            scene.sceneGraph.addGameObject(GameObjectConverter.convert(descriptor, scene.sceneGraph, assets));
        }

        // camera
        scene.cam.position.x = dto.getCamPosX();
        scene.cam.position.y = dto.getCamPosY();
        scene.cam.position.z = dto.getCamPosZ();
        scene.cam.direction.set(dto.getCamDirX(), dto.getCamDirY(), dto.getCamDirZ());
        scene.cam.near = dto.getCamNearPlane() > 0 ? dto.getCamNearPlane() : CameraSettings.DEFAULT_NEAR_PLANE;
        scene.cam.far = dto.getCamFarPlane() > 0 ? dto.getCamFarPlane() : CameraSettings.DEFAULT_FAR_PLANE;
        if (scene.cam instanceof PerspectiveCamera) {
            ((PerspectiveCamera) scene.cam).fieldOfView = dto.getCamFieldOfView() > 0 ? dto.getCamFieldOfView() : CameraSettings.DEFAULT_FOV;
        }
        scene.cam.update();

        return scene;
    }

}
