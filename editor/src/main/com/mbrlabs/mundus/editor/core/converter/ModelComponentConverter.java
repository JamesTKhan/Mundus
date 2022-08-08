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

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.dto.ModelComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.scene3d.components.PickableModelComponent;
import com.mbrlabs.mundus.editor.utils.Log;

import java.util.Map;

/**
 * Converter for model component.
 */
public class ModelComponentConverter {

    private final static String TAG = ModelComponentConverter.class.getSimpleName();

    /**
     * Converts {@link ModelComponentDTO} to {@link PickableModelComponent}.
     */
    public static PickableModelComponent convert(ModelComponentDTO dto, GameObject go,
                                                 Map<String, Asset> assets) {
        ModelAsset model = (ModelAsset) assets.get(dto.getModelID());

        if (model == null) {
            Log.fatal(TAG, "MModel for MModelInstance not found: {}", dto.getModelID());
            return null;
        }

        PickableModelComponent component = new PickableModelComponent(go);
        component.setModel(model, false);
        component.setUseModelCache(dto.isUseModelCache());

        for (String g3dbMatID : dto.getMaterials().keySet()) {
            String uuid = dto.getMaterials().get(g3dbMatID);
            MaterialAsset matAsset = (MaterialAsset) assets.get(uuid);
            component.getMaterials().put(g3dbMatID, matAsset);
        }

        return component;
    }

    /**
     * Converts {@link PickableModelComponent} to {@link ModelComponentDTO}.
     */
    public static ModelComponentDTO convert(PickableModelComponent modelComponent) {
        ModelComponentDTO dto = new ModelComponentDTO();
        dto.setModelID(modelComponent.getModelAsset().getID());
        dto.setUseModelCache(modelComponent.shouldCache());

        // materials
        for (String g3dbMatID : modelComponent.getMaterials().keys()) {
            dto.getMaterials().put(g3dbMatID, modelComponent.getMaterials().get(g3dbMatID).getID());
        }

        return dto;
    }
}
