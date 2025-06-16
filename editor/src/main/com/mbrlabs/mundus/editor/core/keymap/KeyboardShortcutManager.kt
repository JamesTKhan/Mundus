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

package com.mbrlabs.mundus.editor.core.keymap

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.ObjectMap
import com.mbrlabs.mundus.editor.utils.ButtonUtils

class KeyboardShortcutManager(customKeyboardShortcuts: ObjectMap<String, String>) {

    companion object {
        val MOVE_FORWARD_DEFAULT_KEY = KeyboardShortcut(Input.Keys.W)
        val MOVE_BACK_DEFAULT_KEY = KeyboardShortcut(Input.Keys.S)
        val STRAFE_LEFT_DEFAULT_KEY = KeyboardShortcut(Input.Keys.A)
        val STRAFE_RIGHT_DEFAULT_KEY = KeyboardShortcut(Input.Keys.D)
        val MOVE_UP_DEFAULT_KEY = KeyboardShortcut(Input.Keys.Q)
        val MOVE_DOWN_DEFAULT_KEY = KeyboardShortcut(Input.Keys.E)
        val FULLSCREEN_DEFAULT_KEY = KeyboardShortcut(Input.Keys.F8)
        val UNDO_DEFAULT_KEY = KeyboardShortcut(Input.Keys.Z, Input.Keys.CONTROL_LEFT)
        val REDO_DEFAULT_KEY = KeyboardShortcut(Input.Keys.Y, Input.Keys.CONTROL_LEFT)
        val SAVE_PROJECT_DEFAULT_KEY = KeyboardShortcut(Input.Keys.S, Input.Keys.CONTROL_LEFT)
        val TRANSLATE_TOOL_DEFAULT_KEY = KeyboardShortcut(Input.Keys.T, Input.Keys.CONTROL_LEFT)
        val ROTATE_TOOL_DEFAULT_KEY = KeyboardShortcut(Input.Keys.R, Input.Keys.CONTROL_LEFT)
        val SCALE_TOOL_DEFAULT_KEY = KeyboardShortcut(Input.Keys.G, Input.Keys.CONTROL_LEFT)
        val SELECT_TOOL_DEFAULT_KEY = KeyboardShortcut(Input.Keys.F, Input.Keys.CONTROL_LEFT)
        val DEBUG_RENDER_MODE_DEFAULT_KEY = KeyboardShortcut(Input.Keys.F2, Input.Keys.CONTROL_LEFT)
        val WIREFRAME_RENDER_MODE_DEFAULT_KEY = KeyboardShortcut(Input.Keys.F3, Input.Keys.CONTROL_LEFT)

        val OBJECT_SELECTION_DEFAULT_KEY = KeyboardShortcut(Input.Buttons.RIGHT)
        val LOOK_AROUND_DEFAULT_KEY = KeyboardShortcut(Input.Buttons.MIDDLE)
    }

    private val keymap = ObjectMap<KeymapKey, KeyboardShortcut>()

