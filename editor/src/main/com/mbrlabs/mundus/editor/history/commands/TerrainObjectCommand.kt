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

import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.assets.TerrainObject
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.history.Command

class TerrainObjectCommand(private var terrain: TerrainComponent) : Command {

    private val beforeTerrainObjects = Array<TerrainObject>()
    private val afterTerrainObjects = Array<TerrainObject>()

    override fun execute() {
        executeArray(afterTerrainObjects)
        terrain.applyTerrainObjects()
    }

    override fun undo() {
        executeArray(beforeTerrainObjects)
        terrain.applyTerrainObjects()
    }

    fun setObjectsBefore() {
        loadArray(beforeTerrainObjects)
    }

    fun setObjectsAfter() {
        loadArray(afterTerrainObjects)
    }

    private fun loadArray(array: Array<TerrainObject>) {
        val terrainObjectsAsset = terrain.terrainAsset.terrainObjectsAsset

        for (i in 0..<terrainObjectsAsset.terrainObjectNum) {
            val copyTerrainObject = TerrainObject(terrainObjectsAsset.getTerrainObject(i))
            array.add(copyTerrainObject)
        }
    }

    private fun executeArray(array: Array<TerrainObject>) {
        val terrainObjectsAsset = terrain.terrainAsset.terrainObjectsAsset

        terrainObjectsAsset.clear()

        for (terrainObject in array) {
            val copyTerrainObject = TerrainObject(terrainObject)
            terrainObjectsAsset.addTerrainObject(copyTerrainObject)
        }
    }
}
