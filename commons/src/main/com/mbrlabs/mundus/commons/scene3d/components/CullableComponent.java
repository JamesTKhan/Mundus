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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.event.Event;
import com.mbrlabs.mundus.commons.event.EventType;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.ModelCacheable;
import com.mbrlabs.mundus.commons.scene3d.ModelEventable;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;

import static com.mbrlabs.mundus.commons.utils.ModelUtils.isVisible;

/**
 * Components that can be Culled via Frustum Culling should extend
 * this class and call setDimensions once they have access to a modelInstance as well as super
 * for render() and update()
 * The isCulled value will be set accordingly and components can check if is isCulled == true
 * before rendering.
 *
 * @author JamesTKhan
 * @version July 18, 2022
 */
public abstract class CullableComponent extends AbstractComponent implements ModelEventable {
    private final static BoundingBox tmpBounds = new BoundingBox();
    private final static Matrix4 tmpTransform = new Matrix4();
    private final static Quaternion tmpRotation = new Quaternion();
    private final static Vector3 tmpScale = new Vector3();
    private final static short frameCullCheckInterval = 15;
    protected final Vector3 dimensions = new Vector3();
    protected float radius;

    // Render calls since last cull check
    protected short framesSinceLastCullCheck = 0;

    // Is it offscreen?
    protected boolean isCulled = false;
    private Array<Event> events;
    private ModelInstance modelInstance = null;

    public CullableComponent(GameObject go) {
        super(go);
    }

    @Override
    public void update(float delta) {
        if (modelInstance == null) return;

        if (gameObject.transformChanged) {
            updateBoundingBox(modelInstance);
        }

        if (!gameObject.sceneGraph.scene.settings.useFrustumCulling) {
            isCulled = false;
            return;
        }

        // Cannot frustum cull model cache objects, no reason for perform calculations
        if (this instanceof ModelCacheable) {
            if (((ModelCacheable) this).shouldCache()) {
                isCulled = false;
                return;
            }
        }

        // If object is not culled, don't perform a cull check every frame, just in intervals
        // to reduce matrix calculations
        if (!isCulled && framesSinceLastCullCheck++ < frameCullCheckInterval) return;
        framesSinceLastCullCheck = 0;

        boolean visibleToPerspective;
        boolean visibleToShadowMap = false;

        Camera sceneCam = gameObject.sceneGraph.scene.cam;

        visibleToPerspective = isVisible(sceneCam, modelInstance, tmpTransform.getTranslation(new Vector3()), radius);

        // If not visible to main cam, check if it's visible to shadow map (to prevent shadows popping out)
        if (!visibleToPerspective) {
            if (gameObject.sceneGraph.scene.environment.shadowMap instanceof MundusDirectionalShadowLight) {
                MundusDirectionalShadowLight shadowLight = (MundusDirectionalShadowLight) gameObject.sceneGraph.scene.environment.shadowMap;
                visibleToShadowMap = isVisible(shadowLight.getCamera(), modelInstance, tmpTransform.getTranslation(new Vector3()), radius);
            }
        }

        isCulled = !visibleToPerspective && !visibleToShadowMap;
    }

    @Override
    public void addEvent(final Event event) {
        if (events == null) {
            events = new Array<>(1);
        }
        events.add(event);
    }

    @Override
    public void removeEvent(final Event event) {
        if (events == null) {
            events = new Array<>(1);
        }
        events.removeValue(event, true);
    }

    @Override
    public void triggerBeforeDepthRenderEvent() {
        triggerEvent(EventType.BEFORE_DEPTH_RENDER);
    }

    @Override
    public void triggerBeforeRenderEvent() {
        triggerEvent(EventType.BEFORE_RENDER);
    }

    protected void updateBoundingBox(ModelInstance modelInstance) {
        if (modelInstance == null) {
            Gdx.app.error("CullableComponent", "setDimensions called with null modelInstance");
            return;
        }
        modelInstance.calculateBoundingBox(tmpBounds);
        tmpTransform.set(gameObject.getTransform());
        gameObject.getScale(tmpScale);
        gameObject.getRotation(tmpRotation);
        tmpBounds.getDimensions(dimensions);
        dimensions.scl(tmpScale);
        tmpBounds.mul(new Matrix4().setToScaling(dimensions).mul(tmpTransform));
        tmpBounds.mul(new Matrix4().set(tmpRotation));
        radius = dimensions.len() / 2f;
    }

    private void triggerEvent(final EventType eventType) {
        if (events == null) {
            return;
        }

        for (int i = 0; i < events.size; ++i) {
            if (eventType == events.get(i).getType()) {
                events.get(i).action();
            }
        }
    }

    public Vector3 getCenter() {
        return tmpTransform.getTranslation(new Vector3());
    }

    public Vector3 getDimensions() {
        return dimensions;
    }

    public Vector3 getDimensions(Vector3 out){
        out.set(dimensions.cpy());
        return out;
    }
    public float getRadius() {
        return radius;
    }

    public boolean isCulled() {
        return isCulled;
    }
}
