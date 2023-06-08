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

package com.mbrlabs.mundus.editor.core;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.events.ComponentAddedEvent;

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
public class EditorScene extends Scene {

    public Viewport viewport;
    public Array<TerrainComponent> terrains;
    public GameObject currentSelection;

    public EditorScene() {
        super();
        currentSelection = null;
        terrains = new Array<>();
        isRuntime = false;
    }

    @Override
    protected void initFrameBuffers(int width, int height) {
        fboWaterReflection = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
        fboWaterRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
        fboDepthRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
    }

    /**
     * Call this when the scene is loaded and is now the current active scene.
     */
    public void onLoaded() {
        // Post component added events for all components in the scene now that it is loaded
        for (GameObject gameObject : sceneGraph.getGameObjects()) {
            for (Component component : gameObject.getComponents()) {
                Mundus.INSTANCE.postEvent(new ComponentAddedEvent(component));
            }
        }
    }
}
