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

package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class SceneGraph {

    protected GameObject root;

    public Scene scene;

    private GameObject selected;

    private boolean containsWater = false;

    public SceneGraph(Scene scene) {
        root = new GameObject(this, null, -1);
        root.initChildrenArray();
        root.active = false;
        this.scene = scene;
    }

    public void render(float delta, Vector3 clippingPlane, float clipHeight) {
        for (GameObject go : root.getChildren()) {
            if (go.findComponentByType(Component.Type.WATER) != null)
                continue;
            go.render(delta, clippingPlane, clipHeight);
        }
    }

    //todo consider using renderable sorter instead
    public void renderWater(float delta, Texture reflectionTexture, Texture refraction) {
        for (GameObject go : root.getChildren()) {
            WaterComponent waterComponent = (WaterComponent) go.findComponentByType(Component.Type.WATER);
            if (waterComponent != null) {
                waterComponent.getWaterAsset().setWaterReflectionTexture(reflectionTexture);
                waterComponent.getWaterAsset().setWaterRefractionTexture(refraction);
                go.render(delta);
            }
        }
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        for (GameObject go : root.getChildren()) {
            go.update(delta);
        }
    }

    public Array<GameObject> getGameObjects() {
        return root.getChildren();
    }

    public void addGameObject(GameObject go) {
        root.addChild(go);

        if (containsWater) return;

        Component waterComponent = go.findComponentByType(Component.Type.WATER);
        if (waterComponent != null) {
            containsWater = true;
        }
    }

    public GameObject getSelected() {
        return selected;
    }

    public void setSelected(GameObject selected) {
        this.selected = selected;
    }

    public boolean isContainsWater() {
        return containsWater;
    }

    public void setContainsWater(boolean containsWater) {
        this.containsWater = containsWater;
    }
}
