package com.mbrlabs.mundus.editor.core.helperlines

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable

class HelperLines : Disposable {

    private val helperLineObjects = Array<HelperLineObject>()


    fun build(width: Int, terrainComponents: Array<TerrainComponent>) {

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

    override fun dispose() {
        helperLineObjects.forEach { helperLineObject -> helperLineObject.dispose() }
        helperLineObjects.clear()
    }

}
