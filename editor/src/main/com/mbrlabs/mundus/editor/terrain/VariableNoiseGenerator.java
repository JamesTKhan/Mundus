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

import com.badlogic.gdx.math.Interpolation;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.events.TerrainVerticesChangedEvent;

public class VariableNoiseGenerator extends Generator<VariableNoiseGenerator> {

    private final TerrainComponent terrainComponent;

    private int seed = 58008;
    private float frequency = 0.1f;
    private float gain = 0.5f;
    private int octaves = 1;
    private float lacunarity = 0.5f;
    private String noiseType = "Simplex Fractal";
    private int noiseInt;

    VariableNoiseGenerator(TerrainComponent terrainComponent) {
        super(terrainComponent.getTerrainAsset().getTerrain());
        this.terrainComponent = terrainComponent;
    }

    public VariableNoiseGenerator seed(int seed) {
        this.seed = seed;
        return this;
    }

    public VariableNoiseGenerator frequency(float frequency) {
        this.frequency = frequency;
        return this;
    }

    public VariableNoiseGenerator gain(float gain) {
        this.gain = gain;
        return this;
    }

    public VariableNoiseGenerator octaves(int octaves) {
        this.octaves = octaves;
        return this;
    }

    public VariableNoiseGenerator lacunarity(float lacunarity) {
        this.lacunarity = lacunarity;
        return this;
    }

    public VariableNoiseGenerator noiseType(String noiseType){
        this.noiseType = noiseType;
        return this;
    }

    @Override
    public void terraform() {
        switch (noiseType){
            case "Value":
                noiseInt = Noise.VALUE;
                break;
            case "Value Fractal":
                noiseInt = Noise.VALUE_FRACTAL;
                break;
            case "Perlin":
                noiseInt = Noise.PERLIN_FRACTAL;
                break;
            case "Simplex":
                noiseInt = Noise.SIMPLEX;
                break;
            case "Simplex Fractal":
                noiseInt = Noise.SIMPLEX_FRACTAL;
                break;
            case "Cellular":
                noiseInt = Noise.CELLULAR;
                break;
            case "White Noise":
                noiseInt = Noise.WHITE_NOISE;
                break;
            case "Cubic":
                noiseInt = Noise.CUBIC;
                break;
            case "Cubic Fractal":
                noiseInt = Noise.CUBIC_FRACTAL;
                break;
            case "Foam":
                noiseInt = Noise.FOAM;
                break;
            case "Foam Fractal":
                noiseInt = Noise.FOAM_FRACTAL;
                break;
            case "HONEY":
                noiseInt = Noise.HONEY;
                break;
            case "Honey Fractal":
                noiseInt = Noise.HONEY_FRACTAL;
                break;
            case "Mutant":
                noiseInt = Noise.MUTANT;
                break;
            case "Mutant Fractal":
                noiseInt = Noise.MUTANT_FRACTAL;
                break;
        }
        Noise noise = new Noise(seed, frequency, noiseInt, octaves, lacunarity, gain);

        for (int i = 0; i < terrain.heightData.length; i++) {
            //since heightdata is one dimensional we need to keep track of the index ourselves
            int x = i % terrain.vertexResolution;
            int z = (int) Math.floor((double) i / terrain.vertexResolution);

            double e = noise.getNoise(x, z );
            //normalize noise value from (-1:1) to (0:1)
            e = (e + 1f) / 2;

            //scale noise to max and min height
            e = Interpolation.linear.apply(minHeight, maxHeight, (float) e);

            terrain.heightData[z * terrain.vertexResolution + x] = (float) e;
        }
        terrain.update();
        Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
    }
}
