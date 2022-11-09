package com.mbrlabs.mundus.commons.terrain.layers;

import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.shaders.TerrainUberShader;
import com.mbrlabs.mundus.commons.utils.TextureProvider;

/**
 * Abstract Terrain Layer class for different types of texture layers on terrains.
 * @author JamesTKhan
 * @version November 04, 2022
 */
public abstract class TerrainLayer implements TextureProvider {
    public TextureAsset textureAsset;
    public boolean active = true;

    private String name;

    public TerrainLayer(TextureAsset textureAsset) {
        this.textureAsset = textureAsset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the uniforms needed for this terrain layer on the shader
     * @param shader the shader to set uniforms on.
     * @param uniformIndex the uniform index of the terrain shaders layer arrays to use.
     */
    public abstract void setUniforms(TerrainUberShader shader, int uniformIndex);

    @Override
    public Texture getTexture() {
        return textureAsset.getTexture();
    }
}
