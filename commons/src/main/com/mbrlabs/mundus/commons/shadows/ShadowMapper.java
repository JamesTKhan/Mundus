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

package com.mbrlabs.mundus.commons.shadows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.environment.ShadowMap;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;

/**
 * Wrapper around an FBO for shadow mapping, based on DirectionalShadowLight from libGDX and gdx-gltf
 *
 * @author James Pooley
 * @version June 10, 2022
 */
public class ShadowMapper implements ShadowMap {

    protected FrameBuffer fbo;
    protected Camera cam;
    protected Vector3 direction;
    protected final TextureDescriptor textureDesc;
    protected final Vector3 center = new Vector3();

    int textureWidth;
    int textureHeight;
    int viewportWidth;
    int viewportHeight;
    float near;
    float far;

    public ShadowMapper(int textureWidth, int textureHeight, int viewportWidth, int viewportHeight, float near, float far, Vector3 direction) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.near = near;
        this.far = far;
        this.direction = direction;

        fbo = new NestableFrameBuffer(Pixmap.Format.RGBA8888, textureWidth, textureHeight, true);
        cam = new OrthographicCamera(viewportWidth, viewportHeight);
        cam.near = near;
        cam.far = far;

        textureDesc = new TextureDescriptor();
        textureDesc.minFilter = textureDesc.magFilter = Texture.TextureFilter.Nearest;
        textureDesc.uWrap = textureDesc.vWrap = Texture.TextureWrap.ClampToEdge;
    }

    public void begin() {
        float halfDepth = cam.near + 0.5f * (cam.far - cam.near);
        cam.direction.set(direction).nor();
        cam.position.set(direction).scl(-halfDepth).add(center);
        cam.normalizeUp();
        cam.update();

        final int w = fbo.getWidth();
        final int h = fbo.getHeight();
        fbo.begin();
        Gdx.gl.glViewport(0, 0, w, h);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(1, 1, w - 2, h - 2);
    }

    public void end(){
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        fbo.end();
    }

    public void setCenter(Vector3 center) {
        this.center.set(center);
    }


    public FrameBuffer getFbo() {
        return fbo;
    }

    public Camera getCam() {
        return cam;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    @Override
    public Matrix4 getProjViewTrans() {
        return cam.combined;
    }

    @Override
    public TextureDescriptor getDepthMap() {
        textureDesc.texture = fbo.getColorBufferTexture();
        return textureDesc;
    }
}
