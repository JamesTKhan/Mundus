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

package com.mbrlabs.mundus.editor.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.shaders.TerrainShader;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import com.mbrlabs.mundus.editor.shader.EditorTerrainUberShader;

/**
 * Legacy Terrain shader replaced by {@link EditorTerrainUberShader} shader
 */
@Deprecated
public class EditorTerrainShader extends TerrainShader {

    // ============================ MOUSE PICKER ============================
    protected final int UNIFORM_PICKER_POS = register(new Uniform("u_pickerPos"));
    protected final int UNIFORM_PICKER_RADIUS = register(new Uniform("u_pickerRadius"));
    protected final int UNIFORM_MOUSE_ACTIVE = register(new Uniform("u_pickerActive"));

    private boolean pickerActive = false;
    private final Vector3 pickerPosition = new Vector3();
    private float pickerRadius = 0;

    public EditorTerrainShader() {
        String vertexShader = "\n#define PICKER\n" + Gdx.files.classpath(VERTEX_SHADER).readString();
        String fragmentShader = "\n#define PICKER\n" + Gdx.files.classpath(FRAGMENT_SHADER).readString();

        program = new ShaderProgram(vertexShader, ShaderUtils.getShaderPrefix(this) + fragmentShader);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
    }

    @Override
    public void render(Renderable renderable) {
        // mouse picking
        if(pickerActive) {
            set(UNIFORM_MOUSE_ACTIVE, 1);
            set(UNIFORM_PICKER_POS, pickerPosition);
            set(UNIFORM_PICKER_RADIUS, pickerRadius);
        } else {
            set(UNIFORM_MOUSE_ACTIVE, 0);
        }

        super.render(renderable);
    }

    public void activatePicker(boolean active) {
        pickerActive = active;
    }

    public void setPickerPosition(float x, float y, float z) {
        pickerPosition.set(x, y, z);
    }

    public void setPickerRadius(float radius) {
        pickerRadius = radius;
    }

}
