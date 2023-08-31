package com.mbrlabs.mundus.editor.terrain.noise.modifiers;

import com.mbrlabs.mundus.editor.terrain.noise.TerrainNoiseData;
import com.mbrlabs.mundus.editor.utils.FastNoiseLite;

/**
 * @author JamesTKhan
 * @version November 01, 2022
 */
public class ElevationModifier extends NoiseModifier {

    public ElevationModifier() {
        frequency = 0.01f;

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(6);
        noise.SetFrequency(frequency);
        noise.SetFractalGain(0.5f);
        noise.SetFractalLacunarity(2f);
        noise.SetSeed(876509);
        noise.SetFractalWeightedStrength(0.3f);
    }

    @Override
    public String getName() {
        return "Elevation Modifier";
    }

    @Override
    public void modify(TerrainNoiseData terrainNoiseData, float x, float y) {
        super.modify(terrainNoiseData, x, y);

        if (terrainNoiseData.elevation == 0)
            terrainNoiseData.elevation = noise.GetNoise(fVector2.x, fVector2.y);
        else if (!additive)
            terrainNoiseData.elevation *= noise.GetNoise(fVector2.x, fVector2.y);
        else
            terrainNoiseData.elevation+= noise.GetNoise(fVector2.x, fVector2.y);
    }
}
