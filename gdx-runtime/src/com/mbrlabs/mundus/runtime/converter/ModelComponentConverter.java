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
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.dto.ModelComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.runtime.Shaders;

import java.util.Map;

/**
 * The converter for model component.
 */
public class ModelComponentConverter {

    /**
     * Converts {@link ModelComponentDTO} to {@link ModelComponent}.
     */
    public static ModelComponent convert(ModelComponentDTO modelComponentDTO, GameObject gameObject, AssetManager assetManager) {
        ModelComponent mc = new ModelComponent(gameObject);
        mc.setModel((ModelAsset) assetManager.findAssetByID(modelComponentDTO.getModelID()), false);
        mc.setUseModelCache(modelComponentDTO.isUseModelCache());

        for(Map.Entry<String, String> entry : modelComponentDTO.getMaterials().entrySet()) {
            mc.getMaterials().put(entry.getKey(), (MaterialAsset) assetManager.findAssetByID(entry.getValue()));
        }
        mc.applyMaterials();

        return mc;
    }
}
