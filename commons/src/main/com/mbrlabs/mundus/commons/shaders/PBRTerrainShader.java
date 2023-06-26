package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Vector2;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.TerrainMaterial;
import com.mbrlabs.mundus.commons.terrain.attributes.TerrainMaterialAttribute;

/**
 * @author JamesTKhan
 * @version May 15, 2023
 */
public class PBRTerrainShader extends MundusPBRShader {
    public static final TextureDescriptor<Texture> textureDescription = new TextureDescriptor<>();
    private final static Vector2 v2 = new Vector2();
    public static class TerrainInputs {
        public final static Uniform terrainSize = new Uniform("u_terrainSize");

        public final static Uniform splatTexture = new Uniform("u_texture_splat");
        public final static Uniform splatRTexture = new Uniform("u_texture_r");
        public final static Uniform splatGTexture = new Uniform("u_texture_g");
        public final static Uniform splatBTexture = new Uniform("u_texture_b");
        public final static Uniform splatATexture = new Uniform("u_texture_a");

        public final static Uniform splatRNormal = new Uniform("u_texture_r_normal");
        public final static Uniform splatGNormal = new Uniform("u_texture_g_normal");
        public final static Uniform splatBNormal = new Uniform("u_texture_b_normal");
        public final static Uniform splatANormal = new Uniform("u_texture_a_normal");
    }

    public static class TerrainSetters {
        public final static Setter terrainSize = new LocalSetter() {
            @Override
            public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TerrainMaterialAttribute terrainMaterialAttribute = (TerrainMaterialAttribute) combinedAttributes.get(TerrainMaterialAttribute.TerrainMaterial);
                shader.set(inputID, v2.set(terrainMaterialAttribute.terrainMaterial.getTerrain().terrainWidth, terrainMaterialAttribute.terrainMaterial.getTerrain().terrainDepth));
            }
        };

        public final static Setter splatRTexture = getTerrainTextureSetter(SplatTexture.Channel.R);
        public final static Setter splatGTexture = getTerrainTextureSetter(SplatTexture.Channel.G);
        public final static Setter splatBTexture = getTerrainTextureSetter(SplatTexture.Channel.B);
        public final static Setter splatATexture = getTerrainTextureSetter(SplatTexture.Channel.A);

        public final static Setter splatRNormal = getTerrainNormalSetter(SplatTexture.Channel.R);
        public final static Setter splatGNormal = getTerrainNormalSetter(SplatTexture.Channel.G);
        public final static Setter splatBNormal = getTerrainNormalSetter(SplatTexture.Channel.B);
        public final static Setter splatANormal = getTerrainNormalSetter(SplatTexture.Channel.A);

        private static Setter getTerrainTextureSetter(final SplatTexture.Channel channel) {
            return new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    TerrainMaterialAttribute terrainMaterialAttribute = (TerrainMaterialAttribute) combinedAttributes.get(TerrainMaterialAttribute.TerrainMaterial);
                    TerrainMaterial material = terrainMaterialAttribute.terrainMaterial;
                    textureDescription.texture = material.getTexture(channel).getTexture();
                    final int unit = shader.context.textureBinder
                            .bind(textureDescription);
                    shader.set(inputID, unit);
                }
            };
        }

        private static Setter getTerrainNormalSetter(final SplatTexture.Channel channel) {
            return new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    TerrainMaterialAttribute terrainMaterialAttribute = (TerrainMaterialAttribute) combinedAttributes.get(TerrainMaterialAttribute.TerrainMaterial);
                    TerrainMaterial material = terrainMaterialAttribute.terrainMaterial;
                    textureDescription.texture = material.getNormalTexture(channel).getTexture();
                    final int unit = shader.context.textureBinder
                            .bind(textureDescription);
                    shader.set(inputID, unit);
                }
            };
        }

        public static Setter splatTexture = new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TerrainMaterialAttribute terrainMaterialAttribute = (TerrainMaterialAttribute) combinedAttributes.get(TerrainMaterialAttribute.TerrainMaterial);
                TerrainMaterial material = terrainMaterialAttribute.terrainMaterial;

                textureDescription.texture = material.getSplatmap().getTexture();
                final int unit = shader.context.textureBinder
                        .bind(textureDescription);
                shader.set(inputID, unit);
            }
        };

    }

    public final int u_splatTexture;
    public final int u_splatRTexture;
    public final int u_splatGTexture;
    public final int u_splatBTexture;
    public final int u_splatATexture;

    public final int u_splatRNormal;
    public final int u_splatGNormal;
    public final int u_splatBNormal;
    public final int u_splatANormal;
    public final int u_terrainSize;

    protected final long terrainMaterialMask;


    public PBRTerrainShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);

        TerrainMaterial terrainMaterial = getTerrainMaterial(renderable);
        terrainMaterialMask = terrainMaterial.getMask();

        u_terrainSize = register(TerrainInputs.terrainSize, TerrainSetters.terrainSize);

        u_splatTexture = register(TerrainInputs.splatTexture, TerrainSetters.splatTexture);

        u_splatRTexture = register(TerrainInputs.splatRTexture, TerrainSetters.splatRTexture);
        u_splatGTexture = register(TerrainInputs.splatGTexture, TerrainSetters.splatGTexture);
        u_splatBTexture = register(TerrainInputs.splatBTexture, TerrainSetters.splatBTexture);
        u_splatATexture = register(TerrainInputs.splatATexture, TerrainSetters.splatATexture);

        // Normals
        u_splatRNormal = register(TerrainInputs.splatRNormal, TerrainSetters.splatRNormal);
        u_splatGNormal = register(TerrainInputs.splatGNormal, TerrainSetters.splatGNormal);
        u_splatBNormal = register(TerrainInputs.splatBNormal, TerrainSetters.splatBNormal);
        u_splatANormal = register(TerrainInputs.splatANormal, TerrainSetters.splatANormal);
    }

    @Override
    public boolean canRender(Renderable renderable) {
        TerrainMaterial terrainMaterial = getTerrainMaterial(renderable);
        if (terrainMaterial != null)
            return terrainMaterialMask == terrainMaterial.getMask() && super.canRender(renderable);

        return super.canRender(renderable);
    }

    private static TerrainMaterial getTerrainMaterial(Renderable renderable) {
        TerrainMaterialAttribute attr = renderable.material.get(TerrainMaterialAttribute.class, TerrainMaterialAttribute.TerrainMaterial);
        if (attr != null)
            return attr.terrainMaterial;

        return null;
    }
}
