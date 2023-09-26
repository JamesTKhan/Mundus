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

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tibor Zsuro
 * @version 12-08-2021
 */
public class ModelComponentDTO implements AssetUsageDTO {

    private String modelID;
    private HashMap<String, String> materials; // g3db material id to material asset uuid
    private boolean useModelCache;

    public ModelComponentDTO() {
        materials = new HashMap<>();
    }

    public void setMaterials(HashMap<String, String> materials) {
        this.materials = materials;
    }

    public HashMap<String, String> getMaterials() {
        return materials;
    }

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelID) {
        this.modelID = modelID;
    }

    public boolean isUseModelCache() {
        return useModelCache;
    }

    public void setUseModelCache(boolean useModelCache) {
        this.useModelCache = useModelCache;
    }

    @Override
    public boolean usesAsset(Asset assetToCheck, Map<String, Asset> assetMap) {
        if (assetToCheck.getID().equals(modelID)) {
            return true;
        }

        if (assetToCheck instanceof MaterialAsset) {
            for (String matID : materials.keySet()) {
                if (materials.get(matID).equals(assetToCheck.getID())) {
                    return true;
                }
            }
        }

        if (assetToCheck instanceof TextureAsset) {
            for (String matID : materials.keySet()) {
                MaterialAsset mat = (MaterialAsset) assetMap.get(materials.get(matID));
                if (mat != null && mat.usesAsset(assetToCheck)) {
                    return true;
                }
            }
        }

        return false;
    }
}
