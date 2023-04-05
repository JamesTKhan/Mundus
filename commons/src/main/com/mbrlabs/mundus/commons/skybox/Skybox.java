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

package com.mbrlabs.mundus.commons.skybox;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.shaders.SkyboxShader;

/**
 * @author Marcus Brummer
 * @version 08-01-2016
 */
public class Skybox implements Disposable {
    public static final boolean DEFAULT_ROTATE_ENABLED = true;
    public static final float DEFAULT_ROTATE_SPEED = 0.1f;

    public Shader shader;
    public boolean active;

    private Model boxModel;
    private ModelInstance boxInstance;

    private Cubemap cubemap;
    private FileHandle positiveX;
    private FileHandle negativeX;
    private FileHandle positiveY;
    private FileHandle negativeY;
    private FileHandle positiveZ;
    private FileHandle negativeZ;

    private boolean rotateEnabled;
    private float rotateSpeed;

    public Skybox(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY,
            FileHandle positiveZ, FileHandle negativeZ, Shader shader) {
        set(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);

        this.active = true;
        this.shader = shader;
        boxModel = createModel();
        boxInstance = new ModelInstance(boxModel);
    }

    private Model createModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(1, 1, 1,
                new Material(new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap)),
                VertexAttributes.Usage.Position);
        return model;
    }

    public ModelInstance getSkyboxInstance() {
        return boxInstance;
    }

    public void set(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY,
            FileHandle positiveZ, FileHandle negativeZ) {
        if (cubemap != null) {
            cubemap.dispose();
        }
        cubemap = new Cubemap(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
        this.positiveX = positiveX;
        this.negativeX = negativeX;
        this.positiveY = positiveY;
        this.negativeY = negativeY;
        this.positiveZ = positiveZ;
        this.negativeZ = negativeZ;
    }

    public FileHandle getPositiveX() {
        return positiveX;
    }

    public FileHandle getNegativeX() {
        return negativeX;
    }

    public FileHandle getPositiveY() {
        return positiveY;
    }

    public FileHandle getNegativeY() {
        return negativeY;
    }

    public FileHandle getPositiveZ() {
        return positiveZ;
    }

    public FileHandle getNegativeZ() {
        return negativeZ;
    }

    public boolean isRotateEnabled() {
        return rotateEnabled;
    }

    public void setRotateEnabled(boolean rotateEnabled) {
        this.rotateEnabled = rotateEnabled;
        if (shader instanceof SkyboxShader)
            ((SkyboxShader) shader).setRotateEnabled(rotateEnabled);
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
        if (shader instanceof SkyboxShader)
            ((SkyboxShader) shader).setRotateSpeed(rotateSpeed);
    }


    @Override
    public void dispose() {
        boxModel.dispose();
        cubemap.dispose();
    }

}
