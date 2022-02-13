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

package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.ObjectMap;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * @author Marcus Brummer
 * @version 17-01-2016
 */
public class ModelComponent extends AbstractComponent {

    protected ModelAsset modelAsset;
    protected ModelInstance modelInstance;
    protected Shader shader;

    protected ObjectMap<String, MaterialAsset> materials;  // g3db material id to material asset uuid

    public ModelComponent(GameObject go, Shader shader) {
        super(go);
        type = Type.MODEL;
        materials = new ObjectMap<String, MaterialAsset>();
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public void setModel(ModelAsset model, boolean inheritMaterials) {
        this.modelAsset = model;
        modelInstance = new ModelInstance(model.getModel());
        modelInstance.transform = gameObject.getTransform();

        // apply default materials of model
        if (inheritMaterials) {
            for (String g3dbMatID : model.getDefaultMaterials().keySet()) {
                materials.put(g3dbMatID, model.getDefaultMaterials().get(g3dbMatID));
            }
        }
        applyMaterials();
    }

    public ObjectMap<String, MaterialAsset> getMaterials() {
        return materials;
    }

    public ModelAsset getModelAsset() {
        return modelAsset;
    }

    public void applyMaterials() {
        for (Material mat : modelInstance.materials) {
            MaterialAsset materialAsset = materials.get(mat.id);
            if (materialAsset == null) continue;

            materialAsset.applyToMaterial(mat);
        }
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    @Override
    public void render(float delta) {
        modelInstance.transform.set(gameObject.getTransform());
        gameObject.sceneGraph.batch.render(modelInstance, gameObject.sceneGraph.scene.environment);
        // TODO For now we have disabled custom shader or model, should we allow option for custom shader?
        //gameObject.sceneGraph.batch.render(modelInstance, gameObject.sceneGraph.scene.environment, shader);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Component clone(GameObject go) {
        ModelComponent mc = new ModelComponent(go, shader);
        mc.modelAsset = this.modelAsset;
        mc.modelInstance = new ModelInstance(modelAsset.getModel());
        mc.shader = this.shader;
        return mc;
    }

}
