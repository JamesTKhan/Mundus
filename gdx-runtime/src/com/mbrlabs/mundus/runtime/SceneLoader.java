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

package com.mbrlabs.mundus.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.SkyboxAsset;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent;
import com.mbrlabs.mundus.runtime.converter.SceneConverter;

/**
 * @author Marcus Brummer
 * @version 27-10-2016
 */
public class SceneLoader {
    private static final String TAG = SceneLoader.class.getSimpleName();

    private final Mundus mundus;
    private final AssetManager assetManager;

    private final FileHandle root;

    public SceneLoader(Mundus mundus, FileHandle scenesRoot) {
        this.mundus = mundus;
        this.assetManager = mundus.getAssetManager();
        this.root = scenesRoot;
    }

    public Scene load(String name) {
        Json json = new Json();

        // Pass string using readString() instead of FileHandle to support GWT
        SceneDTO sceneDTO = json.fromJson(SceneDTO.class, root.child(name).readString());

        Scene scene =  SceneConverter.convert(sceneDTO, mundus.getShaders(), assetManager);

        // Setup skybox
        if (scene.skyboxAssetId != null) {
            SkyboxAsset skyboxAsset = (SkyboxAsset) assetManager.findAssetByID(scene.skyboxAssetId);
            scene.setSkybox(skyboxAsset, mundus.getShaders().getSkyboxShader());
        }

        scene.setDepthShader(mundus.getShaders().getDepthShader());

        SceneGraph sceneGraph = scene.sceneGraph;
        for (GameObject go : sceneGraph.getGameObjects()) {
            initGameObject(go);
        }

        return scene;
    }

    private void initGameObject(GameObject root) {
        initComponents(root);
        if (root.getChildren() != null) {
            for (GameObject c : root.getChildren()) {
                initGameObject(c);
            }
        }
    }

    private void initComponents(GameObject go) {
        Array<ModelAsset> models = assetManager.getModelAssets();
        Array.ArrayIterator<Component> iterator = go.getComponents().iterator();
        while(iterator.hasNext()) {
            Component c = iterator.next();
            if (c == null) {
                iterator.remove();
                Gdx.app.error(TAG, "Error loading a component. Skipping component in object " + go.name);
                continue;
            }
            // Model component
            if (c.getType() == Component.Type.MODEL) {
                ModelComponent modelComponent = (ModelComponent) c;
                ModelAsset model = findModelById(models, modelComponent.getModelAsset().getID());
                if (model != null) {
                    modelComponent.setModel(model, false);
                } else {
                    Gdx.app.error(TAG, "Could not find model for instance: " + modelComponent.getModelAsset().getID());
                }
            } else if (c.getType() == Component.Type.WATER) {
                ((WaterComponent) c).getWaterAsset().water.setTransform(go.getTransform());
            }

        }
    }

    private ModelAsset findModelById(Array<ModelAsset> models, String id) {
        for (ModelAsset m : models) {
            if (m.getID().equals(id)) {
                return m;
            }
        }

        return null;
    }

}
