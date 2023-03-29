package com.mbrlabs.mundus.editor.core.helperlines

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent

class HelperLineObject(terrainComponent: TerrainComponent) {

    var modelInstance: ModelInstance?

    init {
        val attribs = VertexAttributes(
                VertexAttribute.Position(),
                VertexAttribute.Normal(),
                VertexAttribute(VertexAttributes.Usage.Tangent, 4, ShaderProgram.TANGENT_ATTRIBUTE),
                VertexAttribute.TexCoords(0)
        )

        val terrain = terrainComponent.terrainAsset.terrain

        val numVertices = terrain.vertexResolution * terrain.vertexResolution
        val numIndices = 6

        val mesh = Mesh(true, numVertices, numIndices, attribs)

        val indices = ShortArray(numIndices)
        indices[0] = 0
        indices[1] = 1
        indices[2] = 2
        indices[3] = 3
        indices[4] = 4
        indices[5] = 5

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
    }

}
