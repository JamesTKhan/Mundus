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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.commons.assets.meta.Meta;

import java.util.Map;

public class TerrainObjectsAsset extends Asset {

    private final Json json;

    private final Array<TerrainObject> terrainObjects;

    /**
     * @param meta
     * @param assetFile
     */
    public TerrainObjectsAsset(Meta meta, FileHandle assetFile, Json json) {
        super(meta, assetFile);
        this.json = json;
        terrainObjects= new Array<>(5);
    }

    @Override
    public void load() {
        final Array<TerrainObject> terrainObjectsFromFile = json.fromJson(Array.class, TerrainObject.class, file);

        if (terrainObjectsFromFile != null) {
            terrainObjects.addAll(terrainObjectsFromFile);
        }
    }

    @Override
    public void load(AssetManager assetManager) {
        // No async loading for terrain objects
        load();
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        // Nothing to do here
    }

    @Override
    public void applyDependencies() {
        // Nothing to do here
    }

    @Override
    public void dispose() {
        // Nothing to do here
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        return false;
    }

    public void addTerrainObject(TerrainObject terrainObject) {
        terrainObjects.add(terrainObject);
    }

    public int getTerrainObjectNum() {
        return terrainObjects.size;
    }

    public TerrainObject getTerrainObject(int i) {
        return terrainObjects.get(i);
    }

}
