/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
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
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

/**
 * @author JamesTKhan
 * @version June 10, 2022
 * @deprecated We use PBR Depth shader now
 */
public class ShadowMapShader extends BaseShader {

    protected static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/shadowmap.vert.glsl";
    protected static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/shadowmap.frag.glsl";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewWorldTrans"));
    protected final int UNIFORM_ALPHA_TEST = register(new Uniform("u_alphaTest"));
    protected final int UNIFORM_USE_ALPHA_TEST = register(new Uniform("u_useAlphaTest"));
    protected final int UNIFORM_TEXTURE_DIFFUSE = register(new Uniform("u_diffuseTexture"));

    protected ShaderProgram program;

    private final Matrix4 tmpMatrix = new Matrix4();
    protected final Vector3 center = new Vector3();

    public ShadowMapShader() {
        program = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, this);
    }

    @Override
    public void init() {
        super.init(program, null);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        this.camera = camera;
        context.begin();
        //context.setCullFace(GL20.GL_FRONT);

        this.context.setDepthTest(GL20.GL_LEQUAL , 0f, 1f);
        this.context.setDepthMask(true);

        program.bind();
    }

    @Override
    public void render(Renderable renderable) {

        set(UNIFORM_PROJ_VIEW_MATRIX, tmpMatrix.set(camera.combined).mul(renderable.worldTransform));

        if(renderable.material.has(FloatAttribute.AlphaTest)) {
            set(UNIFORM_USE_ALPHA_TEST, 1);
            FloatAttribute floatAttribute = (FloatAttribute) renderable.material.get(FloatAttribute.AlphaTest);
            set(UNIFORM_ALPHA_TEST, floatAttribute.value);

            if (renderable.material.has(PBRTextureAttribute.Diffuse)) {
                PBRTextureAttribute textureAttribute = (PBRTextureAttribute) renderable.material.get(PBRTextureAttribute.Diffuse);
                set(UNIFORM_TEXTURE_DIFFUSE, textureAttribute.textureDescription.texture);
            }

        } else {
            set(UNIFORM_USE_ALPHA_TEST, 0);
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

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }
}
