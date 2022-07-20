/*
 * Copyright (c) 2022. See AUTHORS file.
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

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

import static com.mbrlabs.mundus.commons.utils.ModelUtils.isVisible;

/**
 * Components that can be Culled via Frustum Culling should extend
 * this class and call setDimensions once they have access to a modelInstance as well as super
 * for render() and update()
 *
 * The isCulled value will be set accordingly and components can check if is isCulled == true
 * before rendering.
 *
 * @author JamesTKhan
 * @version July 18, 2022
 */
public abstract class CullableComponent extends AbstractComponent {
    private final static BoundingBox tmpBounds = new BoundingBox();
    private final static Quaternion quaternion = new Quaternion();
    private final static Vector3 tmpScale = new Vector3();

    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public float radius;

    // Is it offscreen?
    protected boolean isCulled = false;
    private ModelInstance modelInstance = null;

    public CullableComponent(GameObject go) {
        super(go);
    }

    @Override
    public void render(float delta) {
        if (modelInstance == null) return;
        gameObject.getLocalRotation(quaternion);

        if (quaternion.isIdentity()) {
            // If it's not rotated, we use boundsInFrustum which seems to kick in quicker on terrains and water
            // since it's not taking all possible rotations of the object into account
            isCulled = !isVisible(gameObject.sceneGraph.scene.cam, modelInstance, center, dimensions);
        } else {
            isCulled = !isVisible(gameObject.sceneGraph.scene.cam, modelInstance, center, radius);
        }

    }

    @Override
    public void update(float delta) {
        if (gameObject.scaleChanged) {
            setDimensions(modelInstance);
        }
    }

    protected void setDimensions(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        modelInstance.calculateBoundingBox(tmpBounds);
        tmpBounds.getCenter(center);
        tmpBounds.getDimensions(dimensions);
        dimensions.scl(gameObject.getLocalScale(tmpScale));
        radius = dimensions.len() / 2f;
    }

    public boolean isCulled() {
        return isCulled;
    }
}
