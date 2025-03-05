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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ObjectMap
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.io.IOManager
import com.mbrlabs.mundus.editor.core.io.IOManagerProvider
import com.mbrlabs.mundus.editor.core.keymap.KeymapKey
import com.mbrlabs.mundus.editor.core.keymap.KeymapKeyType
import com.mbrlabs.mundus.editor.core.keymap.KeyboardShortcutManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.ButtonUtils

class KeyboardShortcutsSettingsTable : BaseSettingsTable() {

    companion object {
        val FORBIDDEN_KEYS = arrayOf(
            Input.Keys.CONTROL_LEFT
        )
    }

    private val keyboardShortcutManager = Mundus.inject<KeyboardShortcutManager>()
    private val registry: Registry = Mundus.inject()
    private val ioManager: IOManager = Mundus.inject<IOManagerProvider>().ioManager
    private val changeKeyInputListener = ChangeKeyInputListener()
    private val changeButtonInputListener = ChangeButtonInputListener()
    private val keyboardShortcutsTable = VisTable()

    private val keyboardShortcuts = ObjectMap<KeymapKey, ModifiedKeycode>()

    init {
        top().left()
        padRight(UI.PAD_SIDE).padLeft(UI.PAD_SIDE)

        add(VisLabel("Keyboard Shortcuts Settings")).left().row()
        addSeparator().padBottom(UI.PAD_SIDE*2)

        keyboardShortcutsTable.defaults().pad(4f)
        add(keyboardShortcutsTable).growX()
    }

    override fun onInit() {
        keyboardShortcuts.clear()
        for (keymapKey in KeymapKey.values()) {
            keyboardShortcuts.put(keymapKey, ModifiedKeycode(keyboardShortcutManager.getKey(keymapKey), false))
        }

        initKeymapTable()
    }

    override fun onSave() {
        // Save changed shortcuts
        for (keyboardShortcut in keyboardShortcuts) {
            if (keyboardShortcut.value.modified) {
                keyboardShortcutManager.setKey(keyboardShortcut.key, keyboardShortcut.value.keycode)
                keyboardShortcut.value.modified = false
            }
        }

        // Save into registry
        val customKeyboardShortcuts = registry.settings.customKeyboardShortcuts
        customKeyboardShortcuts.clear()

        for (keymapKey in KeymapKey.values()) {
            val keycode = keyboardShortcutManager.getKey(keymapKey)
            if (keycode != keyboardShortcutManager.getDefaultKeycode(keymapKey)) {
                val keyText = if (keymapKey.type == KeymapKeyType.KEY) Input.Keys.toString(keycode) else ButtonUtils.buttonToString(keycode)
                customKeyboardShortcuts.put(keymapKey.name, keyText)
            }
        }

        ioManager.saveRegistry(registry)
        Mundus.postEvent(SettingsChangedEvent(registry.settings))
        UI.toaster.success("Settings saved")
    }

    private fun initKeymapTable() {
        keyboardShortcutsTable.clear()

        addShortcut(KeymapKey.MOVE_FORWARD, "Move Forward", keyboardShortcutsTable)
        addShortcut(KeymapKey.MOVE_BACK, "Move Back", keyboardShortcutsTable)
        addShortcut(KeymapKey.STRAFE_LEFT, "Strafe Left", keyboardShortcutsTable)
        addShortcut(KeymapKey.STRAFE_RIGHT, "Strafe Right", keyboardShortcutsTable)
        addShortcut(KeymapKey.MOVE_UP, "Move Up", keyboardShortcutsTable)
        addShortcut(KeymapKey.MOVE_DOWN, "Move Down", keyboardShortcutsTable)

        addShortcut(KeymapKey.OBJECT_SELECTION, "Object Selection", keyboardShortcutsTable)
        addShortcut(KeymapKey.LOOK_AROUND, "Look Around (Hold)", keyboardShortcutsTable)

        addShortcut(KeymapKey.FULLSCREEN, "Toggle Fullscreen 3d", keyboardShortcutsTable)
    }

