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

package com.mbrlabs.mundus.editor.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.mbrlabs.mundus.commons.utils.DebugRenderer
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.preferences.MundusPreferencesManager
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
class ShortcutController(
    registry: Registry,
    private val projectManager: ProjectManager,
    private val history: CommandHistory,
    private val toolManager: ToolManager,
    private val debugRenderer: DebugRenderer,
    private val globalPrefManager: MundusPreferencesManager
)
    : KeyboardLayoutInputAdapter(registry) {

    private var isCtrlPressed = false

    /**
     * Updates here should also be reflected in the KeyboardShortcutsDialog
     */
    override fun keyDown(code: Int): Boolean {
        val keycode = convertKeycode(code)

        // export
        if(keycode == Input.Keys.F1) {
            UI.exportDialog.export()
            return true
        }

        // fullscreen
        if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
            UI.toggleFullscreenRender()
        }

        // CTR + xyz shortcuts

        if (keycode == Input.Keys.CONTROL_LEFT) {
            isCtrlPressed = true
        }
        if (!isCtrlPressed) return false

        if (keycode == Input.Keys.Z) {
            history.goBack()
            return true
        } else if (keycode == Input.Keys.Y) {
            history.goForward()
            return true
        } else if (keycode == Input.Keys.S) {
            projectManager.saveCurrentProject()
            UI.toaster.success("Project saved")
            return true
        } else if (keycode == Input.Keys.T) {
            toolManager.activateTool(toolManager.translateTool)
            UI.toolbar.updateActiveToolButton()
        } else if (keycode == Input.Keys.R) {
            toolManager.activateTool(toolManager.rotateTool)
            UI.toolbar.updateActiveToolButton()
        } else if (keycode == Input.Keys.G) {
            toolManager.activateTool(toolManager.scaleTool)
            UI.toolbar.updateActiveToolButton()
        } else if (keycode == Input.Keys.F) {
            toolManager.activateTool(toolManager.selectionTool)
            UI.toolbar.updateActiveToolButton()
        } else if (keycode == Input.Keys.F2) {
            debugRenderer.isEnabled = !debugRenderer.isEnabled
            globalPrefManager.set(MundusPreferencesManager.GLOB_BOOL_DEBUG_RENDERER_ON, debugRenderer.isEnabled)
        } else if (keycode == Input.Keys.F3) {
            projectManager.current().renderWireframe = !projectManager.current().renderWireframe
        }

        return false
    }

    override fun keyUp(code: Int): Boolean {
        val keycode = convertKeycode(code)
        if (keycode == Input.Keys.CONTROL_LEFT) {
            isCtrlPressed = false
        }
        return false
    }

}
