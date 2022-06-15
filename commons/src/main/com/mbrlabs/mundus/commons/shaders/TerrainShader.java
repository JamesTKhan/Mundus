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
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.TerrainTexture;
import com.mbrlabs.mundus.commons.terrain.TerrainTextureAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
public class TerrainShader extends LightShader {

    protected static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.vert.glsl";
    protected static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/terrain.frag.glsl";

    // ============================ MATRICES & CAM POSITION ============================
    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_PROJ_VIEW_WORLD_MATRIX = register(new Uniform("u_viewWorldTrans"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_camPos"));

    // ============================ TEXTURE SPLATTING ============================
    protected final int UNIFORM_TERRAIN_SIZE = register(new Uniform("u_terrainSize"));
    protected final int UNIFORM_TEXTURE_BASE = register(new Uniform("u_texture_base"));
    protected final int UNIFORM_TEXTURE_R = register(new Uniform("u_texture_r"));
    protected final int UNIFORM_TEXTURE_G = register(new Uniform("u_texture_g"));
    protected final int UNIFORM_TEXTURE_B = register(new Uniform("u_texture_b"));
    protected final int UNIFORM_TEXTURE_A = register(new Uniform("u_texture_a"));
    protected final int UNIFORM_TEXTURE_SPLAT = register(new Uniform("u_texture_splat"));
    protected final int UNIFORM_TEXTURE_HAS_SPLATMAP = register(new Uniform("u_texture_has_splatmap"));
    protected final int UNIFORM_TEXTURE_HAS_DIFFUSE = register(new Uniform("u_texture_has_diffuse"));

    // ============================ FOG ============================
    protected final int UNIFORM_FOG_DENSITY = register(new Uniform("u_fogDensity"));
    protected final int UNIFORM_FOG_GRADIENT = register(new Uniform("u_fogGradient"));
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));

    private final Vector2 terrainSize = new Vector2();

    protected ShaderProgram program;

    public TerrainShader() {
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
        this.camera = camera;
        this.context = context;
        context.begin();
        context.setCullFace(GL20.GL_BACK);

        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        this.context.setDepthMask(true);

        program.bind();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        set(UNIFORM_CAM_POS, camera.position);
    }
    private final Matrix4 tmpMatrix = new Matrix4();
    @Override
    public void render(Renderable renderable) {
        super.render(renderable);

        final MundusEnvironment env = (MundusEnvironment) renderable.environment;

        setLights(env);
        setShadows(env);
        setTerrainSplatTextures(renderable);
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);
        set(UNIFORM_PROJ_VIEW_WORLD_MATRIX, tmpMatrix.set(camera.view).mul(renderable.worldTransform));

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

    protected void setTerrainSplatTextures(Renderable renderable) {
        final TerrainTextureAttribute splatAttrib = (TerrainTextureAttribute) renderable.material
                .get(TerrainTextureAttribute.ATTRIBUTE_SPLAT0);
        final TerrainTexture terrainTexture = splatAttrib.terrainTexture;

        // base texture
        SplatTexture st = terrainTexture.getTexture(SplatTexture.Channel.BASE);
        if (st != null) {
            set(UNIFORM_TEXTURE_BASE, st.texture.getTexture());
            set(UNIFORM_TEXTURE_HAS_DIFFUSE, 1);
        } else {
            set(UNIFORM_TEXTURE_HAS_DIFFUSE, 0);
        }

        // splat textures
        if (terrainTexture.getSplatmap() != null) {
            set(UNIFORM_TEXTURE_HAS_SPLATMAP, 1);
            set(UNIFORM_TEXTURE_SPLAT, terrainTexture.getSplatmap().getTexture());
            st = terrainTexture.getTexture(SplatTexture.Channel.R);
            if (st != null) set(UNIFORM_TEXTURE_R, st.texture.getTexture());
            st = terrainTexture.getTexture(SplatTexture.Channel.G);
            if (st != null) set(UNIFORM_TEXTURE_G, st.texture.getTexture());
            st = terrainTexture.getTexture(SplatTexture.Channel.B);
            if (st != null) set(UNIFORM_TEXTURE_B, st.texture.getTexture());
            st = terrainTexture.getTexture(SplatTexture.Channel.A);
            if (st != null) set(UNIFORM_TEXTURE_A, st.texture.getTexture());
        } else {
            set(UNIFORM_TEXTURE_HAS_SPLATMAP, 0);
        }

        // set terrain world size
        terrainSize.x = terrainTexture.getTerrain().terrainWidth;
        terrainSize.y = terrainTexture.getTerrain().terrainDepth;
        set(UNIFORM_TERRAIN_SIZE, terrainSize);
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
