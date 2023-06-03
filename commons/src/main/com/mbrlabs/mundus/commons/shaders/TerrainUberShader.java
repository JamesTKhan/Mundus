package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.TerrainMaterial;
import com.mbrlabs.mundus.commons.terrain.attributes.TerrainMaterialAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;

/**
 * @author JamesTKhan
 * @version August 12, 2022
 * @deprecated Use {@link PBRTerrainShader} instead.
 */
public class TerrainUberShader extends LightShader {
    protected static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.uber.vert.glsl";
    protected static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.uber.frag.glsl";
    public static final TextureDescriptor<Texture> textureDescription = new TextureDescriptor<>();

    public static Vector3 terrainClippingPlane = new Vector3(0.0f,0.0f, 0.0f);
    public static float terrainClippingHeight = 0f;

    private static final Vector2 v2 = new Vector2();

    public static class TerrainInputs {
        public final static Uniform terrainSize = new Uniform("u_terrainSize");
        public final static Uniform clipPlane = new Uniform("u_clipPlane");

        public final static Uniform uvScale = new Uniform("u_uvScale");
        public final static Uniform baseTexture = new Uniform("u_baseTexture");
        public final static Uniform baseNormal = new Uniform("u_texture_base_normal");

        public final static Uniform splatTexture = new Uniform("u_texture_splat");
        
        public final static Uniform splatRTexture = new Uniform("u_texture_r");
        public final static Uniform splatGTexture = new Uniform("u_texture_g");
        public final static Uniform splatBTexture = new Uniform("u_texture_b");
        public final static Uniform splatATexture = new Uniform("u_texture_a");

        public final static Uniform splatRNormal = new Uniform("u_texture_r_normal");
        public final static Uniform splatGNormal = new Uniform("u_texture_g_normal");
        public final static Uniform splatBNormal = new Uniform("u_texture_b_normal");
        public final static Uniform splatANormal = new Uniform("u_texture_a_normal");

        public final static Uniform fogColor = new Uniform("u_fogColor");
        public final static Uniform fogEquation = new Uniform("u_fogEquation");
    }

    public static class TerrainSetters {
        public final static Setter terrainSize = new LocalSetter() {
            @Override
            public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TerrainMaterialAttribute terrainMaterialAttribute = (TerrainMaterialAttribute) combinedAttributes.get(TerrainMaterialAttribute.TerrainMaterial);
                shader.set(inputID, v2.set(terrainMaterialAttribute.terrainMaterial.getTerrain().terrainWidth, terrainMaterialAttribute.terrainMaterial.getTerrain().terrainDepth));
            }
        };

