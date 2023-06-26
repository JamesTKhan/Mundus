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
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.dto.TerrainComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.runtime.Shaders;

/**
 * The converter for terrain.
 */
public class TerrainComponentConverter {

    /**
     * Converts {@link TerrainComponentDTO} to {@link TerrainComponent}.
     */
    public static TerrainComponent convert(TerrainComponentDTO terrainComponentDTO, GameObject gameObject, AssetManager assetManager) {
        TerrainComponent tc = new TerrainComponent(gameObject);
        tc.setTerrainAsset((TerrainAsset) assetManager.findAssetByID(terrainComponentDTO.getTerrainID()));

        return tc;
    }
}
