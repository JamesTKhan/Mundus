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

import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.ObjectIntMap
import com.badlogic.gdx.utils.ObjectMap
import com.mbrlabs.mundus.editor.utils.ButtonUtils

class KeyboardShortcutManager(customKeyboardShortcuts: ObjectMap<String, String>) {

    companion object {
        const val MOVE_FORWARD_DEFAULT_KEY = Input.Keys.W
        const val MOVE_BACK_DEFAULT_KEY = Input.Keys.S
        const val STRAFE_LEFT_DEFAULT_KEY = Input.Keys.A
        const val STRAFE_RIGHT_DEFAULT_KEY = Input.Keys.D
        const val MOVE_UP_DEFAULT_KEY = Input.Keys.Q
        const val MOVE_DOWN_DEFAULT_KEY = Input.Keys.E
        const val FULLSCREEN_DEFAULT_KEY = Input.Keys.F8

        const val OBJECT_SELECTION_DEFAULT_KEY = Input.Buttons.RIGHT
        const val LOOK_AROUND_DEFAULT_KEY = Input.Buttons.MIDDLE
    }

    private val keymap = ObjectIntMap<KeymapKey>()

    init {
        // Keyboard shortcuts
        keymap.put(KeymapKey.MOVE_FORWARD, getKeyCode(customKeyboardShortcuts, KeymapKey.MOVE_FORWARD))
        keymap.put(KeymapKey.MOVE_BACK, getKeyCode(customKeyboardShortcuts, KeymapKey.MOVE_BACK))
        keymap.put(KeymapKey.STRAFE_LEFT, getKeyCode(customKeyboardShortcuts, KeymapKey.STRAFE_LEFT))
        keymap.put(KeymapKey.STRAFE_RIGHT, getKeyCode(customKeyboardShortcuts, KeymapKey.STRAFE_RIGHT))
        keymap.put(KeymapKey.MOVE_UP, getKeyCode(customKeyboardShortcuts, KeymapKey.MOVE_UP))
        keymap.put(KeymapKey.MOVE_DOWN, getKeyCode(customKeyboardShortcuts, KeymapKey.MOVE_DOWN))
        keymap.put(KeymapKey.FULLSCREEN, getKeyCode(customKeyboardShortcuts, KeymapKey.FULLSCREEN))

        // Mouse shortcuts
        keymap.put(KeymapKey.OBJECT_SELECTION, getButtonCode(customKeyboardShortcuts, KeymapKey.OBJECT_SELECTION))
        keymap.put(KeymapKey.LOOK_AROUND, getButtonCode(customKeyboardShortcuts, KeymapKey.LOOK_AROUND))
    }

    fun getKey(keymapKey: KeymapKey): Int = keymap.get(keymapKey, -1)

    fun setKey(keymapKey: KeymapKey, keycode: Int) = keymap.put(keymapKey, keycode)

    fun getDefaultKeycode(keymapKey: KeymapKey): Int {
        return when(keymapKey) {
            KeymapKey.MOVE_FORWARD -> MOVE_FORWARD_DEFAULT_KEY
            KeymapKey.MOVE_BACK -> MOVE_BACK_DEFAULT_KEY
            KeymapKey.STRAFE_LEFT -> STRAFE_LEFT_DEFAULT_KEY
            KeymapKey.STRAFE_RIGHT -> STRAFE_RIGHT_DEFAULT_KEY
            KeymapKey.MOVE_UP -> MOVE_UP_DEFAULT_KEY
            KeymapKey.MOVE_DOWN -> MOVE_DOWN_DEFAULT_KEY
            KeymapKey.FULLSCREEN -> FULLSCREEN_DEFAULT_KEY

            KeymapKey.OBJECT_SELECTION -> OBJECT_SELECTION_DEFAULT_KEY
            KeymapKey.LOOK_AROUND -> LOOK_AROUND_DEFAULT_KEY
        }
    }

    private fun getKeyCode(customKeyboardShortcuts: ObjectMap<String, String>, keymapKey: KeymapKey): Int {
        return Input.Keys.valueOf(customKeyboardShortcuts.get(keymapKey.name, Input.Keys.toString(getDefaultKeycode(keymapKey))))
    }

    private fun getButtonCode(customKeyboardShortcuts: ObjectMap<String, String>, keymapKey: KeymapKey): Int {
        return ButtonUtils.buttonStringToButtonKey(customKeyboardShortcuts.get(keymapKey.name, ButtonUtils.buttonToString(getDefaultKeycode(keymapKey))))
    }

}
