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

package com.mbrlabs.mundus.editor.core.helperlines

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.mbrlabs.mundus.editor.events.TerrainAddedEvent
import com.mbrlabs.mundus.editor.events.TerrainNewNeighborEvent
import com.mbrlabs.mundus.editor.events.TerrainRemovedEvent
import com.mbrlabs.mundus.editor.events.TerrainVerticesChangedEvent

class HelperLines : TerrainVerticesChangedEvent.TerrainVerticesChangedEventListener,
        TerrainAddedEvent.TerrainAddedEventListener,
        TerrainRemovedEvent.TerrainRemovedEventListener,
        TerrainNewNeighborEvent.TerrainNewNeighborEventListener,
        Disposable {

    private val helperLineShapes = Array<HelperLineShape>()
    private var width = -1
    private var type: HelperLineType? = null


    fun build(type: HelperLineType, width: Int, terrainComponents: Array<TerrainComponent>) {
        this.type = type
        this.width = width

        for (terrainComponent in terrainComponents) {
            addNewHelperLineShape(terrainComponent)
        }
    }

    fun render(batch: ModelBatch) {
        for (helperLineObject in helperLineShapes) {
            batch.render(helperLineObject.modelInstance)
        }
    }

    fun hasHelperLines() = helperLineShapes.notEmpty()

    override fun onTerrainVerticesChanged(event: TerrainVerticesChangedEvent) {
        helperLineShapes.filter { it.terrainComponent == event.terrainComponent }.forEach { it.updateVertices() }
    }

    override fun onTerrainAdded(event: TerrainAddedEvent) {
        if (type != null) {
            addNewHelperLineShape(event.terrainComponent)
        }
    }

    override fun onTerrainRemoved(event: TerrainRemovedEvent) {
        removeHelperLine(event.terrainComponent)
    }

    override fun onNewTerrainNeighbor(event: TerrainNewNeighborEvent) {
        val terrainComponent = event.terrainComponent
        removeHelperLine(terrainComponent)
        addNewHelperLineShape(terrainComponent)
    }

    override fun dispose() {
        helperLineShapes.forEach { helperLineObject -> helperLineObject.dispose() }
        helperLineShapes.clear()
        type = null
    }

    private fun removeHelperLine(terrainComponent: TerrainComponent) {
        helperLineShapes.filter { it.terrainComponent == terrainComponent }.forEach {
            it.dispose()
            helperLineShapes.removeValue(it, true)
        }
    }

    private fun addNewHelperLineShape(terrainComponent: TerrainComponent) {
        val helperLineShape : HelperLineShape
        if (type == HelperLineType.RECTANGLE) {
            helperLineShape = RectangleHelperLineShape(width, terrainComponent)
        } else {
            helperLineShape = HexagonHelperLineShape(width, terrainComponent)
        }
        helperLineShapes.add(helperLineShape)
    }

}
