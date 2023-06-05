package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import com.mbrlabs.mundus.commons.water.Water;
import com.mbrlabs.mundus.commons.water.WaterMaterial;
import com.mbrlabs.mundus.commons.water.attributes.WaterColorAttribute;
import com.mbrlabs.mundus.commons.water.attributes.WaterFloatAttribute;
import com.mbrlabs.mundus.commons.water.attributes.WaterIntAttribute;
import com.mbrlabs.mundus.commons.water.attributes.WaterMaterialAttribute;
import com.mbrlabs.mundus.commons.water.attributes.WaterTextureAttribute;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;

/**
 * @author JamesTKhan
 * @version October 16, 2022
 */
public class WaterUberShader extends LightShader {
    protected static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/water.uber.vert.glsl";
    protected static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/water.uber.frag.glsl";

    public static class WaterInputs {
        public final static Uniform diffuseUVTransform = new Uniform("u_diffuseUVTransform");

        // Color Attributes
        public final static Uniform color = new Uniform("u_color");

        // Float attributes
        public final static Uniform moveFactor = new Uniform("u_moveFactor");
        public final static Uniform tiling = new Uniform("u_tiling");
        public final static Uniform waveStrength = new Uniform("u_waveStrength");
        public final static Uniform reflectivity = new Uniform("u_reflectivity");
        public final static Uniform shineDamper = new Uniform("u_shineDamper");
        public final static Uniform foamScale = new Uniform("u_foamScale");
        public final static Uniform foamEdgeBias = new Uniform("u_foamEdgeBias");
        public final static Uniform foamEdgeDistance = new Uniform("u_foamEdgeDistance");
        public final static Uniform foamFallOffDistance = new Uniform("u_foamFallOffDistance");
        public final static Uniform foamScrollSpeed = new Uniform("u_foamScrollSpeed");
        public final static Uniform maxVisibleDepth = new Uniform("u_maxVisibleDepth");

        // Texture attributes
        public final static Uniform reflectionTexture = new Uniform("u_reflectionTexture");
        public final static Uniform refractionTexture = new Uniform("u_refractionTexture");
        public final static Uniform refractionDepthTexture = new Uniform("u_refractionDepthTexture");
        public final static Uniform dudvTexture = new Uniform("u_dudvTexture");
        public final static Uniform normalMapTexture = new Uniform("u_normalMapTexture");
        public final static Uniform foamTexture = new Uniform("u_foamTexture");

        public final static Uniform fogColor = new Uniform("u_fogColor");
        public final static Uniform fogEquation = new Uniform("u_fogEquation");
    }

    public static class WaterSetters {
        public final static Setter diffuseUVTransform = new LocalSetter() {
            @Override
            public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                WaterMaterial mat = getWaterMaterial(renderable);
                WaterFloatAttribute attr = (WaterFloatAttribute) mat.get(WaterFloatAttribute.FoamUVOffset);
                if (attr != null)
                    shader.set(inputID,  attr.value, attr.value, 200f, 200f);
            }
        };

