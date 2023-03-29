package com.mbrlabs.mundus.editor.core.helperlines

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.badlogic.gdx.utils.Array

class HelperLines {

    companion object {
        // TMP
        const val WIDTH = 2
    }

    private val helperLineObjects = Array<HelperLineObject>()

    fun build(terrainComponents: Array<TerrainComponent>) {
        for (terrainComponent in terrainComponents) {
            helperLineObjects.add(HelperLineObject(terrainComponent))
        }
    }

    fun render(batch: ModelBatch) {
        for (helperLineObject in helperLineObjects) {
            Gdx.gl20.glLineWidth(5f)
            batch.render(helperLineObject.modelInstance)
        }
    }

}
