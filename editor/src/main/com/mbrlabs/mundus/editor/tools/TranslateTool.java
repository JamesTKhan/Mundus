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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.TranslateCommand;
import com.mbrlabs.mundus.editor.preferences.MundusPreferencesManager;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.utils.Fa;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class TranslateTool extends TransformTool {

    private static final float ARROW_THIKNESS = 0.4f;
    private static final float ARROW_CAP_SIZE = 0.15f;
    private static final int ARROW_DIVISIONS = 12;

    public static final String NAME = "Translate Tool";

    private TransformState state = TransformState.IDLE;
    private boolean initTranslate = true;

    private final TranslateHandle xHandle;
    private final TranslateHandle yHandle;
    private final TranslateHandle zHandle;
    private final TranslateHandle xzPlaneHandle;
    private final TranslateHandle[] handles;

    private final Vector3 lastPos = new Vector3();
    private boolean globalSpace = true;

    private final Vector3 temp0 = new Vector3();
    private final Vector3 temp1 = new Vector3();
    private final Matrix4 tempMat0 = new Matrix4();

    private TranslateCommand command;

    public TranslateTool(final ProjectManager projectManager,
                         final GameObjectPicker goPicker,
                         final ToolHandlePicker handlePicker,
                         final CommandHistory history,
                         final MundusPreferencesManager globalPreferencesManager) {
        super(projectManager, goPicker, handlePicker, history, globalPreferencesManager);

        ModelBuilder modelBuilder = new ModelBuilder();

        Model xHandleModel = modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(PBRColorAttribute.createBaseColorFactor(COLOR_X)),
                VertexAttributes.Usage.Position);
        Model yHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(PBRColorAttribute.createBaseColorFactor(COLOR_Y)),
                VertexAttributes.Usage.Position);
        Model zHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE, ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(PBRColorAttribute.createBaseColorFactor(COLOR_Z)),
                VertexAttributes.Usage.Position);
        Model xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
                new Material(PBRColorAttribute.createBaseColorFactor(COLOR_XZ)), VertexAttributes.Usage.Position);

        xHandle = new TranslateHandle(X_HANDLE_ID, xHandleModel);
        yHandle = new TranslateHandle(Y_HANDLE_ID, yHandleModel);
        zHandle = new TranslateHandle(Z_HANDLE_ID, zHandleModel);
        xzPlaneHandle = new TranslateHandle(XZ_HANDLE_ID, xzPlaneHandleModel);
        handles = new TranslateHandle[] { xHandle, yHandle, zHandle, xzPlaneHandle };

        gameObjectModifiedEvent = new GameObjectModifiedEvent(null);
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
        return Fa.Companion.getARROWS();
    }

    @Override
    public void gameObjectSelected(GameObject go) {
        super.gameObjectSelected(go);
        scaleHandles();
        translateHandles();
    }

    public void setGlobalSpace(boolean global) {
        this.globalSpace = global;
        xHandle.getRotation().idt();
        xHandle.applyTransform();

        yHandle.getRotation().idt();
        yHandle.applyTransform();

        zHandle.getRotation().idt();
        zHandle.applyTransform();
    }

    @Override
    public void render() {
        super.render();
        if (getProjectManager().current().currScene.currentSelection != null) {
            getProjectManager().getModelBatch().begin(getProjectManager().current().currScene.cam);
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            xHandle.render(getProjectManager().getModelBatch());
            yHandle.render(getProjectManager().getModelBatch());
            zHandle.render(getProjectManager().getModelBatch());
            xzPlaneHandle.render(getProjectManager().getModelBatch());

            getProjectManager().getModelBatch().end();
        }
    }

    @Override
    public void act() {
        super.act();

        if (getProjectManager().current().currScene.currentSelection != null) {
            translateHandles();
            scaleHandles();

            if (state == TransformState.IDLE) return;

            Ray ray = getProjectManager().current().currScene.viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Vector3 rayEnd = getProjectManager().current().currScene.currentSelection.getPosition(temp0);
            float dst = getProjectManager().current().currScene.cam.position.dst(rayEnd);
            rayEnd = ray.getEndPoint(rayEnd, dst);

            if (initTranslate) {
                initTranslate = false;
                lastPos.set(rayEnd);
            }

            GameObject go = getProjectManager().current().currScene.currentSelection;

            boolean modified = false;
            Vector3 vec = new Vector3();
            if (state == TransformState.TRANSFORM_XZ) {
                vec.set(rayEnd.x - lastPos.x, 0, rayEnd.z - lastPos.z);
                modified = true;
            } else if (state == TransformState.TRANSFORM_X) {
                vec.set(rayEnd.x - lastPos.x, 0, 0);
                modified = true;
            } else if (state == TransformState.TRANSFORM_Y) {
                vec.set(0, rayEnd.y - lastPos.y, 0);
                modified = true;
            } else if (state == TransformState.TRANSFORM_Z) {
                vec.set(0, 0, rayEnd.z - lastPos.z);
                modified = true;
            }

            if (go.getParent() != null) {
                // First, get the world transform from parent and apply translation
                Matrix4 worldTrans = tempMat0.set(go.getParent().getTransform());
                worldTrans.trn(vec.scl(-1)); // I believe we have to scale this by -1 due to inv()

                // Convert that new translation from world to local space for child
                Matrix4 localTrans = go.getTransform().mulLeft(worldTrans.inv());
                Vector3 localPos = localTrans.getTranslation(temp1);

                // apply position
                go.setLocalPosition(localPos.x, localPos.y, localPos.z);
            } else {
                go.translate(vec);
            }

            // If a water component height is changed, global water height needs to update
            if (go.findComponentByType(Component.Type.WATER) != null)
                getProjectManager().current().currScene.settings.waterHeight = go.getPosition(temp1).y;

            if (modified) {
                gameObjectModifiedEvent.setGameObject(getProjectManager().current().currScene.currentSelection);
                Mundus.INSTANCE.postEvent(gameObjectModifiedEvent);
            }

            lastPos.set(rayEnd);
        }
    }

    @Override
    protected void scaleHandles() {
        Vector3 pos = getProjectManager().current().currScene.currentSelection.getPosition(temp0);
        float scaleFactor = getProjectManager().current().currScene.cam.position.dst(pos) * 0.25f;
        xHandle.getScale().set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        xHandle.applyTransform();

        yHandle.getScale().set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        yHandle.applyTransform();

        zHandle.getScale().set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        zHandle.applyTransform();

        xzPlaneHandle.getScale().set(scaleFactor * 0.13f, scaleFactor * 0.13f, scaleFactor * 0.13f);
        xzPlaneHandle.applyTransform();
    }

    @Override
    protected void translateHandles() {
        final Vector3 pos = getProjectManager().current().currScene.currentSelection.getTransform()
                .getTranslation(temp0);
        xHandle.getPosition().set(pos);
        xHandle.applyTransform();
        yHandle.getPosition().set(pos);
        yHandle.applyTransform();
        zHandle.getPosition().set(pos);
        zHandle.applyTransform();
        xzPlaneHandle.getPosition().set(pos);
        xzPlaneHandle.applyTransform();
    }

    @Override
    protected void rotateHandles() {
        // no rotation for this one
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isSelectWithRightButton()) {
            super.touchDown(screenX, screenY, pointer, button);
        }

        if (button == Input.Buttons.LEFT && getProjectManager().current().currScene.currentSelection != null) {
            TranslateHandle handle = (TranslateHandle) handlePicker.pick(handles,
                    getProjectManager().current().currScene, screenX, screenY);
            if (handle == null) {
                state = TransformState.IDLE;
                if (!isSelectWithRightButton()) {
                    super.touchDown(screenX, screenY, pointer, button);
                }
                return false;
            }

            if (handle.getId() == XZ_HANDLE_ID) {
                state = TransformState.TRANSFORM_XZ;
                initTranslate = true;
                xzPlaneHandle.changeColor(COLOR_SELECTED);
            } else if (handle.getId() == X_HANDLE_ID) {
                state = TransformState.TRANSFORM_X;
                initTranslate = true;
                xHandle.changeColor(COLOR_SELECTED);
            } else if (handle.getId() == Y_HANDLE_ID) {
                state = TransformState.TRANSFORM_Y;
                initTranslate = true;
                yHandle.changeColor(COLOR_SELECTED);
            } else if (handle.getId() == Z_HANDLE_ID) {
                state = TransformState.TRANSFORM_Z;
                initTranslate = true;
                zHandle.changeColor(COLOR_SELECTED);
            }
        }

        if (state != TransformState.IDLE) {
            command = new TranslateCommand(getProjectManager().current().currScene.currentSelection);
            command.setBefore(getProjectManager().current().currScene.currentSelection.getLocalPosition(temp0));
        } else if (!isSelectWithRightButton()) {
            super.touchDown(screenX, screenY, pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (state != TransformState.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);
            xzPlaneHandle.changeColor(COLOR_XZ);

            command.setAfter(getProjectManager().current().currScene.currentSelection.getLocalPosition(temp0));
            getHistory().add(command);
            command = null;
            state = TransformState.IDLE;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
        xzPlaneHandle.dispose();
    }

    /**
     * 
     */
    private class TranslateHandle extends ToolHandle {

        private final Model model;
        private final ModelInstance modelInstance;

        public TranslateHandle(int id, Model model) {
            super(id);
            this.model = model;
            this.modelInstance = new ModelInstance(model);
            modelInstance.materials.first().set(getIdAttribute());
        }

        public void changeColor(Color color) {
            PBRColorAttribute diffuse = (PBRColorAttribute) modelInstance.materials.get(0).get(PBRColorAttribute.BaseColorFactor);
            diffuse.color.set(color);
        }

        @Override
        public void render(ModelBatch batch) {
            batch.render(modelInstance, getEnvironment());
        }

        @Override
        public void renderPick(ModelBatch modelBatch) {
            getProjectManager().getModelBatch().render(modelInstance, Shaders.INSTANCE.getPickerShader());
        }

        @Override
        public void act() {

        }

        @Override
        public void applyTransform() {
            getRotation().setEulerAngles(getRotationEuler().y, getRotationEuler().x, getRotationEuler().z);
            modelInstance.transform.set(getPosition(), getRotation(), getScale());
        }

        @Override
        public void dispose() {
            this.model.dispose();
        }

    }

}
