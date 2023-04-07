package com.mbrlabs.mundus.editor.core.helperlines

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.terrain.Terrain

abstract class HelperLineObject(width: Int, val terrainComponent: TerrainComponent) : Disposable {

    val mesh: Mesh
    val modelInstance: ModelInstance

    init {
        val attribs = VertexAttributes(
                VertexAttribute.Position(),
                VertexAttribute.Normal(),
                VertexAttribute(VertexAttributes.Usage.Tangent, 4, ShaderProgram.TANGENT_ATTRIBUTE),
                VertexAttribute.TexCoords(0)
        )

        val terrain = terrainComponent.terrainAsset.terrain

        val numVertices = terrain.vertexResolution * terrain.vertexResolution
        val numIndices = calculateIndicesNum(width, terrain)

        mesh = Mesh(true, numVertices, numIndices, attribs)

        val indices = buildIndices(width, numIndices, terrain)

        val material = Material(ColorAttribute.createDiffuse(Color.RED))

        mesh.setIndices(indices)
        mesh.setVertices(terrain.vertices)

        val meshPart = MeshPart(null, mesh, 0, numIndices, GL20.GL_LINES)
        meshPart.update()

        val mb = ModelBuilder()
        mb.begin()
        mb.part(meshPart, material)
        val model = mb.end()
        modelInstance = ModelInstance(model)
        modelInstance.transform = terrainComponent.modelInstance.transform
    }

    fun updateVertices() {
        mesh.setVertices(terrainComponent.terrainAsset.terrain.vertices)
    }

    abstract fun calculateIndicesNum(width: Int, terrain: Terrain): Int

    abstract fun fillIndices(width: Int, indices: ShortArray, vertexResolution: Int)

    private fun buildIndices(width: Int, numIndices: Int, terrain: Terrain): ShortArray {
        val indices = ShortArray(numIndices)
        val vertexResolution = terrain.vertexResolution

        fillIndices(width, indices, vertexResolution)

        return indices
    }

    override fun dispose() {
        modelInstance.model!!.dispose()
    }

}
