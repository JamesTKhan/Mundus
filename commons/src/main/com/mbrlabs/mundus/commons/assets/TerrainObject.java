/*
 * Copyright (c) 2023. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.math.Vector3;

public class TerrainObject {

    private String id;

    private int layerPos;

    private Vector3 position;

    private Vector3 rotation;

    private Vector3 scale;

    public TerrainObject() {
        // NOOP
    }

    /**
     * Copy constructor.
     *
     * @param original The original terrain object.
     */
    public TerrainObject(final TerrainObject original) {
        id = original.getId();
        layerPos = original.getLayerPos();
        position = new Vector3(original.getPosition());
        rotation = new Vector3(original.getRotation());
        scale = new Vector3(original.getScale());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLayerPos() {
        return layerPos;
    }

    public void setLayerPos(int layerPos) {
        this.layerPos = layerPos;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }
}
