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

package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.PointLightsAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class ModelShader extends ClippableShader {

    private static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/model.vert.glsl";
    private static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/model.frag.glsl";

    // ============================ MATERIALS ============================
    protected final int UNIFORM_MATERIAL_DIFFUSE_TEXTURE = register(new Uniform("u_diffuseTexture"));
    protected final int UNIFORM_MATERIAL_DIFFUSE_COLOR = register(new Uniform("u_diffuseColor"));
    protected final int UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE = register(new Uniform("u_diffuseUseTexture"));
    protected final int UNIFORM_MATERIAL_SHININESS = register(new Uniform("u_shininess"));


    // ============================ MATRICES & CAM POSITION ============================
    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_camPos"));

    // ============================ LIGHTS ============================
//    protected final int UNIFORM_AMBIENT_LIGHT_COLOR = register(new Uniform("u_ambientLight.color"));
//    protected final int UNIFORM_AMBIENT_LIGHT_INTENSITY = register(new Uniform("u_ambientLight.intensity"));

    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR = register(new Uniform("gDirectionalLight.Base.Color"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR_AMBIENT = register(new Uniform("gDirectionalLight.Base.AmbientColor"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_DIR = register(new Uniform("gDirectionalLight.Direction"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY = register(new Uniform("gDirectionalLight.Base.DiffuseIntensity"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY_AMBIENT = register(new Uniform("gDirectionalLight.Base.AmbientIntensity"));

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

    private ShaderProgram program;

    public ModelShader() {
        super();
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

        this.context.setCullFace(GL20.GL_BACK);
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        this.context.setDepthMask(true);

        program.bind();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        set(UNIFORM_CAM_POS, camera.position);
    }

    @Override
    public void render(Renderable renderable) {
        super.render(renderable);

        final MundusEnvironment env = (MundusEnvironment) renderable.environment;

        setLights(env);
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);

        // texture uniform
        TextureAttribute diffuseTexture = ((TextureAttribute) (renderable.material.get(TextureAttribute.Diffuse)));
        ColorAttribute diffuseColor = ((ColorAttribute) (renderable.material.get(ColorAttribute.Diffuse)));

        if (diffuseTexture != null) {
            set(UNIFORM_MATERIAL_DIFFUSE_TEXTURE, diffuseTexture.textureDescription.texture);
            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 1);
        } else {
            set(UNIFORM_MATERIAL_DIFFUSE_COLOR, diffuseColor.color);
            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 0);
        }

        // shininess
        if (renderable.material.has(FloatAttribute.Shininess)) {
            float shininess = ((FloatAttribute) renderable.material.get(FloatAttribute.Shininess)).value;
            set(UNIFORM_MATERIAL_SHININESS, shininess);
        }

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
//        set(UNIFORM_AMBIENT_LIGHT_COLOR, env.getAmbientLight().color);
//        set(UNIFORM_AMBIENT_LIGHT_INTENSITY, env.getAmbientLight().intensity);

        // TODO light array for each light type

        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights;
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first();
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR, light.color.r, light.color.g, light.color.b);
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR_AMBIENT, env.getAmbientLight().color.r, env.getAmbientLight().color.g, env.getAmbientLight().color.b);
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

                set(UNIFORM_POINT_LIGHT_ATT_CONSTANT[i], light.attenuation.constant);
                set(UNIFORM_POINT_LIGHT_ATT_LINEAR[i], light.attenuation.linear);
                set(UNIFORM_POINT_LIGHT_ATT_EXP[i] , light.attenuation.exponential);
            }
        }

        // TODO point lights, spot lights
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
