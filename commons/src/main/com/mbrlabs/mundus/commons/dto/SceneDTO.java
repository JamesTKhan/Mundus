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

package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mbrlabs.mundus.commons.shadows.ShadowResolution;
import com.mbrlabs.mundus.commons.water.WaterResolution;

/**
 * @author Tibor Zsuro
 * @version 12-08-2021
 */
public class SceneDTO implements Json.Serializable {

    private transient long id;
    private String name;
    private String skyboxAssetId;
    private Array<GameObjectDTO> gameObjects;
    private FogDTO fog;
    private BaseLightDTO ambientLight;
    private DirectionalLightDTO directionalLight;
    private float camPosX;
    private float camPosY;
    private float camPosZ;
    private float camDirX = 0;
    private float camDirY = 0;
    private float camDirZ = 0;
    private float camNearPlane;
    private float camFarPlane;
    private float camFieldOfView;
    private float waterHeight;
    private boolean useFrustumCulling;
    private boolean enableWaterReflections = true;
    private boolean enableWaterRefractions = true;
    private WaterResolution waterResolution;

    public SceneDTO() {
        gameObjects = new Array<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCamPosX() {
        return camPosX;
    }

    public void setCamPosX(float camPosX) {
        this.camPosX = camPosX;
    }

    public float getCamPosY() {
        return camPosY;
    }

    public void setCamPosY(float camPosY) {
        this.camPosY = camPosY;
    }

    public float getCamPosZ() {
        return camPosZ;
    }

    public void setCamPosZ(float camPosZ) {
        this.camPosZ = camPosZ;
    }

    public float getCamDirX() {
        return camDirX;
    }

    public void setCamDirX(float camDirX) {
        this.camDirX = camDirX;
    }

    public float getCamDirY() {
        return camDirY;
    }

    public void setCamDirY(float camDirY) {
        this.camDirY = camDirY;
    }

    public float getCamDirZ() {
        return camDirZ;
    }

    public void setCamDirZ(float camDirZ) {
        this.camDirZ = camDirZ;
    }

    public float getCamNearPlane() {
        return camNearPlane;
    }

    public void setCamNearPlane(float camNearPlane) {
        this.camNearPlane = camNearPlane;
    }

    public float getCamFarPlane() {
        return camFarPlane;
    }

    public void setCamFarPlane(float camFarPlane) {
        this.camFarPlane = camFarPlane;
    }

    public float getCamFieldOfView() {
        return camFieldOfView;
    }

    public void setCamFieldOfView(float camFieldOfView) {
        this.camFieldOfView = camFieldOfView;
    }

    public FogDTO getFog() {
        return fog;
    }

    public void setFog(FogDTO fog) {
        this.fog = fog;
    }

    public BaseLightDTO getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(BaseLightDTO ambientLight) {
        this.ambientLight = ambientLight;
    }

    public DirectionalLightDTO getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLightDTO directionalLight) {
        this.directionalLight = directionalLight;
    }

    public Array<GameObjectDTO> getGameObjects() {
        return gameObjects;
    }

    public void setGameObjects(Array<GameObjectDTO> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public void setWaterResolution(WaterResolution waterResolution) {
        this.waterResolution = waterResolution;
    }

    public WaterResolution getWaterResolution() {
        return waterResolution;
    }

    public void setWaterHeight(float waterHeight) {
        this.waterHeight = waterHeight;
    }

    public float getWaterHeight() {
        return waterHeight;
    }

    public boolean isUseFrustumCulling() {
        return useFrustumCulling;
    }

    public void setUseFrustumCulling(boolean useFrustumCulling) {
        this.useFrustumCulling = useFrustumCulling;
    }

    public void setSkyboxAssetId(String skyboxAssetId) {
        this.skyboxAssetId = skyboxAssetId;
    }

    public String getSkyboxAssetId() {
        return skyboxAssetId;
    }

    public boolean isEnableWaterReflections() {
        return enableWaterReflections;
    }

    public void setEnableWaterReflections(boolean enableWaterReflections) {
        this.enableWaterReflections = enableWaterReflections;
    }

    public boolean isEnableWaterRefractions() {
        return enableWaterRefractions;
    }

    public void setEnableWaterRefractions(boolean enableWaterRefractions) {
        this.enableWaterRefractions = enableWaterRefractions;
    }

    @Override
    public void write(Json json) {
        // ID is written separately due to GWT technical limitations on Long emulation and reflection
        json.writeValue("id", id);
        json.writeFields(this);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.setIgnoreUnknownFields(true);
        // Default scenes may not have an ID, so we check for it first
        if (jsonData.has("id")) {
            // ID is read in separately due to GWT technical limitations on Long emulation and reflection
            id = Long.parseLong(jsonData.getString("id"));
        }
        json.readFields(this, jsonData);
        json.setIgnoreUnknownFields(false);
    }
}