        public final static Setter clipPlane = new LocalSetter() {
            @Override
            public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                shader.set(inputID, terrainClippingPlane.x, terrainClippingPlane.y, terrainClippingPlane.z, terrainClippingHeight);
            }
        };

        public final static Setter uvScale = new LocalSetter() {
            @Override
            public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                TerrainMaterialAttribute terrainMaterialAttribute = (TerrainMaterialAttribute) combinedAttributes.get(TerrainMaterialAttribute.TerrainMaterial);
                shader.set(inputID, terrainMaterialAttribute.terrainMaterial.getTerrain().getUvScale());
            }
        };

        public final static Setter baseTexture = getTerrainTextureSetter(SplatTexture.Channel.BASE);
        public final static Setter splatRTexture = getTerrainTextureSetter(SplatTexture.Channel.R);
        public final static Setter splatGTexture = getTerrainTextureSetter(SplatTexture.Channel.G);
        public final static Setter splatBTexture = getTerrainTextureSetter(SplatTexture.Channel.B);
        public final static Setter splatATexture = getTerrainTextureSetter(SplatTexture.Channel.A);

        public final static Setter baseNormal = getTerrainNormalSetter(SplatTexture.Channel.BASE);
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

    // Global uniforms
    public final int u_projViewTrans;

    // Object uniforms
    public final int u_worldTrans;
    public final int u_normalMatrix;
    public final int u_cameraPosition;

    // env
    public final int u_fogColor;
    public final int u_fogEquation;

    // Terrain uniforms
    public final int u_terrainSize;
    public final int u_clipPlane;
    public final int u_uvScale;
    public final int u_baseTexture;
    public final int u_baseNormal;
    public final int u_splatTexture;
    public final int u_splatRTexture;
    public final int u_splatGTexture;
    public final int u_splatBTexture;
    public final int u_splatATexture;

    public final int u_splatRNormal;
    public final int u_splatGNormal;
    public final int u_splatBNormal;
    public final int u_splatANormal;

    /** The renderable used to create this shader, invalid after the call to init */
    private Renderable renderable;

    /** The attributes that this shader supports */
    protected final long attributesMask;
    protected final long terrainMaterialMask;

    public TerrainUberShader(Renderable renderable, DefaultShader.Config config) {
        this.renderable = renderable;

        TerrainMaterial terrainMaterial = getTerrainMaterial(renderable);

        attributesMask = ShaderUtils.combineAttributeMasks(renderable);
        terrainMaterialMask = terrainMaterial.getMask();

        String prefix = createPrefixForRenderable(renderable);

        // Compile the shaders
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, this, prefix);

        u_projViewTrans = register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        u_worldTrans = register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        u_normalMatrix = register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);
        u_cameraPosition = register(DefaultShader.Inputs.cameraPosition, DefaultShader.Setters.cameraPosition);

        // Custom setters
        u_terrainSize = register(TerrainInputs.terrainSize, TerrainSetters.terrainSize);
        u_clipPlane = register(TerrainInputs.clipPlane, TerrainSetters.clipPlane);
        u_uvScale = register(TerrainInputs.uvScale, TerrainSetters.uvScale);

        // Splat map
        u_splatTexture = register(TerrainInputs.splatTexture, TerrainSetters.splatTexture);

        // Base diffuse
        u_baseTexture = register(TerrainInputs.baseTexture, TerrainSetters.baseTexture);
        u_splatRTexture = register(TerrainInputs.splatRTexture, TerrainSetters.splatRTexture);
        u_splatGTexture = register(TerrainInputs.splatGTexture, TerrainSetters.splatGTexture);
        u_splatBTexture = register(TerrainInputs.splatBTexture, TerrainSetters.splatBTexture);
        u_splatATexture = register(TerrainInputs.splatATexture, TerrainSetters.splatATexture);

        // Normals
        u_baseNormal = register(TerrainInputs.baseNormal, TerrainSetters.baseNormal);
        u_splatRNormal = register(TerrainInputs.splatRNormal, TerrainSetters.splatRNormal);
        u_splatGNormal = register(TerrainInputs.splatGNormal, TerrainSetters.splatGNormal);
        u_splatBNormal = register(TerrainInputs.splatBNormal, TerrainSetters.splatBNormal);
        u_splatANormal = register(TerrainInputs.splatANormal, TerrainSetters.splatANormal);

        u_fogColor = register(TerrainInputs.fogColor);
        u_fogEquation = register(TerrainInputs.fogEquation);
    }

    protected String createPrefixForRenderable(Renderable renderable) {
        String prefix = "";

        if (renderable.environment.has(ColorAttribute.Fog)) {
            prefix += "#define fogFlag\n";
        }

        TerrainMaterial terrainMaterial = getTerrainMaterial(renderable);

        if (terrainMaterial.isTriplanar()) {
            prefix += "#define triplanarFlag\n";
        }

        if (terrainMaterial.getSplatmap() != null && terrainMaterial.getSplatmap().getTexture() != null) {
            prefix += "#define splatFlag\n";
        }

        if (terrainMaterial.hasTextureChannel(SplatTexture.Channel.R)) {
            prefix += "#define splatRFlag\n";
        }

        if (terrainMaterial.hasTextureChannel(SplatTexture.Channel.G)) {
            prefix += "#define splatGFlag\n";
        }

        if (terrainMaterial.hasTextureChannel(SplatTexture.Channel.B)) {
            prefix += "#define splatBFlag\n";
        }

        if (terrainMaterial.hasTextureChannel(SplatTexture.Channel.A)) {
            prefix += "#define splatAFlag\n";
        }

        // Normals
        if (terrainMaterial.hasNormalTextures()) {
            prefix += "#define normalTextureFlag\n";

            if (terrainMaterial.hasNormalChannel(SplatTexture.Channel.BASE)) {
                prefix += "#define baseNormalFlag\n";
            }

            if (terrainMaterial.hasNormalChannel(SplatTexture.Channel.R)) {
                prefix += "#define splatRNormalFlag\n";
            }

            if (terrainMaterial.hasNormalChannel(SplatTexture.Channel.G)) {
                prefix += "#define splatGNormalFlag\n";
            }

            if (terrainMaterial.hasNormalChannel(SplatTexture.Channel.B)) {
                prefix += "#define splatBNormalFlag\n";
            }

            if (terrainMaterial.hasNormalChannel(SplatTexture.Channel.A)) {
                prefix += "#define splatANormalFlag\n";
            }
        }

        return prefix;
    }

    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        this.init(program, renderable);
        renderable = null;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        context.setCullFace(GL20.GL_BACK);
        context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        context.setDepthTest(GL20.GL_LESS, 0f, 1f);
        context.setDepthMask(true);

        super.begin(camera, context);
    }

    @Override
    public void render(Renderable renderable) {
        final MundusEnvironment env = (MundusEnvironment) renderable.environment;
        setLights(env);
        setShadows(env);

        super.render(renderable);
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        if (combinedAttributes.has(ColorAttribute.Fog) && combinedAttributes.has(FogAttribute.FogEquation)) {
            set(u_fogColor, ((ColorAttribute)combinedAttributes.get(ColorAttribute.Fog)).color);
            set(u_fogEquation, ((FogAttribute)combinedAttributes.get(FogAttribute.FogEquation)).value);
        }
        super.render(renderable, combinedAttributes);
    }

    @Override
    public int compareTo(Shader other) {
        if (other == null) return -1;
        if (other == this) return 0;
        return 0; // FIXME compare shaders on their impact on performance
    }

    @Override
    public boolean canRender(Renderable instance) {
        if (ShaderUtils.combineAttributeMasks(instance) != attributesMask) {
            return false;
        }

        TerrainMaterial terrainMaterial = getTerrainMaterial(instance);
        return terrainMaterialMask == terrainMaterial.getMask();
    }

    private static TerrainMaterial getTerrainMaterial(Renderable renderable) {
        return renderable.material.get(TerrainMaterialAttribute.class, TerrainMaterialAttribute.TerrainMaterial).terrainMaterial;
    }

}
