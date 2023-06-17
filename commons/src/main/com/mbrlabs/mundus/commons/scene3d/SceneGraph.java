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
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class SceneGraph {

    protected GameObject root;

    public Scene scene;

    private boolean containsWater = false;

    public SceneGraph(Scene scene) {
        root = new GameObject(this, null, -1);
        root.initChildrenArray();
        root.active = false;
        this.scene = scene;
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

    /**
     * Adds a model to the scene graph to the given position.
     *
     * @param model The model.
     * @param position The position.
     * @return The game object of added model.
     */
    public GameObject addGameObject(final Model model, final Vector3 position) {
        return addGameObject(new ModelInstance(model), position);
    }

    /**
     * Adds a model instance to the scene graph to the given position.
     *
     * @param modelInstance The model instance.
     * @param position The position.
     * @return The game object of added model instance.
     */
    public GameObject addGameObject(final ModelInstance modelInstance, final Vector3 position) {
        final GameObject go = new GameObject(this, "", getNextId());

        go.translate(position);

        ModelComponent modelComponent = new ModelComponent(go);
        modelComponent.setModel(modelInstance);

        try {
            go.addComponent(modelComponent);
        } catch (final InvalidComponentException ex) {
            // Because we created a new game object which has not any component
            // this exception won't throw
        }

        root.addChild(go);

        return go;
    }

    private int getNextId() {
        int maxId = 0;

        for (final GameObject go : root.getChildren()) {
            if (go.id > maxId) {
                maxId = go.id;
            }
        }

        return maxId + 1;
    }

    /**
     * Returns the first GameObject in the scene matching the name.
     *
     * @param name the GameObject name to search for
     * @return the first GameObject found or null if not found
     */
    public GameObject findByName(String name) {
        return root.findChildByName(name);
    }

    /**
     * Returns an Array of all GameObjects in the scene matching the name.
     * Traversing the scene can be expensive, cache these results if you need them often.
     *
     * @param name the GameObject name to search for
     * @return Array of all matching GameObjects
     */
    public Array<GameObject> findAllByName(String name) {
        return root.findChildrenByName(name);
    }

    /**
     * Returns an Array of all GameObjects in the scene that have the given Component.Type
     * Traversing the scene can be expensive, cache these results if you need them often.
     *
     * @param type the Component Type to search for
     * @return Array of all matching GameObjects
     */
    public Array<GameObject> findAllByComponent(Component.Type type) {
        return root.findChildrenByComponent(type);
    }

    /**
     * Returns an Array of all scene GameObjects that have the given Tag
     * Traversing the scene can be expensive, cache these results if you need them often.
     *
     * @param tag the string tag to search for
     * @return Array of all matching GameObjects
     */
    public Array<GameObject> findAllByTag(String tag) {
        return root.findChildrenByTag(tag);
    }

    public GameObject getRoot() {
        return root;
    }

    public boolean isContainsWater() {
        return containsWater;
    }

    public void setContainsWater(boolean containsWater) {
        this.containsWater = containsWater;
    }
}
