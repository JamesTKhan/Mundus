package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import com.mbrlabs.mundus.commons.water.Water;
import com.mbrlabs.mundus.commons.water.WaterFloatAttribute;
import com.mbrlabs.mundus.commons.water.WaterTextureAttribute;

public class WaterShader extends BaseShader {

    protected static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/water.vert.glsl";
    protected static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/water.frag.glsl";

    // ============================ MATRICES & CAM POSITION ============================
    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_cameraPosition"));
    protected final int UNIFORM_DIFFUSE_UV = register(new Uniform("u_diffuseUVTransform"));

    // ============================ TEXTURES ============================
    protected final int UNIFORM_TEXTURE = register(new Uniform("u_texture"));
    public final int UNIFORM_REFRACTION_TEXTURE = register(new Uniform("u_refractionTexture"));
    public final int UNIFORM_REFRACTION_DEPTH_TEXTURE = register(new Uniform("u_refractionDepthTexture"));
    protected final int UNIFORM_DUDV_TEXTURE = register(new Uniform("u_dudvTexture"));
    protected final int UNIFORM_NORMAL_MAP_TEXTURE = register(new Uniform("u_normalMapTexture"));
    protected final int UNIFORM_FOAM_TEXTURE = register(new Uniform("u_foamTexture"));

    // ============================ FLOATS ============================
    protected final int UNIFORM_MOVE_FACTOR = register(new Uniform("u_moveFactor"));
    protected final int UNIFORM_TILING = register(new Uniform("u_tiling"));
    protected final int UNIFORM_WAVE_STRENGTH = register(new Uniform("u_waveStrength"));
    protected final int UNIFORM_SPECULAR_REFLECTIVITY = register(new Uniform("u_reflectivity"));
    protected final int UNIFORM_SHINE_DAMPER = register(new Uniform("u_shineDamper"));
    protected final int UNIFORM_FOAM_SCALE = register(new Uniform("u_foamScale"));
    protected final int UNIFORM_FOAM_EDGE_BIAS = register(new Uniform("u_foamEdgeBias"));
    protected final int UNIFORM_FOAM_EDGE_DISTANCE = register(new Uniform("u_foamEdgeDistance"));
    protected final int UNIFORM_FOAM_FALL_OFF_DISTANCE = register(new Uniform("u_foamFallOffDistance"));
    protected final int UNIFORM_FOAM_FALL_SCROLL_SPEED = register(new Uniform("u_foamScrollSpeed"));
    protected final int UNIFORM_CAM_NEAR_PLANE= register(new Uniform("u_camNearPlane"));
    protected final int UNIFORM_CAM_FAR_PLANE= register(new Uniform("u_camFarPlane"));