        // Color attributes
        public final static Setter color = new LocalSetter() {
            @Override
            public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                WaterMaterialAttribute waterMaterialAttribute = (WaterMaterialAttribute) combinedAttributes
                        .get(WaterMaterialAttribute.WaterMaterial);
                WaterMaterial waterMaterial = waterMaterialAttribute.waterMaterial;
                WaterColorAttribute attr = (WaterColorAttribute)(waterMaterial.get(WaterColorAttribute.Diffuse));

                if (attr != null)
                    shader.set(inputID, attr.color);
            }
        };

        // Float attributes
        public final static Setter moveFactor = new FloatSetter(WaterFloatAttribute.MoveFactor, 0);
        public final static Setter tiling = new FloatSetter(WaterFloatAttribute.Tiling, Water.DEFAULT_TILING);
        public final static Setter waveStrength = new FloatSetter(WaterFloatAttribute.WaveStrength, Water.DEFAULT_WAVE_STRENGTH);
        public final static Setter reflectivity = new FloatSetter(WaterFloatAttribute.Reflectivity, Water.DEFAULT_REFLECTIVITY);
        public final static Setter shineDamper = new FloatSetter(WaterFloatAttribute.ShineDamper, Water.DEFAULT_SHINE_DAMPER);
        public final static Setter foamScale = new FloatSetter(WaterFloatAttribute.FoamPatternScale, Water.DEFAULT_FOAM_SCALE);
        public final static Setter foamEdgeBias = new FloatSetter(WaterFloatAttribute.FoamEdgeBias, Water.DEFAULT_FOAM_EDGE_BIAS);
        public final static Setter foamEdgeDistance = new FloatSetter(WaterFloatAttribute.FoamEdgeDistance, Water.DEFAULT_FOAM_EDGE_DISTANCE);
        public final static Setter foamFallOffDistance = new FloatSetter(WaterFloatAttribute.FoamFallOffDistance, Water.DEFAULT_FOAM_FALL_OFF_DISTANCE);
        public final static Setter foamScrollSpeed = new FloatSetter(WaterFloatAttribute.FoamScrollSpeed, Water.DEFAULT_FOAM_SCROLL_SPEED);
        public final static Setter maxVisibleDepth = new FloatSetter(WaterFloatAttribute.MaxVisibleDepth, Water.DEFAULT_MAX_VISIBLE_DEPTH);

        // Texture attributes
        public final static Setter reflectionTexture = getTextureSetter(WaterTextureAttribute.Reflection);
        public final static Setter refractionTexture = getTextureSetter(WaterTextureAttribute.Refraction);
        public final static Setter refractionDepthTexture = getTextureSetter(WaterTextureAttribute.RefractionDepth);
        public final static Setter dudvTexture = getTextureSetter(WaterTextureAttribute.Dudv);
        public final static Setter normalMapTexture = getTextureSetter(WaterTextureAttribute.NormalMap);
        public final static Setter foamTexture = getTextureSetter(WaterTextureAttribute.Foam);

        private static Setter getTextureSetter(final long attribute) {
            return new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                    WaterMaterialAttribute waterMaterialAttribute = (WaterMaterialAttribute) combinedAttributes
                            .get(WaterMaterialAttribute.WaterMaterial);
                    WaterMaterial waterMaterial = waterMaterialAttribute.waterMaterial;

                    WaterTextureAttribute waterTextureAttributeU = waterMaterial.get(WaterTextureAttribute.class, attribute);
                    final int unit = shader.context.textureBinder
                            .bind(waterTextureAttributeU.textureDescription);
                    shader.set(inputID, unit);
                }
            };
        }

    }

    /**
     * Custom Setter that tracks the long attribute value and default value to set
     */
    public static class FloatSetter extends LocalSetter {
        private final long attribute;
        private final float defaultValue;

        public FloatSetter(long attribute, float defaultValue) {
            this.attribute = attribute;
            this.defaultValue = defaultValue;
        }

        @Override
        public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
            shader.set(inputID, getFloatValue(renderable, attribute, defaultValue));
        }
    }

    // Global uniforms
    public final int u_projViewTrans;

    // env
    public final int u_fogColor;
    public final int u_fogEquation;

    // Object uniforms
    public final int u_worldTrans;
    public final int u_cameraPosition;
    public final int u_cameraNearFar;

    // Water color uniforms
    public final int u_color;

    // Water float uniforms
    public final int u_diffuseUVTransform;
    public final int u_tiling;
    public final int u_moveFactor;
    public final int u_waveStrength;
    public final int u_reflectivity;
    public final int u_shineDamper;
    public final int u_foamScale;
    public final int u_foamEdgeBias;
    public final int u_foamEdgeDistance;
    public final int u_foamFallOffDistance;
    public final int u_foamScrollSpeed;
    public final int u_maxVisibleDepth;

    // Water texture uniforms
    public final int u_reflectionTexture;
    public final int u_refractionTexture;
    public final int u_refractionDepthTexture;
    public final int u_dudvTexture;
    public final int u_normalMapTexture;
    public final int u_foamTexture;

    /** The renderable used to create this shader, invalid after the call to init */
    private Renderable renderable;
    private DefaultShader.Config config;

    /** The attributes that this shader supports */
    protected final long attributesMask;
    protected final long waterMaterialMask;

    public WaterUberShader(Renderable renderable, DefaultShader.Config config) {
        this.renderable = renderable;
        this.config = config;

        WaterMaterial waterMaterial = getWaterMaterial(renderable);

        attributesMask = ShaderUtils.combineAttributeMasks(renderable);
        waterMaterialMask = waterMaterial.getMask();

        String prefix = createPrefixForRenderable(renderable);

        // Compile the shaders
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, this, prefix);

        u_projViewTrans = register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        u_worldTrans = register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
        u_cameraPosition = register(DefaultShader.Inputs.cameraPosition, DefaultShader.Setters.cameraPosition);
        u_cameraNearFar = register(DefaultShader.Inputs.cameraNearFar, DefaultShader.Setters.cameraNearFar);

        // Water specific
        u_diffuseUVTransform = register(WaterInputs.diffuseUVTransform, WaterSetters.diffuseUVTransform);
        u_tiling = register(WaterInputs.tiling, WaterSetters.tiling);

        u_moveFactor = register(WaterInputs.moveFactor, WaterSetters.moveFactor);
        u_waveStrength = register(WaterInputs.waveStrength, WaterSetters.waveStrength);
        u_reflectivity = register(WaterInputs.reflectivity, WaterSetters.reflectivity);
        u_shineDamper = register(WaterInputs.shineDamper, WaterSetters.shineDamper);
        u_foamScale = register(WaterInputs.foamScale, WaterSetters.foamScale);
        u_foamEdgeBias = register(WaterInputs.foamEdgeBias, WaterSetters.foamEdgeBias);
        u_foamEdgeDistance = register(WaterInputs.foamEdgeDistance, WaterSetters.foamEdgeDistance);
        u_foamFallOffDistance = register(WaterInputs.foamFallOffDistance, WaterSetters.foamFallOffDistance);
        u_foamScrollSpeed = register(WaterInputs.foamScrollSpeed, WaterSetters.foamScrollSpeed);
        u_maxVisibleDepth = register(WaterInputs.maxVisibleDepth, WaterSetters.maxVisibleDepth);

        u_reflectionTexture = register(WaterInputs.reflectionTexture, WaterSetters.reflectionTexture);
        u_refractionTexture = register(WaterInputs.refractionTexture, WaterSetters.refractionTexture);
        u_refractionDepthTexture = register(WaterInputs.refractionDepthTexture, WaterSetters.refractionDepthTexture);
        u_dudvTexture = register(WaterInputs.dudvTexture, WaterSetters.dudvTexture);
        u_normalMapTexture = register(WaterInputs.normalMapTexture, WaterSetters.normalMapTexture);
        u_foamTexture = register(WaterInputs.foamTexture, WaterSetters.foamTexture);

        u_color = register(WaterInputs.color, WaterSetters.color);

        u_fogColor = register(WaterInputs.fogColor);
        u_fogEquation = register(WaterInputs.fogEquation);
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
        context.begin();

        context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        context.setDepthMask(true);

        super.begin(camera, context);
    }

    @Override
    public void render(Renderable renderable) {
        final MundusEnvironment env = (MundusEnvironment) renderable.environment;
        setLights(env);

        super.render(renderable);
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        if (combinedAttributes.has(ColorAttribute.Fog) && combinedAttributes.has(FogAttribute.FogEquation)) {
            set(u_fogColor, ((ColorAttribute)combinedAttributes.get(ColorAttribute.Fog)).color);
            set(u_fogEquation, ((FogAttribute)combinedAttributes.get(FogAttribute.FogEquation)).value);
        }

        int cullFace = GL20.GL_BACK;
        // If mask has WaterIntAttribute, use it
        if ((WaterIntAttribute.CullFace & waterMaterialMask) == WaterIntAttribute.CullFace) {
            WaterMaterial material = getWaterMaterial(renderable);
            WaterIntAttribute attr = (WaterIntAttribute) material.get(WaterIntAttribute.CullFace);
            if (attr != null && attr.value != -1) {
                cullFace = attr.value;
            }
        }
        context.setCullFace(cullFace);


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

        WaterMaterial waterMaterial = getWaterMaterial(instance);
        return waterMaterialMask == waterMaterial.getMask();
    }

    private static float getFloatValue(Renderable renderable, long attribute, float defaultValue) {
        WaterMaterial waterMaterial = getWaterMaterial(renderable);
        WaterFloatAttribute attr = (WaterFloatAttribute) waterMaterial.get(attribute);
        if (attr != null) {
            return attr.value;
        } else {
            return defaultValue;
        }
    }

    private static WaterMaterial getWaterMaterial(Renderable renderable) {
        return renderable.material.get(WaterMaterialAttribute.class, WaterMaterialAttribute.WaterMaterial).waterMaterial;
    }

    private String createPrefixForRenderable(Renderable renderable) {
        String prefix = "";

        if (renderable.environment.has(ColorAttribute.Fog)) {
            prefix += "#define fogFlag\n";
        }

        WaterMaterial waterMaterial = getWaterMaterial(renderable);

        if (waterMaterial.has(WaterTextureAttribute.Reflection)) {
            prefix += "#define reflectionFlag\n";
        }

        if (waterMaterial.has(WaterTextureAttribute.Refraction)) {
            prefix += "#define refractionFlag\n";
        }

        if (config instanceof PBRShaderConfig) {
            PBRShaderConfig pbrShaderConfig = (PBRShaderConfig) config;
            if(pbrShaderConfig.manualSRGB != PBRShaderConfig.SRGB.NONE){
                prefix += "#define MANUAL_SRGB\n";
                if(pbrShaderConfig.manualSRGB == PBRShaderConfig.SRGB.FAST){
                    prefix += "#define SRGB_FAST_APPROXIMATION\n";
                }
            }
            if(pbrShaderConfig.manualGammaCorrection){
                prefix += "#define GAMMA_CORRECTION " + pbrShaderConfig.gamma + "\n";
            }
        }
        // Drop reference, no longer needed
        config = null;

        return prefix;
    }
}
