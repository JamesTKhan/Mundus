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

package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.InvalidComponentException;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.scene3d.components.PickableModelComponent;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.ui.UI;
import com.mbrlabs.mundus.editor.utils.TerrainUtils;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class ModelPlacementTool extends Tool {

    public static final String NAME = "Placement Tool";
    public static Vector3 DEFAULT_ORIENTATION = Vector3.Z.cpy();

    private Vector3 tempV3 = new Vector3();

    private boolean shouldRespectTerrainSlope = false;

    // DO NOT DISPOSE THIS
    private ModelAsset model;
    private ModelInstance modelInstance;

    public ModelPlacementTool(ProjectManager projectManager, ModelBatch batch, CommandHistory history) {
        super(projectManager, batch, history);
        setShader(Shaders.INSTANCE.getModelShader());
        this.model = null;
        this.modelInstance = null;
    }

    public void setModel(ModelAsset model) {
        this.model = model;
        modelInstance = null;
        this.modelInstance = new ModelInstance(model.getModel());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Drawable getIcon() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIconFont() {
        throw new UnsupportedOperationException();
    }

    public boolean isShouldRespectTerrainSlope() {
        return shouldRespectTerrainSlope;
    }

    public void setShouldRespectTerrainSlope(boolean shouldRespectTerrainSlope) {
        this.shouldRespectTerrainSlope = shouldRespectTerrainSlope;
    }

    @Override
    public void render() {
        if (modelInstance != null) {
            getBatch().begin(getProjectManager().current().currScene.cam);
            // TODO For now we have disabled custom shader or model, should we allow option for custom shader?
            //getBatch().render(modelInstance, getProjectManager().current().currScene.environment, getShader());
            getBatch().render(modelInstance, getProjectManager().current().currScene.environment);
            getBatch().end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (modelInstance != null && button == Input.Buttons.LEFT) {
            int id = getProjectManager().current().obtainID();
            GameObject modelGo = new GameObject(getProjectManager().current().currScene.sceneGraph, model.getName(),
                    id);
            getProjectManager().current().currScene.sceneGraph.addGameObject(modelGo);

            modelInstance.transform.getTranslation(tempV3);
            modelGo.translate(tempV3);

            PickableModelComponent modelComponent = new PickableModelComponent(modelGo, Shaders.INSTANCE.getModelShader());
            modelComponent.setShader(getShader());
            modelComponent.setModel(model, true);
            modelComponent.encodeRaypickColorId();

            try {
                modelGo.addComponent(modelComponent);
            } catch (InvalidComponentException e) {
                Dialogs.showErrorDialog(UI.INSTANCE, e.getMessage());
                return false;
            }

            Mundus.INSTANCE.postEvent(new SceneGraphChangedEvent());
            mouseMoved(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (this.model == null || modelInstance == null) return false;

        final ProjectContext context = getProjectManager().current();

        final Ray ray = getProjectManager().current().currScene.viewport.getPickRay(screenX, screenY);
        if (context.currScene.terrains.size > 0 && modelInstance != null) {
            MeshPartBuilder.VertexInfo vi = TerrainUtils.getRayIntersectionAndUp(context.currScene.terrains, ray);
            if (vi != null) {
                if (shouldRespectTerrainSlope) {
                    modelInstance.transform.setToLookAt(DEFAULT_ORIENTATION, vi.normal);
                }
                modelInstance.transform.setTranslation(vi.position);
            }
        } else {
            tempV3.set(getProjectManager().current().currScene.cam.position);
            tempV3.add(ray.direction.nor().scl(200));
            modelInstance.transform.setTranslation(tempV3);
        }

        return false;
    }

    @Override
    public void dispose() {
        this.model = null;
        this.modelInstance = null;
    }

    @Override
    public void onActivated() {

    }

    @Override
    public void onDisabled() {
        dispose();
    }

}
