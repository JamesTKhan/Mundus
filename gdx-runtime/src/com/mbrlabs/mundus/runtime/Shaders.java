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

package com.mbrlabs.mundus.runtime;

import com.mbrlabs.mundus.commons.shaders.DepthShader;
import com.mbrlabs.mundus.commons.shaders.ModelShader;
import com.mbrlabs.mundus.commons.shaders.SkyboxShader;
import com.mbrlabs.mundus.commons.shaders.TerrainShader;
import com.mbrlabs.mundus.commons.shaders.WaterShader;

public class Shaders {

    private final ModelShader modelShader;
    private final TerrainShader terrainShader;
    private final WaterShader waterShader;
    private final SkyboxShader skyboxShader;
    private final DepthShader depthShader;

    public Shaders() {
        modelShader = new ModelShader();
        modelShader.init();
        terrainShader = new TerrainShader();
        terrainShader.init();
        waterShader = new WaterShader();
        waterShader.init();
        skyboxShader = new SkyboxShader();
        skyboxShader.init();
        depthShader = new DepthShader();
        depthShader.init();
    }

    public ModelShader getModelShader() {
        return modelShader;
    }

    public TerrainShader getTerrainShader() {
        return terrainShader;
    }

    public WaterShader getWaterShader() {
        return waterShader;
    }

    public SkyboxShader getSkyboxShader() {
        return skyboxShader;
    }

    public DepthShader getDepthShader() {
        return depthShader;
    }
}
