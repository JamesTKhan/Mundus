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

import java.util.Random;

import com.badlogic.gdx.math.Interpolation;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.events.TerrainVerticesChangedEvent;

/**
 *
 * @author Marcus Brummer
 * @version 20-06-2016
 */
public class PerlinNoiseGenerator extends Generator<PerlinNoiseGenerator> {

    private final Random rand = new Random();
    private final TerrainComponent terrainComponent;

    private long seed = 0;
    // number of noise functions
    private int octaves = 1;
    // decrease of amplitude per octave
    private float roughness = 0;

    PerlinNoiseGenerator(TerrainComponent terrainComponent) {
        super(terrainComponent.getTerrainAsset().getTerrain());
        this.terrainComponent = terrainComponent;
    }

    public PerlinNoiseGenerator seed(long seed) {
        this.seed = seed;
        return this;
    }

    public PerlinNoiseGenerator octaves(int octaves) {
        this.octaves = octaves;
        return this;
    }

    public PerlinNoiseGenerator roughness(float roughness) {
        this.roughness = roughness;
        return this;
    }

    @Override
    public void terraform() {
        rand.setSeed(seed);

        // final float d = (float) Math.pow(2, this.octaves);

        for (int i = 0; i < terrain.heightData.length; i++) {
            int x = i % terrain.vertexResolution;
            int z = (int) Math.floor((double) i / terrain.vertexResolution);

            float height = Interpolation.linear.apply(minHeight, maxHeight, getInterpolatedNoise(x / 4f, z / 4f));
            height += Interpolation.linear.apply(minHeight / 3f, maxHeight / 3f, getInterpolatedNoise(x / 2f, z / 2f));

            terrain.heightData[z * terrain.vertexResolution + x] = height;
        }

        terrain.update();
        Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
    }

    private float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (float) (1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }

    private float getNoise(int x, int z) {
        rand.setSeed(x * 49632 + z * 325176 + seed);
        return rand.nextFloat();
    }

    private float getInterpolatedNoise(float x, float z) {
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;

        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1, i2, fracZ);
    }

    private float getSmoothNoise(int x, int z) {
        // corner noise
        float corners = getNoise(x + 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
                + getNoise(x + 1, z + 1);
        corners /= 16f;
        // side noise
        float sides = getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1);
        sides /= 8f;
        // center noise
        float center = getNoise(x, z) / 4f;

        return corners + sides + center;
    }

}
