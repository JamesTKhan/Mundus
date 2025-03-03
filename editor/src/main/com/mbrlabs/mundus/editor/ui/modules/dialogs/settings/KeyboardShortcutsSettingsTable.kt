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
import com.badlogic.gdx.utils.ObjectIntMap
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

    private val keyboardShortcutManager = Mundus.inject<KeyboardShortcutManager>()
    private val registry: Registry = Mundus.inject()
    private val ioManager: IOManager = Mundus.inject<IOManagerProvider>().ioManager
    private val changeKeyInputListener = ChangeKeyInputListener()
    private val changeButtonInputListener = ChangeButtonInputListener()
    private val keyboardShortcutsTable = VisTable()

    private val changedKeyboardShortcuts = ObjectIntMap<KeymapKey>()

    init {
        top().left()
        padRight(UI.PAD_SIDE).padLeft(UI.PAD_SIDE)

        add(VisLabel("Keyboard Shortcuts Settings")).left().row()
        addSeparator().padBottom(UI.PAD_SIDE*2)

        keyboardShortcutsTable.defaults().pad(4f)
        add(keyboardShortcutsTable).growX()
    }

    override fun onInit() {
        changedKeyboardShortcuts.clear()
        initKeymapTable()
    }

    override fun onSave() {
        // Save changed shortcuts
        for (changedKeyboardShortcut in changedKeyboardShortcuts) {
            keyboardShortcutManager.setKey(changedKeyboardShortcut.key, changedKeyboardShortcut.value)
        }
        changedKeyboardShortcuts.clear()

        // Save into registry
        val customKeyboardShortcuts = registry.settings.customKeyboardShortcuts
        customKeyboardShortcuts.clear()

        if (keyboardShortcutManager.getKey(KeymapKey.MOVE_FORWARD) != KeyboardShortcutManager.MOVE_FORWARD_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.MOVE_FORWARD.name, Input.Keys.toString(keyboardShortcutManager.getKey(KeymapKey.MOVE_FORWARD)))
        }
        if (keyboardShortcutManager.getKey(KeymapKey.MOVE_BACK) != KeyboardShortcutManager.MOVE_BACK_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.MOVE_BACK.name, Input.Keys.toString(keyboardShortcutManager.getKey(KeymapKey.MOVE_BACK)))
        }
        if (keyboardShortcutManager.getKey(KeymapKey.STRAFE_LEFT) != KeyboardShortcutManager.STRAFE_LEFT_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.STRAFE_LEFT.name, Input.Keys.toString(keyboardShortcutManager.getKey(KeymapKey.STRAFE_LEFT)))
        }
        if (keyboardShortcutManager.getKey(KeymapKey.STRAFE_RIGHT) != KeyboardShortcutManager.STRAFE_RIGHT_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.STRAFE_RIGHT.name, Input.Keys.toString(keyboardShortcutManager.getKey(KeymapKey.STRAFE_RIGHT)))
        }
        if (keyboardShortcutManager.getKey(KeymapKey.LOOK_AROUND) != KeyboardShortcutManager.LOOK_AROUND_DEFAULT_KEY) {
            customKeyboardShortcuts.put(KeymapKey.LOOK_AROUND.name, ButtonUtils.buttonToString(keyboardShortcutManager.getKey(KeymapKey.LOOK_AROUND)))
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
        addShortcut(KeymapKey.LOOK_AROUND, "Look Around (Hold)", keyboardShortcutsTable)
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
        resetButton.addListener(ResetButtonListener())

        table.add(keyLabel).left()
        table.add(changeButton).left()
        table.add(resetButton).left()
        table.addSeparator(true)
        table.add(desc).left().row()
        table.addSeparator().colspan(5)
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

    private class ResetButtonListener : ClickListener() {
        override fun clicked(event: InputEvent, x: Float, y: Float) {
            // TODO
        }
    }

    private inner class ChangeKeyInputListener : InputAdapter() {

        var originalInputListeners: InputProcessor? = null
        lateinit var keymapKey: KeymapKey
        lateinit var keyLabel: VisLabel

        override fun keyUp(keycode: Int): Boolean {
            if (keycode == Input.Keys.ESCAPE) {
                keyLabel.setText(Input.Keys.toString(keyboardShortcutManager.getKey(keymapKey)))
            } else {
                // TODO check already bind
                changedKeyboardShortcuts.put(keymapKey, keycode)
                keyLabel.setText(Input.Keys.toString(keycode))
            }

            Gdx.input.inputProcessor = originalInputListeners
            return true
        }
    }

    private inner class ChangeButtonInputListener : InputAdapter() {

        var originalInputListeners: InputProcessor? = null
        lateinit var keymapKey: KeymapKey
        lateinit var keyLabel: VisLabel

        override fun keyUp(keycode: Int): Boolean {
            if (keycode == Input.Keys.ESCAPE) {
                keyLabel.setText(Input.Keys.toString(keyboardShortcutManager.getKey(keymapKey)))
            }

            Gdx.input.inputProcessor = originalInputListeners
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            // TODO check already bind
            changedKeyboardShortcuts.put(keymapKey, button)
            keyLabel.setText(ButtonUtils.buttonToString(button))

            Gdx.input.inputProcessor = originalInputListeners
            return true
        }
    }
}
