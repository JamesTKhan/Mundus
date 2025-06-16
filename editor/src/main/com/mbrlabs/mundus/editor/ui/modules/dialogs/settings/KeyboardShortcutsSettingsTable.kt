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
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.io.IOManager
import com.mbrlabs.mundus.editor.core.io.IOManagerProvider
import com.mbrlabs.mundus.editor.core.keymap.KeyboardShortcut
import com.mbrlabs.mundus.editor.core.keymap.KeymapKey
import com.mbrlabs.mundus.editor.core.keymap.KeymapKeyType
import com.mbrlabs.mundus.editor.core.keymap.KeyboardShortcutManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.KeyboardLayoutUtils

class KeyboardShortcutsSettingsTable : BaseSettingsTable() {

    companion object {
        val FORBIDDEN_KEYS = arrayOf(
            Input.Keys.CONTROL_LEFT,
            Input.Keys.CONTROL_RIGHT,
            Input.Keys.ALT_LEFT,
            Input.Keys.ALT_RIGHT
        )
    }

    private val keyboardShortcutManager = Mundus.inject<KeyboardShortcutManager>()
    private val registry: Registry = Mundus.inject()
    private val ioManager: IOManager = Mundus.inject<IOManagerProvider>().ioManager
    private val changeKeyInputListener = ChangeKeyInputListener()
    private val changeButtonInputListener = ChangeButtonInputListener()
    private val keyboardShortcutsTable = VisTable()
    private val scrollPane = VisScrollPane(keyboardShortcutsTable)

    private val keyboardShortcuts = ObjectMap<KeymapKey, ModifiedKeycode>()

    init {
        top().left()
        padRight(UI.PAD_SIDE).padLeft(UI.PAD_SIDE)

        add(VisLabel("Keyboard Shortcuts Settings")).left().row()
        addSeparator().padBottom(UI.PAD_SIDE*2)

        keyboardShortcutsTable.defaults().pad(4f)

        scrollPane.setFlickScroll(false)
        scrollPane.setFadeScrollBars(false)
        add(scrollPane).growX()
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
                keyboardShortcutManager.setKey(keyboardShortcut.key, keyboardShortcut.value.keyboardShortcut)
                keyboardShortcut.value.modified = false
            }
        }

        // Save into registry
        val customKeyboardShortcuts = registry.settings.customKeyboardShortcuts
        customKeyboardShortcuts.clear()

        for (keymapKey in KeymapKey.values()) {
            val keycode = keyboardShortcutManager.getKey(keymapKey)
            if (keycode != keyboardShortcutManager.getDefaultKeycode(keymapKey)) {
                customKeyboardShortcuts.put(keymapKey.name, keyboardShortcutManager.getKeyText(keymapKey))
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
        addShortcut(KeymapKey.UNDO, "Undo", keyboardShortcutsTable)
        addShortcut(KeymapKey.REDO, "Redo", keyboardShortcutsTable)
        addShortcut(KeymapKey.SAVE_PROJECT, "Save Project", keyboardShortcutsTable)
        addShortcut(KeymapKey.TRANSLATE_TOOL, "Translate Tool", keyboardShortcutsTable)
        addShortcut(KeymapKey.ROTATE_TOOL, "Rotate Tool", keyboardShortcutsTable)
        addShortcut(KeymapKey.SCALE_TOOL, "Scale Tool", keyboardShortcutsTable)
        addShortcut(KeymapKey.SELECT_TOOL, "Select Tool", keyboardShortcutsTable)
        addShortcut(KeymapKey.DEBUG_RENDER_MODE, "Debug Render Mode", keyboardShortcutsTable)
        addShortcut(KeymapKey.WIREFRAME_RENDER_MODE, "Wireframe Mode", keyboardShortcutsTable)
    }

    private fun addShortcut(keymapKey: KeymapKey, desc: String, table: VisTable) {
        val keyLabel = VisLabel(keyboardShortcutManager.getKeyText(keymapKey))

        val changeButton = VisTextButton("Change")
        if (KeymapKeyType.KEY == keymapKey.type) {
            changeButton.addListener(ChangeKeyListener(keymapKey, keyLabel))
        } else if (KeymapKeyType.BUTTON == keymapKey.type) {
            changeButton.addListener(ChangeButtonListener(keymapKey, keyLabel))
        }

        val resetButton = VisTextButton("Reset")
        resetButton.addListener(ResetButtonListener(keymapKey, keyLabel))

        table.add(keyLabel).left()
        table.add(changeButton).right()
        table.add(resetButton).right()
        table.addSeparator(true)
        table.add(desc).left().row()
        table.addSeparator().colspan(5)
    }

    private fun isKeyAlreadyUse(keymapKey: KeymapKey, keyboardShortcut: KeyboardShortcut): Boolean {
        return keyboardShortcuts.count { it.key != keymapKey && it.value.keyboardShortcut == keyboardShortcut } > 0
    }

    private fun getPressedExtraKeycode(): Int? {
        for (extraKey in FORBIDDEN_KEYS) {
            if (Gdx.input.isKeyPressed(extraKey)) {
                return extraKey
            }
        }

        return null
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

            keyboardShortcuts.get(keymapKey).keyboardShortcut = defaultKey
            keyboardShortcuts.get(keymapKey).modified = true
            keyLabel.setText(keyboardShortcutManager.getKeyText(keymapKey, defaultKey))
        }
    }

    private inner class ChangeKeyInputListener : InputAdapter() {

        var originalInputListeners: InputProcessor? = null
        lateinit var keymapKey: KeymapKey
        lateinit var keyLabel: VisLabel

        override fun keyUp(keycode: Int): Boolean {
            if (keycode == Input.Keys.ESCAPE) {
                keyLabel.setText(keyboardShortcutManager.getKeyText(keymapKey, keyboardShortcuts.get(keymapKey).keyboardShortcut))
                Gdx.input.inputProcessor = originalInputListeners
            } else if (!FORBIDDEN_KEYS.contains(keycode)) {
                val keyboardShortcut = KeyboardShortcut(KeyboardLayoutUtils.convertKeycode(keycode), getPressedExtraKeycode())

                if (isKeyAlreadyUse(keymapKey, keyboardShortcut)) {
                    UI.toaster.error("The key is already used!")
                } else {
                    keyboardShortcuts.get(keymapKey).keyboardShortcut = keyboardShortcut
                    keyboardShortcuts.get(keymapKey).modified = true
                    keyLabel.setText(keyboardShortcutManager.getKeyText(keymapKey, keyboardShortcut))
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
                keyLabel.setText(keyboardShortcutManager.getKeyText(keymapKey, keyboardShortcuts.get(keymapKey).keyboardShortcut))
            }

            Gdx.input.inputProcessor = originalInputListeners
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val keyboardShortcut = KeyboardShortcut(button)

            if (isKeyAlreadyUse(keymapKey, keyboardShortcut)) {
                UI.toaster.error("The key is already used!")
            } else {
                keyboardShortcuts.get(keymapKey).keyboardShortcut = keyboardShortcut
                keyboardShortcuts.get(keymapKey).modified = true
                keyLabel.setText(keyboardShortcutManager.getKeyText(keymapKey, keyboardShortcut))

                Gdx.input.inputProcessor = originalInputListeners
            }
            return true
        }
    }

    private data class ModifiedKeycode(var keyboardShortcut: KeyboardShortcut, var modified: Boolean)
}
