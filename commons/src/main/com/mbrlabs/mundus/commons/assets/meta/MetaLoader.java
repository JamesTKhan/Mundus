/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.assets.meta;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.water.Water;

/**
 *
 * @author Marcus Brummer
 * @version 26-10-2016
 */
public class MetaLoader {

    private final JsonReader reader = new JsonReader();

    public Meta load(FileHandle file) throws MetaFileParseException {
        Meta meta = new Meta(file);

        JsonValue json = reader.parse(file);
        parseBasics(meta, json);

        if(meta.getType() == AssetType.TERRAIN) {
            parseTerrain(meta, json.get(Meta.JSON_TERRAIN));
        } else if(meta.getType() == AssetType.MODEL) {
            parseModel(meta, json.get(Meta.JSON_MODEL));
        } else if(meta.getType() == AssetType.WATER) {
            parseWater(meta, json.get(Meta.JSON_WATER));
        } else if(meta.getType() == AssetType.SKYBOX) {
            parseSkybox(meta, json.get(Meta.JSON_SKY_BOX));
        }

        return meta;
    }

    private void parseBasics(Meta meta, JsonValue jsonRoot) {
        meta.setVersion(jsonRoot.getInt(Meta.JSON_VERSION));
        meta.setLastModified(jsonRoot.getLong(Meta.JSON_LAST_MOD));
        meta.setUuid(jsonRoot.getString(Meta.JSON_UUID));
        meta.setType(AssetType.valueOf(jsonRoot.getString(Meta.JSON_TYPE)));
    }

    private void parseTerrain(Meta meta, JsonValue jsonTerrain) {
        if(jsonTerrain == null) return;

        final MetaTerrain terrain = new MetaTerrain();
        terrain.setSize(jsonTerrain.getInt(MetaTerrain.JSON_SIZE));
        terrain.setUv(jsonTerrain.getFloat(MetaTerrain.JSON_UV_SCALE, Terrain.DEFAULT_UV_SCALE));
        terrain.setSplatmap(jsonTerrain.getString(MetaTerrain.JSON_SPLATMAP, null));
        terrain.setSplatBase(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_BASE, null));
        terrain.setSplatR(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_R, null));
        terrain.setSplatG(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_G, null));
        terrain.setSplatB(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_B, null));
        terrain.setSplatA(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_A, null));

        meta.setTerrain(terrain);
    }

    private void parseWater(Meta meta, JsonValue jsonValue) {
        if(jsonValue == null) return;

        final MetaWater water = new MetaWater();
        water.setSize(jsonValue.getInt(MetaWater.JSON_SIZE));
        water.setDudvMap(jsonValue.getString(MetaWater.JSON_DUDV));
        water.setNormalMap(jsonValue.getString(MetaWater.JSON_NORMAL_MAP));
        water.setTiling(jsonValue.getFloat(MetaWater.JSON_TILING));
        water.setWaveStrength(jsonValue.getFloat(MetaWater.JSON_WAVE_STRENGTH));
        water.setWaveSpeed(jsonValue.getFloat(MetaWater.JSON_WAVE_SPEED));
        water.setReflectivity(readWithDefault(jsonValue, MetaWater.JSON_REFLECTIVITY, Water.DEFAULT_REFLECTIVITY));
        water.setShineDamper(readWithDefault(jsonValue, MetaWater.JSON_SHINE_DAMPER, Water.DEFAULT_SHINE_DAMPER));

        meta.setWater(water);
    }

    private void parseModel(Meta meta, JsonValue jsonModel) {
        if(jsonModel == null) return;

        final MetaModel model = new MetaModel();
        final JsonValue materials = jsonModel.get(MetaModel.JSON_DEFAULT_MATERIALS);

        for(final JsonValue mat : materials) {
            System.out.println(mat.name);
            final String g3dbID = mat.name;
            final String assetUUID = materials.getString(g3dbID);
            model.getDefaultMaterials().put(g3dbID, assetUUID);
        }

        meta.setModel(model);
    }

    private void parseSkybox(Meta meta, JsonValue jsonValue) {
        if(jsonValue == null) return;

        final MetaSkybox skybox = new MetaSkybox();
        skybox.setPositiveX(jsonValue.getString(MetaSkybox.JSON_POSITIVE_X));
        skybox.setNegativeX(jsonValue.getString(MetaSkybox.JSON_NEGATIVE_X));
        skybox.setPositiveY(jsonValue.getString(MetaSkybox.JSON_POSITIVE_Y));
        skybox.setNegativeY(jsonValue.getString(MetaSkybox.JSON_NEGATIVE_Y));
        skybox.setPositiveZ(jsonValue.getString(MetaSkybox.JSON_POSITIVE_Z));
        skybox.setNegativeZ(jsonValue.getString(MetaSkybox.JSON_NEGATIVE_Z));

        meta.setMetaSkybox(skybox);
    }

    /**
     * When new values are added and cannot be found on jsonValue.getXXX(),
     * an IllegalArgumentException is thrown.
     *
     * To try and maintain backwards compatibility between meta changes,
     * if we have a default value, that can be used with this method to
     * default to it when it could not be found in the meta during parsing.
     *
     * @param jsonValue the JsonValue instance
     * @param jsonKey the jsonKey value to try and read
     * @param defaultValue the value to default to if jsonKey not found
     * @return float from meta file, or default if not found
     */
    private float readWithDefault(JsonValue jsonValue, String jsonKey, float defaultValue) {
        try {
            return jsonValue.getFloat(jsonKey);
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

}
