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

enum class KeymapKey(val type: KeymapKeyType) {
    MOVE_FORWARD(KeymapKeyType.KEY),
    MOVE_BACK(KeymapKeyType.KEY),
    STRAFE_LEFT(KeymapKeyType.KEY),
    STRAFE_RIGHT(KeymapKeyType.KEY),
    MOVE_UP(KeymapKeyType.KEY),
    MOVE_DOWN(KeymapKeyType.KEY),
    FULLSCREEN(KeymapKeyType.KEY),
    UNDO(KeymapKeyType.KEY),
    REDO(KeymapKeyType.KEY),
    SAVE_PROJECT(KeymapKeyType.KEY),
    TRANSLATE_TOOL(KeymapKeyType.KEY),
    ROTATE_TOOL(KeymapKeyType.KEY),
    SCALE_TOOL(KeymapKeyType.KEY),
    SELECT_TOOL(KeymapKeyType.KEY),
    DEBUG_RENDER_MODE(KeymapKeyType.KEY),
    WIREFRAME_RENDER_MODE(KeymapKeyType.KEY),

    OBJECT_SELECTION(KeymapKeyType.BUTTON),
    LOOK_AROUND(KeymapKeyType.BUTTON);
}
