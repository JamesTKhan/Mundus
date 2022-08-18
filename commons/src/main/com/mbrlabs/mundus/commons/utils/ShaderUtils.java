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

package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.shaders.LightShader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

/**
 * @author Marcus Brummer
 * @version 23-11-2015
 */
public class ShaderUtils {

    protected static final String LIGHT_SHADER_PREFIX = "com/mbrlabs/mundus/commons/shaders/light.glsl";

    /**
     * Compiles and links shader.
     *
     * @param vertexShader
     *            path to vertex shader
     * @param fragmentShader
     *            path to fragment shader
     * @param shader
     *            the shader to compile a program for
     * @param customPrefix
     *             a custom prefix string to prepend to vertex and fragment shaders
     *
     * @return compiled shader program
     */
    public static ShaderProgram compile(String vertexShader, String fragmentShader, Shader shader, String customPrefix) {
        String vert;
        String frag;

        if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            vert = Gdx.files.internal(vertexShader).readString();
            frag = Gdx.files.internal(fragmentShader).readString();
        } else {
            vert = Gdx.files.classpath(vertexShader).readString();
            frag = Gdx.files.classpath(fragmentShader).readString();
        }

        ShaderProgram program = new ShaderProgram(customPrefix + vert,  getShaderPrefix(shader) + customPrefix + frag);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }

        return program;
    }

    /**
     * Compiles and links shader.
     *
     * @param vertexShader
     *            path to vertex shader
     * @param fragmentShader
     *            path to fragment shader
     * @param shader
     *            the shader to compile a program for
     *
     * @return compiled shader program
     */
    public static ShaderProgram compile(String vertexShader, String fragmentShader, Shader shader) {
        return compile(vertexShader, fragmentShader, shader, "");
    }

    public static String getShaderPrefix(Shader shader) {
        String fragPrefix = "";

        if (shader instanceof LightShader) {
            fragPrefix += "#define numPointLights " + LightUtils.MAX_POINT_LIGHTS + "\n";
            fragPrefix += "#define numSpotLights " + LightUtils.MAX_SPOT_LIGHTS + "\n";

            if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
                fragPrefix += Gdx.files.internal(LIGHT_SHADER_PREFIX).readString();
            } else {
                fragPrefix +=  Gdx.files.classpath(LIGHT_SHADER_PREFIX).readString();
            }
        }

        return fragPrefix;
    }

    /**
     * Builds and returns a PBRShaderConfig.
     *
     * @param numBones the numBones to use for the config
     */
    public static PBRShaderConfig buildPBRShaderConfig(int numBones) {
        // Create and initialize PBR config
        PBRShaderConfig config = new PBRShaderConfig();
        config.numDirectionalLights = 1;
        config.numPointLights = LightUtils.MAX_POINT_LIGHTS;
        config.numSpotLights = LightUtils.MAX_SPOT_LIGHTS;
        config.numBones = numBones;
        config.defaultCullFace = GL20.GL_BACK;
        config.vertexShader = Gdx.files.classpath("com/mbrlabs/mundus/commons/shaders/custom-gdx-pbr.vs.glsl").readString();
        config.fragmentShader = Gdx.files.classpath("com/mbrlabs/mundus/commons/shaders/custom-gdx-pbr.fs.glsl").readString();
        return config;
    }

    /**
     * Builds and returns a DepthShader.Config.
     *
     * @param numBones the numBones to use for the config
     */
    public static DepthShader.Config buildPBRShaderDepthConfig(int numBones) {
        // Create and initialize PBR config
        DepthShader.Config depthConfig = PBRShaderProvider.createDefaultDepthConfig();
        depthConfig.numBones = numBones;
        depthConfig.defaultCullFace = GL20.GL_BACK;
        return depthConfig;
    }

}
