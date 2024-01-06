/*
 * Copyright (c) 2023. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TerrainObject;
import com.mbrlabs.mundus.commons.assets.TerrainObjectLayerAsset;
import com.mbrlabs.mundus.commons.assets.TerrainObjectsAsset;
import com.mbrlabs.mundus.commons.utils.Pools;

public class TerrainObjectManager implements RenderableProvider {

    private final Array<ModelInstance> modelInstances;

    private final ModelCache modelCache;

    public TerrainObjectManager() {
        modelInstances = new Array<>(5);
        modelCache = new ModelCache();
    }

    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        modelCache.getRenderables(renderables, pool);
    }

    public void apply(final boolean recreateAllObjects, final TerrainObjectsAsset terrainObjectsAsset, final TerrainObjectLayerAsset terrainObjectLayerAsset, final Matrix4 parentTransform) {
        removeModelInstances(recreateAllObjects, terrainObjectsAsset);
        addModelInstances(terrainObjectsAsset, terrainObjectLayerAsset);
        updatePositions(terrainObjectsAsset, parentTransform);

        modelCache.begin();
        modelCache.add(modelInstances);
        modelCache.end();
    }

    public void updatePositions(final TerrainObjectsAsset terrainObjectsAsset, final Matrix4 parentTransform) {
        for (int i = 0; i < terrainObjectsAsset.getTerrainObjectNum(); ++i) {
            final TerrainObject terrainObject = terrainObjectsAsset.getTerrainObject(i);
            final ModelInstance modelInstance = findById(terrainObject.getId());

            modelInstance.transform.idt();
            setupPositionScaleAndRotation(modelInstance, terrainObject, parentTransform);
        }
    }

    private void addModelInstances(final TerrainObjectsAsset terrainObjectsAsset, final TerrainObjectLayerAsset terrainObjectLayerAsset) {
        for (int i = 0; i < terrainObjectsAsset.getTerrainObjectNum(); ++i) {
            final TerrainObject terrainObject = terrainObjectsAsset.getTerrainObject(i);

            final String terrainObjectId = terrainObject.getId();

            if (!containsModelInstance(terrainObjectId)) {
                final int layerPos = terrainObject.getLayerPos();
                final ModelAsset modelAsset = terrainObjectLayerAsset.getModels().get(layerPos);
                final Model model = modelAsset.getModel();

                final ModelInstance modelInstance = new ModelInstance(model);
                modelInstance.userData = terrainObjectId;
                modelInstances.add(modelInstance);
            }
        }
    }

    private void setupPositionScaleAndRotation(final ModelInstance modelInstance, final TerrainObject terrainObject, final Matrix4 parentTransform) {
        final Vector3 localPosition = terrainObject.getPosition();
        final Vector3 rotate = terrainObject.getRotation();
        final Vector3 scale = terrainObject.getScale();

        modelInstance.transform.translate(localPosition);

        if (!rotate.isZero()) {
            final Quaternion rot = modelInstance.transform.getRotation(Pools.quaternionPool.obtain());
            rot.setEulerAngles(rotate.y, rotate.x, rotate.z);
            modelInstance.transform.rotate(rot);

            Pools.quaternionPool.free(rot);
        }

        if (!scale.isUnit()) {
            modelInstance.transform.scale(scale.x, scale.y, scale.z);
        }

        modelInstance.transform.mulLeft(parentTransform);
    }

    private void removeModelInstances(final boolean recreateAllObjects, final TerrainObjectsAsset terrainObjectsAsset) {
        for (int i = modelInstances.size - 1; i >= 0; --i) {
            final ModelInstance modelInstance = modelInstances.get(i);
            final String id = (String) modelInstance.userData;

            if (recreateAllObjects || !containsTerrainObject(id, terrainObjectsAsset)) {
                modelInstances.removeIndex(i);
            }
        }
    }

    private boolean containsModelInstance(final String id) {
        for (int i = 0; i < modelInstances.size; ++i) {
            if (id.equals(modelInstances.get(i).userData)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsTerrainObject(final String id, final TerrainObjectsAsset terrainObjectsAsset) {
        for (int i = 0; i < terrainObjectsAsset.getTerrainObjectNum(); ++i) {
            if (id.equals(terrainObjectsAsset.getTerrainObject(i).getId())) {
                return true;
            }
        }

        return false;
    }

    private ModelInstance findById(final String id) {
        for (int i = 0; i < modelInstances.size; ++i) {
            final ModelInstance modelInstance = modelInstances.get(i);
            if (modelInstance.userData.equals(id)) {
                return modelInstance;
            }
        }

        return null;
    }
}
