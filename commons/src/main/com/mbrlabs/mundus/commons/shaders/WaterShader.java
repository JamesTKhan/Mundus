package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import com.mbrlabs.mundus.commons.water.Water;
import com.mbrlabs.mundus.commons.water.WaterFloatAttribute;
import com.mbrlabs.mundus.commons.water.WaterTextureAttribute;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;

public class WaterShader extends LightShader {

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

    // ============================ FOG ============================
    protected final int UNIFORM_FOG_EQUATION = register(new Uniform("u_fogEquation"));
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));


    public ShaderProgram program;


    public WaterShader() {
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, this);
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
        set(UNIFORM_CAM_POS, camera.position.x, camera.position.y, camera.position.z,
                1.1881f / (camera.far * camera.far));
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
        FogAttribute fogEquation = renderable.environment.get(FogAttribute.class, FogAttribute.FogEquation);
        ColorAttribute colorAttribute = renderable.environment.get(ColorAttribute.class, ColorAttribute.Fog);
        if (fogEquation != null && colorAttribute != null) {
            set(UNIFORM_FOG_EQUATION, fogEquation.value);
            set(UNIFORM_FOG_COLOR, colorAttribute.color);
        } else {
            set(UNIFORM_FOG_EQUATION, Vector3.Zero);
        }

        // bind attributes, bind mesh & render; then unbinds everything
        renderable.meshPart.render(program);
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
