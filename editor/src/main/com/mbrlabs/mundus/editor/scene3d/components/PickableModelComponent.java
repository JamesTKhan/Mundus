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

package com.mbrlabs.mundus.editor.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.picker.PickerColorEncoder;
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;

/**
 * @author Marcus Brummer
 * @version 27-10-2016
 */
public class PickableModelComponent extends ModelComponent implements PickableComponent {

    public PickableModelComponent(GameObject go) {
        super(go);
    }

    @Override
    public void encodeRaypickColorId() {
        PickerIDAttribute goIDa = PickerColorEncoder.encodeRaypickColorId(gameObject);
        for (Material material : modelInstance.materials) {
            material.set(goIDa);
        }
    }

    @Override
    public void renderPick() {
        gameObject.sceneGraph.scene.batch.render(modelInstance, Shaders.INSTANCE.getPickerShader());
    }

    @Override
    public Component clone(GameObject go) {
        PickableModelComponent mc = new PickableModelComponent(go);
        mc.modelAsset = this.modelAsset;
        mc.setModel(new ModelInstance(modelAsset.getModel()));
        mc.materials.putAll(this.materials);
        mc.setUseModelCache(useModelCache);
        mc.encodeRaypickColorId();
        gameObject.sceneGraph.scene.modelCacheManager.requestModelCacheRebuild();
        return mc;
    }
}
