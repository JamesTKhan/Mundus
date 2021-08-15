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

import java.util.HashMap;

/**
 * @author Tibor Zsuro
 * @version 12-08-2021
 */
public class ModelComponentDTO {

    private String modelID;
    private HashMap<String, String> materials; // g3db material id to material asset uuid

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

}
