/*
 * Copyright (c) 2022. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.terrain.layers;

import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.shaders.TerrainUberShader;

/**
 * Terrain Layer for rendering textures on the terrain based on slope angles of the surface normal.
 * @author JamesTKhan
 * @version November 06, 2022
 */
public class SlopeTerrainLayer extends HeightTerrainLayer {
    public float strength;

    public SlopeTerrainLayer(TextureAsset textureAsset, float minHeight, float maxHeight, float strength) {
        super(textureAsset, minHeight, maxHeight);
        this.strength = strength;
    }

    @Override
    public void setUniforms(TerrainUberShader shader, int uniformIndex) {
        shader.set(shader.u_slopeTextureLayers[uniformIndex], textureAsset.getTexture());
        shader.set(shader.u_slopeHeightLayers[uniformIndex], minHeight, maxHeight);
        shader.set(shader.u_slopeStrengthLayers[uniformIndex], strength);
    }
}
