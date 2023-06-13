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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;

/**
 * @author Marcus Brummer
 * @version 08-01-2016
 */
public class SkyboxShader extends BaseShader {

    private final static String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/skybox.vert.glsl";
    private final static String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/skybox.frag.glsl";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_TEXTURE = register(new Uniform("u_texture"));

    protected final int UNIFORM_FOG = register(new Uniform("u_fog"));
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));

    private boolean rotateEnabled = Skybox.DEFAULT_ROTATE_ENABLED;
    private float rotateSpeed = Skybox.DEFAULT_ROTATE_SPEED;
    private float rotation = 0f;

    private final ShaderProgram program;

    private final Matrix4 transform = new Matrix4();

    public SkyboxShader() {
        super();
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

        // GL_LEQUAL instead of the default GL_LESS.
        // The depth buffer will be filled with values of 1.0 for the skybox,
        // so we need to make sure the skybox passes the depth tests with values less than or equal to
        // the depth buffer instead of less than.
        context.setDepthTest(GL20.GL_LEQUAL);

        program.bind();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        transform.idt();
        transform.translate(camera.position);

        if (isRotateEnabled()) {
            rotation += rotateSpeed * Gdx.graphics.getDeltaTime();
            transform.rotateRad(Vector3.Y, MathUtils.degreesToRadians * rotation);
        }

        set(UNIFORM_TRANS_MATRIX, transform);
    }

    @Override
    public void render(Renderable renderable) {

        // texture uniform
        CubemapAttribute cubemapAttribute = ((CubemapAttribute) (renderable.material
                .get(CubemapAttribute.EnvironmentMap)));
        if (cubemapAttribute != null) {
            set(UNIFORM_TEXTURE, cubemapAttribute.textureDescription);
        }

        FogAttribute fogEquation = renderable.environment.get(FogAttribute.class, FogAttribute.FogEquation);
        ColorAttribute colorAttribute = renderable.environment.get(ColorAttribute.class, ColorAttribute.Fog);
        if (fogEquation != null && colorAttribute != null) {
            set(UNIFORM_FOG, 1);
            set(UNIFORM_FOG_COLOR, colorAttribute.color);
        } else {
            set(UNIFORM_FOG, 0);
        }

        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        context.setDepthTest(GL20.GL_LESS);
        context.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }

    public boolean isRotateEnabled() {
        return rotateEnabled;
    }

    public void setRotateEnabled(boolean rotateEnabled) {
        this.rotateEnabled = rotateEnabled;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }
}