    init {
        // Keyboard shortcuts
        keymap.put(KeymapKey.MOVE_FORWARD, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.MOVE_FORWARD))
        keymap.put(KeymapKey.MOVE_BACK, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.MOVE_BACK))
        keymap.put(KeymapKey.STRAFE_LEFT, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.STRAFE_LEFT))
        keymap.put(KeymapKey.STRAFE_RIGHT, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.STRAFE_RIGHT))
        keymap.put(KeymapKey.MOVE_UP, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.MOVE_UP))
        keymap.put(KeymapKey.MOVE_DOWN, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.MOVE_DOWN))
        keymap.put(KeymapKey.FULLSCREEN, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.FULLSCREEN))
        keymap.put(KeymapKey.UNDO, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.UNDO))
        keymap.put(KeymapKey.REDO, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.REDO))
        keymap.put(KeymapKey.SAVE_PROJECT, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.SAVE_PROJECT))
        keymap.put(KeymapKey.TRANSLATE_TOOL, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.TRANSLATE_TOOL))
        keymap.put(KeymapKey.ROTATE_TOOL, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.ROTATE_TOOL))
        keymap.put(KeymapKey.SCALE_TOOL, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.SCALE_TOOL))
        keymap.put(KeymapKey.SELECT_TOOL, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.SELECT_TOOL))
        keymap.put(KeymapKey.DEBUG_RENDER_MODE, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.DEBUG_RENDER_MODE))
        keymap.put(KeymapKey.WIREFRAME_RENDER_MODE, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.WIREFRAME_RENDER_MODE))

        // Mouse shortcuts
        keymap.put(KeymapKey.OBJECT_SELECTION, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.OBJECT_SELECTION))
        keymap.put(KeymapKey.LOOK_AROUND, getKeyboardShortcut(customKeyboardShortcuts, KeymapKey.LOOK_AROUND))
    }

    fun getKey(keymapKey: KeymapKey): KeyboardShortcut = keymap.get(keymapKey)

    fun getKeyText(keymapKey: KeymapKey): String {
        return getKeyText(keymapKey, keymap.get(keymapKey))
    }

    fun getKeyText(keymapKey: KeymapKey, keyboardShortcut: KeyboardShortcut): String {
        var text = ""

        if (KeymapKeyType.KEY == keymapKey.type) {
            if (keyboardShortcut.extraKeycode != null) {
                text = Input.Keys.toString(keyboardShortcut.extraKeycode) + "+"
            }
            text += Input.Keys.toString(keyboardShortcut.keycode)
        } else { // KeymapKeyType.BUTTON == keymapKey.type
            text = ButtonUtils.buttonToString(keyboardShortcut.keycode)
        }

        return text
    }

    fun setKey(keymapKey: KeymapKey, keyboardShortcut: KeyboardShortcut) = keymap.put(keymapKey, keyboardShortcut)

    fun getDefaultKeycode(keymapKey: KeymapKey): KeyboardShortcut {
        return when(keymapKey) {
            KeymapKey.MOVE_FORWARD -> MOVE_FORWARD_DEFAULT_KEY
            KeymapKey.MOVE_BACK -> MOVE_BACK_DEFAULT_KEY
            KeymapKey.STRAFE_LEFT -> STRAFE_LEFT_DEFAULT_KEY
            KeymapKey.STRAFE_RIGHT -> STRAFE_RIGHT_DEFAULT_KEY
            KeymapKey.MOVE_UP -> MOVE_UP_DEFAULT_KEY
            KeymapKey.MOVE_DOWN -> MOVE_DOWN_DEFAULT_KEY
            KeymapKey.FULLSCREEN -> FULLSCREEN_DEFAULT_KEY
            KeymapKey.UNDO -> UNDO_DEFAULT_KEY
            KeymapKey.REDO -> REDO_DEFAULT_KEY
            KeymapKey.SAVE_PROJECT -> SAVE_PROJECT_DEFAULT_KEY
            KeymapKey.TRANSLATE_TOOL -> TRANSLATE_TOOL_DEFAULT_KEY
            KeymapKey.ROTATE_TOOL -> ROTATE_TOOL_DEFAULT_KEY
            KeymapKey.SCALE_TOOL -> SCALE_TOOL_DEFAULT_KEY
            KeymapKey.SELECT_TOOL -> SELECT_TOOL_DEFAULT_KEY
            KeymapKey.DEBUG_RENDER_MODE -> DEBUG_RENDER_MODE_DEFAULT_KEY
            KeymapKey.WIREFRAME_RENDER_MODE -> WIREFRAME_RENDER_MODE_DEFAULT_KEY

            KeymapKey.OBJECT_SELECTION -> OBJECT_SELECTION_DEFAULT_KEY
            KeymapKey.LOOK_AROUND -> LOOK_AROUND_DEFAULT_KEY
        }
    }

    fun isPressed(keymapKey: KeymapKey): Boolean {
        val keyboardShortcut = keymap.get(keymapKey)

        if (KeymapKeyType.KEY == keymapKey.type) {
            return (keyboardShortcut.extraKeycode == null || Gdx.input.isKeyPressed(keyboardShortcut.extraKeycode)) &&
                    Gdx.input.isKeyJustPressed(keyboardShortcut.keycode)
        } else { // KeymapKeyType.BUTTON == keymapKey.type
            return Gdx.input.isButtonPressed(keyboardShortcut.keycode)
        }
    }

    fun hasConfiguredButtonCode(keymapKey: KeymapKey, buttonCode: Int): Boolean {
        return keymap.get(keymapKey).keycode == buttonCode
    }

    private fun getKeyboardShortcut(customKeyboardShortcuts: ObjectMap<String, String>, keymapKey: KeymapKey): KeyboardShortcut {
        if (!customKeyboardShortcuts.containsKey(keymapKey.name)) {
            return getDefaultKeycode(keymapKey)
        }

        val text = customKeyboardShortcuts.get(keymapKey.name)
        if (KeymapKeyType.KEY == keymapKey.type) {
            val keyTexts = text.split("+").map { it.replace("+", "") }
            if (keyTexts.size == 1) {
                return KeyboardShortcut(Input.Keys.valueOf(keyTexts[0]))
            } else {
                return KeyboardShortcut(Input.Keys.valueOf(keyTexts[0]), Input.Keys.valueOf(keyTexts[1]))
            }
        } else { // KeymapKeyType.KEY == keymapKey.type
            return KeyboardShortcut(ButtonUtils.buttonStringToButtonKey(text))
        }
    }
}
