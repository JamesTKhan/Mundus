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

class HelperLineObject(private val type: HelperLineType,
                       private val width: Int,
                       val terrainComponent: TerrainComponent) : Disposable {

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
        val numIndices = calculateIndicesNum(terrain)

        mesh = Mesh(true, numVertices, numIndices, attribs)

        val indices = buildIndices(numIndices, terrain)

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

    private fun calculateIndicesNum(terrain: Terrain): Int {
        val vertexResolution = terrain.vertexResolution

        if (type == HelperLineType.RECTANGLE) {
            return vertexResolution * 2 * ((vertexResolution / width) + 1) * 2
        } else {
            return 2 * width + 2 * width + 2 * width
        }
    }

    private fun buildIndices(numIndices: Int, terrain: Terrain): ShortArray {
        val indices = ShortArray(numIndices)
        val vertexResolution = terrain.vertexResolution

        if (type == HelperLineType.RECTANGLE) {
            fillRectangleIndices(indices, vertexResolution)
        } else {
            fillHexagonIndices(indices, vertexResolution)
        }

        return indices
    }

    private fun fillRectangleIndices(indices: ShortArray, vertexResolution: Int) {
        var i = -1
        for (y in 0 until vertexResolution step width) {
            for (x in 0 until  vertexResolution - 1) {
                val current = y * vertexResolution + x
                val next = current + 1

                indices[++i] = current.toShort()
                indices[++i] = next.toShort()
            }
        }
        for (y in 0 until vertexResolution step width) {
            for (x in 0 until  vertexResolution - 1) {
                val current = y + vertexResolution * x
                val next = current + vertexResolution

                indices[++i] = current.toShort()
                indices[++i] = next.toShort()
            }
        }
    }

    private fun fillHexagonIndices(indices: ShortArray, vertexResolution: Int) {
        var i = -1

//        for (y in 0 until vertexResolution step width) {
//            for (x in 0 until vertexResolution step 3 * width) {
        for (y in 0 until width step width) {
            for (x in 0 until width step width) {
                val leftVertex = y * vertexResolution + x

                // Bottom left side
                for (j in 0 until width) {
                    val current = leftVertex + j * vertexResolution + j
                    val next = current + vertexResolution + 1

                    if (getRow(next, vertexResolution) == getRow(current, vertexResolution) + 1) {
                        indices[++i] = current.toShort()
                        indices[++i] = next.toShort()
                    }
                }

                // Bottom side
                for (j in width until 2 * width) {
                    val current = leftVertex + width * vertexResolution + j
                    val next = current + 1

                    if (getRow(next, vertexResolution) == getRow(current, vertexResolution)) {
                        indices[++i] = current.toShort()
                        indices[++i] = next.toShort()
                    }
                }

                // Bottom right side
                for (j in 2 * width until 3 * width) {
                    val current = leftVertex + (3 * width - j) * vertexResolution + j
                    val next = current - vertexResolution + 1

                    if (getRow(next, vertexResolution) == getRow(current, vertexResolution) - 1) {
                        indices[++i] = current.toShort()
                        indices[++i] = next.toShort()
                    }
                }

            }
        }
    }

    private fun getRow(cell: Int, vertexResolution: Int) = cell / vertexResolution

    override fun dispose() {
        modelInstance.model!!.dispose()
    }

}
