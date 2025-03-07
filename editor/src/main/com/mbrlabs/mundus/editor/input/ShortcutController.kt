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

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.mbrlabs.mundus.commons.utils.DebugRenderer
import com.mbrlabs.mundus.editor.core.keymap.KeyboardShortcutManager
import com.mbrlabs.mundus.editor.core.keymap.KeymapKey
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.preferences.MundusPreferencesManager
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.KeyboardLayoutUtils

/**
 * @author Marcus Brummer
 * @version 07-02-2016
 */
class ShortcutController(
    private val projectManager: ProjectManager,
    private val history: CommandHistory,
    private val toolManager: ToolManager,
    private val debugRenderer: DebugRenderer,
    private val globalPrefManager: MundusPreferencesManager,
    private val shortcutManager: KeyboardShortcutManager
) : InputAdapter() {

    /**
     * Updates here should also be reflected in the KeyboardShortcutsDialog
     */
    override fun keyDown(code: Int): Boolean {
        val keycode = KeyboardLayoutUtils.convertKeycode(code)

        // export
        if(keycode == Input.Keys.F1) {
            UI.exportDialog.export()
            return true
        }

        // fullscreen
        if (shortcutManager.isPressed(KeymapKey.FULLSCREEN)) {
            UI.toggleFullscreenRender()
        } else if (shortcutManager.isPressed(KeymapKey.UNDO)) {
            history.goBack()
            return true
        } else if (shortcutManager.isPressed(KeymapKey.REDO)) {
            history.goForward()
            return true
        } else if (shortcutManager.isPressed(KeymapKey.SAVE_PROJECT)) {
            projectManager.saveCurrentProject()
            UI.toaster.success("Project saved")
            return true
        } else if (shortcutManager.isPressed(KeymapKey.TRANSLATE_TOOL)) {
            toolManager.activateTool(toolManager.translateTool)
            UI.toolbar.updateActiveToolButton()
        } else if (shortcutManager.isPressed(KeymapKey.ROTATE_TOOL)) {
            toolManager.activateTool(toolManager.rotateTool)
            UI.toolbar.updateActiveToolButton()
        } else if (shortcutManager.isPressed(KeymapKey.SCALE_TOOL)) {
            toolManager.activateTool(toolManager.scaleTool)
            UI.toolbar.updateActiveToolButton()
        } else if (shortcutManager.isPressed(KeymapKey.SELECT_TOOL)) {
            toolManager.activateTool(toolManager.selectionTool)
            UI.toolbar.updateActiveToolButton()
        } else if (shortcutManager.isPressed(KeymapKey.DEBUG_RENDER_MODE)) {
            debugRenderer.isEnabled = !debugRenderer.isEnabled
            globalPrefManager.set(MundusPreferencesManager.GLOB_BOOL_DEBUG_RENDERER_ON, debugRenderer.isEnabled)
        } else if (shortcutManager.isPressed(KeymapKey.WIREFRAME_RENDER_MODE)) {
            projectManager.current().renderWireframe = !projectManager.current().renderWireframe
        }

        return false
    }

}
