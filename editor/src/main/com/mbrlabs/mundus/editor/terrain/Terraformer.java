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

package com.mbrlabs.mundus.editor.terrain;

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;

/**
 * Factory class for terraform generators.
 *
 * @author Marcus Brummer
 * @version 20-06-2016
 */
public class Terraformer {

    public static PerlinNoiseGenerator perlin(final TerrainComponent terrainComponent) {
        return new PerlinNoiseGenerator(terrainComponent);
    }

    public static HeightMapGenerator heightMap(final TerrainComponent terrainComponent) {
        return new HeightMapGenerator(terrainComponent);
    }

}
