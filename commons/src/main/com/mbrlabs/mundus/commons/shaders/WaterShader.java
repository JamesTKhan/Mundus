package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
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

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_TEXTURE = register(new Uniform("u_texture"));
    public final int UNIFORM_REFRACTION_TEXTURE = register(new Uniform("u_refractionTexture"));
    protected final int UNIFORM_DUDV_TEXTURE = register(new Uniform("u_dudvTexture"));
    protected final int UNIFORM_NORMAL_MAP_TEXTURE = register(new Uniform("u_normalMapTexture"));
    protected final int UNIFORM_MOVE_FACTOR = register(new Uniform("u_moveFactor"));
    protected final int UNIFORM_TILING = register(new Uniform("u_tiling"));
    protected final int UNIFORM_WAVE_STRENGTH = register(new Uniform("u_waveStrength"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_cameraPosition"));
    protected final int UNIFORM_LIGHT_POS = register(new Uniform("u_lightPositon"));
    protected final int UNIFORM_LIGHT_COLOR = register(new Uniform("u_lightColor"));

    public ShaderProgram program;

    private float moveFactor = 0;
    private float waveSpeed = Water.DEFAULT_WAVE_SPEED;

    public WaterShader() {
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER);
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

        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 100f);
        this.context.setDepthMask(true);

        program.bind();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        set(UNIFORM_CAM_POS, camera.position);
    }

    @Override
    public void render(Renderable renderable) {
        final MundusEnvironment env = (MundusEnvironment) renderable.environment;

        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights;
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first();
            set(UNIFORM_LIGHT_COLOR, light.color.r, light.color.g, light.color.b);
            // Normally this would be position but directional lights do not have a position
            set(UNIFORM_LIGHT_POS, light.direction);
        }

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

        // Floats
        WaterFloatAttribute tiling = (WaterFloatAttribute) renderable.material.get(WaterFloatAttribute.Tiling);
        if (tiling != null) {
            set(UNIFORM_TILING, tiling.value);
        } else {
            set(UNIFORM_TILING, Water.DEFAULT_TILING);
        }

        WaterFloatAttribute strength = (WaterFloatAttribute) renderable.material.get(WaterFloatAttribute.WaveStrength);
        if (strength != null) {
            set(UNIFORM_WAVE_STRENGTH, strength.value);
        } else {
            set(UNIFORM_WAVE_STRENGTH, Water.DEFAULT_WAVE_STRENGTH);
        }

        WaterFloatAttribute speed = (WaterFloatAttribute) renderable.material.get(WaterFloatAttribute.WaveSpeed);
        if (speed != null) {
            waveSpeed = speed.value;
        }

        moveFactor +=  waveSpeed * Gdx.graphics.getDeltaTime();
        moveFactor %= 1;

        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);
        set(UNIFORM_MOVE_FACTOR, moveFactor);

        // bind attributes, bind mesh & render; then unbinds everything
        renderable.meshPart.render(program);
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
