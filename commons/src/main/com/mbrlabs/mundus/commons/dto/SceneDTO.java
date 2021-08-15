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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tibor Zsuro
 * @version 12-08-2021
 */
public class SceneDTO {

    private long id;
    private String name;
    private List<GameObjectDTO> gameObjects;
    private FogDTO fog;
    private BaseLightDTO ambientLight;
    private float camPosX;
    private float camPosY;
    private float camPosZ;
    private float camDirX = 0;
    private float camDirY = 0;
    private float camDirZ = 0;

    public SceneDTO() {
        gameObjects = new ArrayList<>();
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

    public List<GameObjectDTO> getGameObjects() {
        return gameObjects;
    }

    public void setGameObjects(List<GameObjectDTO> gameObjects) {
        this.gameObjects = gameObjects;
    }
}
