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

import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.dto.GameObjectDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.runtime.Shaders;

/**
 * Converter for game object.
 */
public class GameObjectConverter {

    /**
     * Converts {@link GameObjectDTO} to {@link GameObject}.
     */
    public static GameObject convert(GameObjectDTO dto, SceneGraph sceneGraph, Shaders shaders, AssetManager assetManager) {
        final GameObject go = new GameObject(sceneGraph, dto.getName(), dto.getId());
        go.active = dto.isActive();

        // transformation
        final float[] transform = dto.getTransform();
        go.translate(transform[0], transform[1], transform[2]);
        go.rotate(transform[3], transform[4], transform[5], transform[6]);
        go.scale(transform[7], transform[8], transform[9]);

        // convert tags
        if (dto.getTags() != null || !dto.getTags().isEmpty()) {
            for (String tag : dto.getTags()) {
                go.addTag(tag);
            }
        }

        // convert components
        if (dto.getModelComponent() != null) {
            go.getComponents().add(ModelComponentConverter.convert(dto.getModelComponent(), go, shaders.getModelShader(), assetManager));
        } else if (dto.getTerrainComponent() != null) {
            go.getComponents().add(TerrainComponentConverter.convert(dto.getTerrainComponent(), go, shaders.getTerrainShader(), assetManager));
        }

        // recursively convert children
        if (dto.getChilds() != null) {
            for (GameObjectDTO c : dto.getChilds()) {
                go.addChild(convert(c, sceneGraph, shaders, assetManager));
            }
        }

        return go;
    }
}
