/*
 * Copyright (c) 2025. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.settings

import com.badlogic.gdx.Input
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.io.IOManager
import com.mbrlabs.mundus.editor.core.io.IOManagerProvider
import com.mbrlabs.mundus.editor.core.keymap.KeymapKey
import com.mbrlabs.mundus.editor.core.keymap.KeymapManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent
import com.mbrlabs.mundus.editor.ui.UI

class KeyboardShortcutsSettingsTable : BaseSettingsTable() {

    private val keymapManager = Mundus.inject<KeymapManager>()
    private val registry: Registry = Mundus.inject()
    private val ioManager: IOManager = Mundus.inject<IOManagerProvider>().ioManager

    init {
        top().left()
        padRight(UI.PAD_SIDE).padLeft(UI.PAD_SIDE)

        add(VisLabel("Keyboard Shortcuts Settings")).left().row()
        addSeparator().padBottom(UI.PAD_SIDE*2)

        val keyboardShortcutsTable = VisTable()
        keyboardShortcutsTable.defaults().pad(4f)

        addShortcut(keymapManager.getKey(KeymapKey.MOVE_FORWARD), "Move Forward", keyboardShortcutsTable)
        addShortcut(keymapManager.getKey(KeymapKey.MOVE_BACK), "Move Back", keyboardShortcutsTable)
        addShortcut(keymapManager.getKey(KeymapKey.STRAFE_LEFT), "Strafe Left", keyboardShortcutsTable)
        addShortcut(keymapManager.getKey(KeymapKey.STRAFE_RIGHT), "Strafe Right", keyboardShortcutsTable)

        add(keyboardShortcutsTable).growX()
    }

    override fun onSave() {
        val customKeyboardShortcuts = registry.settings.customKeyboardShortcuts
        customKeyboardShortcuts.clear()

        if (keymapManager.getKey(KeymapKey.MOVE_FORWARD) != KeymapManager.MOVE_FORWARD_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.MOVE_FORWARD.name, Input.Keys.toString(keymapManager.getKey(KeymapKey.MOVE_FORWARD)))
        }
        if (keymapManager.getKey(KeymapKey.MOVE_BACK) != KeymapManager.MOVE_BACK_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.MOVE_BACK.name, Input.Keys.toString(keymapManager.getKey(KeymapKey.MOVE_BACK)))
        }
        if (keymapManager.getKey(KeymapKey.STRAFE_LEFT) != KeymapManager.STRAFE_LEFT_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.STRAFE_LEFT.name, Input.Keys.toString(keymapManager.getKey(KeymapKey.STRAFE_LEFT)))
        }
        if (keymapManager.getKey(KeymapKey.STRAFE_RIGHT) != KeymapManager.STRAFE_RIGHT_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.STRAFE_RIGHT.name, Input.Keys.toString(keymapManager.getKey(KeymapKey.STRAFE_RIGHT)))
        }

        ioManager.saveRegistry(registry)
        Mundus.postEvent(SettingsChangedEvent(registry.settings))
        UI.toaster.success("Settings saved")
    }

    private fun addShortcut(keycode: Int, desc: String, table: VisTable) {
        table.add(Input.Keys.toString(keycode)).left()
        table.addSeparator(true)
        table.add(desc).left().row()
        table.addSeparator().colspan(3)
    }
}
