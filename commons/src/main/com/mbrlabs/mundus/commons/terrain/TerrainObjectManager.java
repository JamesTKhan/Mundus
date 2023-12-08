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
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.TerrainObject;
import com.mbrlabs.mundus.commons.assets.TerrainObjectLayerAsset;
import com.mbrlabs.mundus.commons.assets.TerrainObjectsAsset;

public class TerrainObjectManager implements RenderableProvider {

    private final Array<ModelInstance> modelInstances;

    public TerrainObjectManager() {
        modelInstances = new Array<>(5);
    }

    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        for (int i = 0; i < modelInstances.size; ++i) {
            final ModelInstance modelInstance = modelInstances.get(i);
            modelInstance.getRenderables(renderables, pool);
        }
    }

    public void apply(final TerrainObjectsAsset terrainObjectsAsset, final TerrainObjectLayerAsset terrainObjectLayerAsset, final Matrix4 transform) {
        for (int i = 0; i < terrainObjectsAsset.getTerrainObjectNum(); ++i) {
            final TerrainObject terrainObject = terrainObjectsAsset.getTerrainObject(i);

            final String terrainObjectId = terrainObject.getId();

            if (!contains(terrainObjectId)) {
                final int layerPos = terrainObject.getLayerPos();
                final Vector3 localPosition = terrainObject.getPosition();

                final ModelAsset modelAsset = terrainObjectLayerAsset.getModels().get(layerPos);
                final Model model = modelAsset.getModel();

                final ModelInstance modelInstance = new ModelInstance(model);
                modelInstance.userData = terrainObjectId;
                modelInstance.transform.translate(localPosition);
                modelInstance.transform.mulLeft(transform);

                modelInstances.add(modelInstance);
            }
        }
    }

    private boolean contains(final String id) {
        for (int i = 0; i < modelInstances.size; ++i) {
            if (id.equals(modelInstances.get(i).userData)) {
                return true;
            }
        }

        return false;
    }

}
