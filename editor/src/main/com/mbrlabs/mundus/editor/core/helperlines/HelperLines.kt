package com.mbrlabs.mundus.editor.core.helperlines

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.TerrainRemovedEvent
import com.mbrlabs.mundus.editor.events.TerrainVerticesChangedEvent

class HelperLines : TerrainVerticesChangedEvent.TerrainVerticesChangedEventListener,
        TerrainRemovedEvent.TerrainRemovedEventListener,
        Disposable {

    private val helperLineObjects = Array<HelperLineObject>()
    private var width = 0

    init {
        Mundus.registerEventListener(this)
    }


    fun build(width: Int, terrainComponents: Array<TerrainComponent>) {
        this.width = width

        for (terrainComponent in terrainComponents) {
            helperLineObjects.add(HelperLineObject(width, terrainComponent))
        }
    }

    fun render(batch: ModelBatch) {
        for (helperLineObject in helperLineObjects) {
//            Gdx.gl20.glLineWidth(5f)
            batch.render(helperLineObject.modelInstance)
        }
    }

    fun hasHelperLines() = helperLineObjects.notEmpty()

    override fun onTerrainVerticesChanged(event: TerrainVerticesChangedEvent) {
        helperLineObjects.filter { it.terrainComponent == event.terrainComponent }.forEach { it.updateVertices() }
    }

    override fun onTerrainRemoved(event: TerrainRemovedEvent) {
        helperLineObjects.filter { it.terrainComponent == event.terrainComponent }.forEach {
            it.dispose()
            helperLineObjects.removeValue(it, true)
        }
    }

    override fun dispose() {
        helperLineObjects.forEach { helperLineObject -> helperLineObject.dispose() }
        helperLineObjects.clear()
    }

}
