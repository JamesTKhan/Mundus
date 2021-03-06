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

import com.badlogic.gdx.utils.ObjectMap;

/**
 *
 * @author Marcus Brummer
 * @version 26-10-2016
 */
public class MetaModel {

    public static final String JSON_DEFAULT_MATERIALS = "mats";
    public static final String JSON_NUM_BONES = "numBones";

    // g3db material id -> material asset uuid
    private ObjectMap<String, String> defaultMaterials = new ObjectMap<>();

    private int numBones;

    public ObjectMap<String, String> getDefaultMaterials() {
        return defaultMaterials;
    }

    public int getNumBones() {
        return numBones;
    }

    public void setNumBones(int numBones) {
        this.numBones = numBones;
    }

    @Override
    public String toString() {
        return "MetaModel{" +
                "defaultMaterials=" + defaultMaterials +
                "numBones=" + numBones +
                '}';
    }
}
