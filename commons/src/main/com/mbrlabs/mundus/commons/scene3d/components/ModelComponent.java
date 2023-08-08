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
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.ObjectMap;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.ModelCacheable;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 17-01-2016
 */
public class ModelComponent extends CullableComponent implements AssetUsage, ModelCacheable, RenderableComponent {

    protected ModelAsset modelAsset;
    protected ModelInstance modelInstance;
    protected boolean useModelCache = false;

    protected ObjectMap<String, MaterialAsset> materials;  // g3db material id to material asset uuid

    public ModelComponent(GameObject go) {
        super(go);
        type = Type.MODEL;
        materials = new ObjectMap<>();
    }

    @Override
    public RenderableProvider getRenderableProvider() {
        return modelInstance;
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

        setDimensions(modelInstance);
    }

    public void setModel(final ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        modelInstance.transform = gameObject.getTransform();

        setDimensions(this.modelInstance);
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

    @Override
    public boolean shouldCache() {
        return useModelCache;
    }

    @Override
    public void setUseModelCache(boolean value) {
        useModelCache = value;
    }

    @Override
    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    @Override
    public Component clone(GameObject go) {
        ModelComponent mc = new ModelComponent(go);
        mc.modelAsset = this.modelAsset;
        mc.setModel(new ModelInstance(modelAsset.getModel()));
        mc.materials.putAll(this.materials);
        mc.setUseModelCache(useModelCache);
        gameObject.sceneGraph.scene.modelCacheManager.requestModelCacheRebuild();
        return mc;
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (Objects.equals(assetToCheck.getID(), modelAsset.getID()))
            return true;

        if (assetToCheck instanceof MaterialAsset) {
            if (materials.containsValue(assetToCheck,true)) {
                return true;
            }
        }

        if (assetToCheck instanceof TextureAsset) {
            // for each texture see if there is a match
            for (ObjectMap.Entry<String, MaterialAsset> next : materials) {
                if (next.value.usesAsset(assetToCheck)) {
                    return true;
                }
            }
        }

        return false;
    }
}
