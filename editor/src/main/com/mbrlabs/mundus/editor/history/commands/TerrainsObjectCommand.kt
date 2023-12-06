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

/**
 * A wrapper for modifying the objects of multiple terrains.
 */
class TerrainsObjectCommand : Command {
    private var terrainObjectCommands = HashMap<TerrainComponent, TerrainObjectCommand>()

    override fun execute() {
        for (terrain in terrainObjectCommands.keys) {
            val command = terrainObjectCommands[terrain]
            command!!.execute()
        }
    }

    override fun undo() {
        for (terrain in terrainObjectCommands.keys) {
            val command = terrainObjectCommands[terrain]
            command!!.undo()
        }
    }

    /**
     * Add a terrain to be modified and set the object data before.
     */
    fun addTerrain(terrain: TerrainComponent) {
        val command = TerrainObjectCommand(terrain)
        command.setObjectsBefore()
        terrainObjectCommands[terrain] = command
    }

    /**
     * Call this after modifying the terrains is complete.
     * This will set the object data after.
     */
    fun setAfter() {
        for (terrain in terrainObjectCommands.keys) {
            val command = terrainObjectCommands[terrain]
            command!!.setObjectsAfter()
        }
    }

}