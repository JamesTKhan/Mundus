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
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class ModelShader extends LightShader {

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

    // ============================ FOG ============================
    protected final int UNIFORM_FOG_DENSITY = register(new Uniform("u_fogDensity"));
    protected final int UNIFORM_FOG_GRADIENT = register(new Uniform("u_fogGradient"));
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));

    private ShaderProgram program;

    public ModelShader() {
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

    @Override
    public void end() {
        context.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
