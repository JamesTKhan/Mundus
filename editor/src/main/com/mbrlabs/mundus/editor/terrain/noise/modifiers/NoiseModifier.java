
package com.mbrlabs.mundus.editor.terrain.noise.modifiers;

import com.mbrlabs.mundus.editor.terrain.noise.TerrainNoiseData;
import com.mbrlabs.mundus.editor.utils.FastNoiseLite;

/**
 * Base Noise Modifier for modifying terrain noise
 * @author JamesTKhan
 * @version November 02, 2022
 */
public abstract class NoiseModifier implements TerrainModifier {
    protected static final FastNoiseLite.Vector2 fVector2 = new FastNoiseLite.Vector2(0,0);
    protected FastNoiseLite noise = new FastNoiseLite();
    protected float domainWarpFrequency = 0.0f;
    protected float frequency = 0.0f;

    public FastNoiseLite getNoiseGenerator() {
        return noise;
    }

    @Override
    public void modify(TerrainNoiseData terrainNoiseData, float x, float y) {
        fVector2.x = x;
        fVector2.y = y;

        // Apply domain warp
        noise.SetFrequency(domainWarpFrequency);
        noise.DomainWarp(fVector2);

        // Reset regular frequency
        noise.SetFrequency(frequency);
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public float getDomainWarpFrequency() {
        return domainWarpFrequency;
    }

    public void setDomainWarpFrequency(float domainWarpFrequency) {
        this.domainWarpFrequency = domainWarpFrequency;
    }
}
