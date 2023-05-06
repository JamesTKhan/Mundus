package com.mbrlabs.mundus.commons.utils;

/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Originally From https://github.com/crykn/guacamole
 *
 * OpenGL utilities.
 *
 * @author damios
 */
public final class GLUtils {

    private GLUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * The buffer used internally. A size of 64 bytes is required as at most 16
     * integer elements can be returned.
     */
    private static final IntBuffer INT_BUFF = ByteBuffer
            .allocateDirect(64).order(ByteOrder.nativeOrder())
            .asIntBuffer();

    /**
     * Returns the name of the currently bound framebuffer
     * ({@code GL_FRAMEBUFFER_BINDING}).
     *
     * @return the name of the currently bound framebuffer; the initial value is
     *         {@code 0}, indicating the default framebuffer
     */
    public static synchronized int getBoundFboHandle() {
        IntBuffer intBuf = INT_BUFF;
        Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, intBuf);
        return intBuf.get(0);
    }

    /**
     * @return the current gl viewport ({@code GL_VIEWPORT}) as an array,
     *         containing four values: the x and y window coordinates of the
     *         viewport, followed by its width and height.
     */
    public static synchronized int[] getViewport() {
        IntBuffer intBuf = INT_BUFF;
        Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, intBuf);

        return new int[] { intBuf.get(0), intBuf.get(1), intBuf.get(2),
                intBuf.get(3) };
    }

    /**
     * Originally gdx-gltf library PBRShaderProvider
     *
     * @return if target platform is running with at least OpenGL ES 3 (GLSL 300 es), WebGL 2.0 (GLSL 300 es)
     *  or desktop OpenGL 3.0 (GLSL 130).
     */
    public static boolean isGL3(){
        return Gdx.graphics.getGLVersion().isVersionEqualToOrHigher(3, 0);
    }

}