    private fun addShortcut(keymapKey: KeymapKey, desc: String, table: VisTable) {
        val keyLabel = if (KeymapKeyType.KEY == keymapKey.type)
            VisLabel(Input.Keys.toString(keyboardShortcutManager.getKey(keymapKey)))
        else
            VisLabel(ButtonUtils.buttonToString(keyboardShortcutManager.getKey(keymapKey)))

        val changeButton = VisTextButton("Change")
        if (KeymapKeyType.KEY == keymapKey.type) {
            changeButton.addListener(ChangeKeyListener(keymapKey, keyLabel))
        } else if (KeymapKeyType.BUTTON == keymapKey.type) {
            changeButton.addListener(ChangeButtonListener(keymapKey, keyLabel))
        }

        val resetButton = VisTextButton("Reset")
        resetButton.addListener(ResetButtonListener(keymapKey, keyLabel))

        table.add(keyLabel).left()
        table.add(changeButton).left()
        table.add(resetButton).left()
        table.addSeparator(true)
        table.add(desc).left().row()
        table.addSeparator().colspan(5)
    }

    private fun isKeyAlreadyUse(keymapKey: KeymapKey, keycode: Int): Boolean {
        return keyboardShortcuts.count { it.key != keymapKey && it.value.keycode == keycode } > 0
    }

    private inner class ChangeKeyListener(val keymapKey: KeymapKey, val keyLabel: VisLabel) : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            changeKeyInputListener.originalInputListeners = Gdx.input.inputProcessor
            changeKeyInputListener.keymapKey = keymapKey
            changeKeyInputListener.keyLabel = keyLabel
            Gdx.input.inputProcessor = changeKeyInputListener

            keyLabel.setText("---")
        }
    }

    private inner class ChangeButtonListener(val keymapKey: KeymapKey, val keyLabel: VisLabel) : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            changeButtonInputListener.originalInputListeners = Gdx.input.inputProcessor
            changeButtonInputListener.keymapKey = keymapKey
            changeButtonInputListener.keyLabel = keyLabel
            Gdx.input.inputProcessor = changeButtonInputListener

            keyLabel.setText("---")
        }
    }

    private inner class ResetButtonListener(val keymapKey: KeymapKey, val keyLabel: VisLabel) : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            val defaultKey = keyboardShortcutManager.getDefaultKeycode(keymapKey)
            if (isKeyAlreadyUse(keymapKey, defaultKey)) {
                UI.toaster.error("The key is already used!")
                return
            }

            keyboardShortcuts.get(keymapKey).keycode = defaultKey
            keyboardShortcuts.get(keymapKey).modified = true
            if (KeymapKeyType.KEY == keymapKey.type) {
                keyLabel.setText(Input.Keys.toString(defaultKey))
            } else if (KeymapKeyType.BUTTON == keymapKey.type) {
                keyLabel.setText(ButtonUtils.buttonToString(defaultKey))
            }
        }
    }

    private inner class ChangeKeyInputListener : InputAdapter() {

        var originalInputListeners: InputProcessor? = null
        lateinit var keymapKey: KeymapKey
        lateinit var keyLabel: VisLabel

        override fun keyUp(keycode: Int): Boolean {
            if (keycode == Input.Keys.ESCAPE) {
                keyLabel.setText(Input.Keys.toString(keyboardShortcutManager.getKey(keymapKey)))
                Gdx.input.inputProcessor = originalInputListeners
            } else if (!FORBIDDEN_KEYS.contains(keycode)) {
                if (isKeyAlreadyUse(keymapKey, keycode)) {
                    UI.toaster.error("The key is already used!")
                } else {
                    keyboardShortcuts.get(keymapKey).keycode = keycode
                    keyboardShortcuts.get(keymapKey).modified = true
                    keyLabel.setText(Input.Keys.toString(keycode))
                    Gdx.input.inputProcessor = originalInputListeners
                }
            }

            return true
        }
    }

    private inner class ChangeButtonInputListener : InputAdapter() {

        var originalInputListeners: InputProcessor? = null
        lateinit var keymapKey: KeymapKey
        lateinit var keyLabel: VisLabel

        override fun keyUp(keycode: Int): Boolean {
            if (keycode == Input.Keys.ESCAPE) {
                keyLabel.setText(ButtonUtils.buttonToString(keyboardShortcuts.get(keymapKey).keycode))
            }

            Gdx.input.inputProcessor = originalInputListeners
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (isKeyAlreadyUse(keymapKey, button)) {
                UI.toaster.error("The key is already used!")
            } else {
                keyboardShortcuts.get(keymapKey).keycode = button
                keyboardShortcuts.get(keymapKey).modified = true
                keyLabel.setText(ButtonUtils.buttonToString(button))

                Gdx.input.inputProcessor = originalInputListeners
            }
            return true
        }
    }

    private data class ModifiedKeycode(var keycode: Int, var modified: Boolean)
}
