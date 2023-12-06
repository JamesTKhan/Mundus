/*
 * Copyright (c) 2023. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.history.commands

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.history.Command

class TerrainObjectCommand(private var terrain: TerrainComponent) : Command {
    override fun execute() {
        // TODO implement later
    }

    override fun undo() {
        // TODO implement later
    }

    fun setObjectsBefore() {
        // TODO implement later
    }

    fun setObjectsAfter() {
        // TODO implement later
    }


}
