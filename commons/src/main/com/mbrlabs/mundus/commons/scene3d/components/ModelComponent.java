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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.ModelCacheable;
import com.mbrlabs.mundus.commons.shaders.ClippableShader;
import com.mbrlabs.mundus.commons.shaders.ShadowMapShader;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 17-01-2016
 */
public class ModelComponent extends CullableComponent implements AssetUsage, ClippableComponent, ModelCacheable {

    protected ModelAsset modelAsset;
    protected ModelInstance modelInstance;
    protected Shader shader;
    protected boolean useModelCache = false;

    protected ObjectMap<String, MaterialAsset> materials;  // g3db material id to material asset uuid

    public ModelComponent(GameObject go) {
        super(go);
        type = Type.MODEL;
        materials = new ObjectMap<>();
    }

    public ModelComponent(GameObject go, Shader shader) {
        super(go);
        type = Type.MODEL;
        materials = new ObjectMap<>();
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
    public void render(float delta) {
        // Update transform before checking if culled
        modelInstance.transform.set(gameObject.getTransform());

        super.render(delta);

        if (isCulled || useModelCache) return;

        triggerBeforeRenderEvent();
        if (shader != null) {
            gameObject.sceneGraph.scene.batch.render(modelInstance, gameObject.sceneGraph.scene.environment, shader);
        } else {
            gameObject.sceneGraph.scene.batch.render(modelInstance, gameObject.sceneGraph.scene.environment);
        }
    }

    @Override
    public void renderDepth(float delta, Vector3 clippingPlane, float clipHeight, Shader depthShader) {
        if (isCulled || useModelCache) return;

        if (depthShader instanceof ClippableShader) {
            ((ClippableShader) depthShader).setClippingPlane(clippingPlane);
            ((ClippableShader) depthShader).setClippingHeight(clipHeight);
        }

        triggerBeforeDepthRenderEvent();

        if (depthShader instanceof ShadowMapShader)
            // Shadow Mapper will use default (PBR's depth shader) for animation support
            gameObject.sceneGraph.scene.depthBatch.render(modelInstance, gameObject.sceneGraph.scene.environment);
        else {
            // Otherwise, use the mundus depth shader (using PBR Depth shader causes odd foam artifacts and issues).
            gameObject.sceneGraph.scene.depthBatch.render(modelInstance, gameObject.sceneGraph.scene.environment, depthShader);
        }
    }

    @Override
    public void render(float delta, Vector3 clippingPlane, float clipHeight) {
        if (shader != null && shader instanceof ClippableShader) {
            ((ClippableShader) shader).setClippingPlane(clippingPlane);
            ((ClippableShader) shader).setClippingHeight(clipHeight);
        } else {
            // For use with PBR shader
            gameObject.sceneGraph.scene.environment.setClippingHeight(clipHeight);
            gameObject.sceneGraph.scene.environment.getClippingPlane().set(clippingPlane);
        }

        render(delta);
    }

    @Override
    public Component clone(GameObject go) {
        ModelComponent mc = new ModelComponent(go, shader);
        mc.modelAsset = this.modelAsset;
        mc.modelInstance = new ModelInstance(modelAsset.getModel());
        mc.shader = this.shader;
        mc.materials.putAll(this.materials);
        mc.setDimensions(mc.modelInstance);
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