    // ============================ LIGHTS ============================
    protected final int UNIFORM_AMBIENT_LIGHT_COLOR = register(new Uniform("u_ambientLight.color"));
    protected final int UNIFORM_AMBIENT_LIGHT_INTENSITY = register(new Uniform("u_ambientLight.intensity"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR = register(new Uniform("gDirectionalLight.Base.Color"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_DIR = register(new Uniform("gDirectionalLight.Direction"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY = register(new Uniform("gDirectionalLight.Base.DiffuseIntensity"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY_AMBIENT = register(new Uniform("gDirectionalLight.Base.AmbientIntensity"));

    protected final int UNIFORM_DIRECTIONAL_MAT_AMBIENT = register(new Uniform("gMaterial.AmbientColor"));
    protected final int UNIFORM_DIRECTIONAL_MAT_DIFFUSE = register(new Uniform("gMaterial.DiffuseColor"));
    protected final int UNIFORM_DIRECTIONAL_MAT_SPECULAR = register(new Uniform("gMaterial.SpecularColor"));

    protected final int UNIFORM_POINT_LIGHT_NUM = register(new Uniform("gNumPointLights"));

    protected int[] UNIFORM_POINT_LIGHT_COLOR = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_INTENSITY = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_INTENSITY_AMBIENT = new int[ShaderUtils.MAX_POINT_LIGHTS];

    protected int[] UNIFORM_POINT_LIGHT_POS = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_ATT_CONSTANT = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_ATT_LINEAR = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_ATT_EXP = new int[ShaderUtils.MAX_POINT_LIGHTS];

    // ============================ FOG ============================
    protected final int UNIFORM_FOG_DENSITY = register(new Uniform("u_fogDensity"));
    protected final int UNIFORM_FOG_GRADIENT = register(new Uniform("u_fogGradient"));
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));


    public ShaderProgram program;


    public WaterShader() {
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER);

        for (int i = 0; i < ShaderUtils.MAX_POINT_LIGHTS; i++) {
            UNIFORM_POINT_LIGHT_COLOR[i] = register(new Uniform("gPointLights["+ i +"].Base.Color"));
            UNIFORM_POINT_LIGHT_INTENSITY[i] = register(new Uniform("gPointLights["+ i +"].Base.DiffuseIntensity"));
            UNIFORM_POINT_LIGHT_INTENSITY_AMBIENT[i] = register(new Uniform("gPointLights["+ i +"].Base.AmbientIntensity"));

            UNIFORM_POINT_LIGHT_POS[i] = register(new Uniform("gPointLights["+ i +"].LocalPos"));
            UNIFORM_POINT_LIGHT_ATT_CONSTANT[i] = register(new Uniform("gPointLights["+ i +"].Atten.Constant"));
            UNIFORM_POINT_LIGHT_ATT_LINEAR[i] = register(new Uniform("gPointLights["+ i +"].Atten.Linear"));
            UNIFORM_POINT_LIGHT_ATT_EXP[i] = register(new Uniform("gPointLights["+ i +"].Atten.Exp"));
        }
    }

    @Override
    public void init() {
        super.init(program, null);
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;

        context.begin();
        context.setCullFace(GL20.GL_BACK);

        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        this.context.setDepthMask(true);

        program.bind();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        set(UNIFORM_CAM_NEAR_PLANE, camera.near);
        set(UNIFORM_CAM_FAR_PLANE, camera.far);
        set(UNIFORM_CAM_POS, camera.position);
    }

    @Override
    public void render(Renderable renderable) {
        final MundusEnvironment env = (MundusEnvironment) renderable.environment;

        setLights(env);

        // Set Textures
        WaterTextureAttribute dudvAttrib = (WaterTextureAttribute) renderable.material.get(WaterTextureAttribute.Dudv);
        if (dudvAttrib != null) {
            set(UNIFORM_DUDV_TEXTURE, dudvAttrib.getTexture());
        }

        WaterTextureAttribute normalAttrib = (WaterTextureAttribute) renderable.material.get(WaterTextureAttribute.NormalMap);
        if (normalAttrib != null) {
            set(UNIFORM_NORMAL_MAP_TEXTURE, normalAttrib.getTexture());
        }

        WaterTextureAttribute reflectTexture = (WaterTextureAttribute) renderable.material.get(WaterTextureAttribute.Reflection);
        if (reflectTexture != null) {
            set(UNIFORM_TEXTURE, reflectTexture.getTexture());
        }

        WaterTextureAttribute refractTexture = (WaterTextureAttribute) renderable.material.get(WaterTextureAttribute.Refraction);
        if (refractTexture != null) {
            set(UNIFORM_REFRACTION_TEXTURE, refractTexture.getTexture());
        }

        WaterTextureAttribute refractDepthTexture = (WaterTextureAttribute) renderable.material.get(WaterTextureAttribute.RefractionDepth);
        if (refractDepthTexture != null) {
            set(UNIFORM_REFRACTION_DEPTH_TEXTURE, refractDepthTexture.getTexture());
        }

        WaterTextureAttribute foamTexture = (WaterTextureAttribute) renderable.material.get(WaterTextureAttribute.Foam);
        if (foamTexture != null) {
            set(UNIFORM_FOAM_TEXTURE, foamTexture.getTexture());
        }

        setFloatUniform(renderable, WaterFloatAttribute.Tiling, UNIFORM_TILING, Water.DEFAULT_TILING);
        setFloatUniform(renderable, WaterFloatAttribute.WaveStrength, UNIFORM_WAVE_STRENGTH, Water.DEFAULT_WAVE_STRENGTH);
        setFloatUniform(renderable, WaterFloatAttribute.Reflectivity, UNIFORM_SPECULAR_REFLECTIVITY, Water.DEFAULT_REFLECTIVITY);
        setFloatUniform(renderable, WaterFloatAttribute.ShineDamper, UNIFORM_SHINE_DAMPER, Water.DEFAULT_SHINE_DAMPER);
        setFloatUniform(renderable, WaterFloatAttribute.FoamPatternScale, UNIFORM_FOAM_SCALE, Water.DEFAULT_FOAM_SCALE);
        setFloatUniform(renderable, WaterFloatAttribute.FoamEdgeBias, UNIFORM_FOAM_EDGE_BIAS, Water.DEFAULT_FOAM_EDGE_BIAS);
        setFloatUniform(renderable, WaterFloatAttribute.FoamEdgeDistance, UNIFORM_FOAM_EDGE_DISTANCE, Water.DEFAULT_FOAM_EDGE_DISTANCE);
        setFloatUniform(renderable, WaterFloatAttribute.FoamFallOffDistance, UNIFORM_FOAM_FALL_OFF_DISTANCE, Water.DEFAULT_FOAM_FALL_OFF_DISTANCE);
        setFloatUniform(renderable, WaterFloatAttribute.FoamScrollSpeed, UNIFORM_FOAM_FALL_SCROLL_SPEED, Water.DEFAULT_FOAM_SCROLL_SPEED);
        setFloatUniform(renderable, WaterFloatAttribute.MoveFactor, UNIFORM_MOVE_FACTOR, 0);

        WaterFloatAttribute uvOffset = (WaterFloatAttribute) renderable.material.get(WaterFloatAttribute.FoamUVOffset);
        if (uvOffset != null) {
            set(UNIFORM_DIFFUSE_UV, uvOffset.value, uvOffset.value, 200f, 200f);
        }

        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);

        // Fog
        final Fog fog = env.getFog();
        if (fog == null) {
            set(UNIFORM_FOG_DENSITY, 0f);
            set(UNIFORM_FOG_GRADIENT, 0f);
        } else {
            set(UNIFORM_FOG_DENSITY, fog.density);
            set(UNIFORM_FOG_GRADIENT, fog.gradient);
            set(UNIFORM_FOG_COLOR, fog.color);
        }

        // bind attributes, bind mesh & render; then unbinds everything
        renderable.meshPart.render(program);
    }

    private void setLights(MundusEnvironment env) {
        // ambient
        set(UNIFORM_AMBIENT_LIGHT_COLOR, env.getAmbientLight().color);
        set(UNIFORM_AMBIENT_LIGHT_INTENSITY, env.getAmbientLight().intensity);

        // TODO light array for each light type

        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights;
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first();
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR, Color.WHITE.r, Color.WHITE.g, Color.WHITE.b);
            set(UNIFORM_DIRECTIONAL_LIGHT_DIR, light.direction);
            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY, light.intensity);
            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY_AMBIENT, env.getAmbientLight().intensity);
        }

        PointLightsAttribute attr = env.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> pointLights = attr == null ? null : attr.lights;
        if (pointLights != null && pointLights.size > 0) {
            set(UNIFORM_POINT_LIGHT_NUM, pointLights.size);

            for (int i = 0; i < pointLights.size; i++) {
                PointLight light = pointLights.get(i);

                set(UNIFORM_POINT_LIGHT_COLOR[i], light.color.r, light.color.g, light.color.b);
                set(UNIFORM_POINT_LIGHT_POS[i], light.position);
                set(UNIFORM_POINT_LIGHT_INTENSITY[i], light.intensity);
                set(UNIFORM_POINT_LIGHT_INTENSITY_AMBIENT[i], 10.0f);

                set(UNIFORM_POINT_LIGHT_ATT_CONSTANT[i], 1.0f);
                set(UNIFORM_POINT_LIGHT_ATT_LINEAR[i], 0.045f);
                set(UNIFORM_POINT_LIGHT_ATT_EXP[i] ,0.0075f);
            }
        }

        // TODO point lights, spot lights
    }

    private void setFloatUniform(Renderable renderable, long attribute, int uniform, float defaultValue) {
        WaterFloatAttribute attr = (WaterFloatAttribute) renderable.material.get(attribute);
        if (attr != null) {
            set(uniform, attr.value);
        } else {
            set(uniform, defaultValue);
        }
    }

    @Override
    public void end() {
        context.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }

}
