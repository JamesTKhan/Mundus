package com.mbrlabs.mundus.editor.core.helperlines

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.terrain.Terrain

class HexagonHelperLineObject(width: Int, terrainComponent: TerrainComponent) : HelperLineObject(width, terrainComponent) {

    enum class Vector {
        BOTTOM_RIGHT,
        TOP_RIGHT,
        RIGHT,
    }

    companion object {
        val PATTERN = arrayOf(
                arrayOf(arrayOf(Vector.BOTTOM_RIGHT), arrayOf(),             arrayOf(),                                      arrayOf(Vector.RIGHT)),
                arrayOf(arrayOf(),                    arrayOf(Vector.RIGHT), arrayOf(Vector.TOP_RIGHT, Vector.BOTTOM_RIGHT), arrayOf()            ),
                arrayOf(arrayOf(Vector.TOP_RIGHT),    arrayOf(),             arrayOf(),                                      arrayOf(Vector.RIGHT))
        )
    }

    override fun calculateIndicesNum(width: Int, terrain: Terrain): Int {
        return 28 // TODO calculate
    }

    override fun fillIndices(width: Int, indices: ShortArray, vertexResolution: Int) {
        val vertexResolutionWidth = 8
        val vertexResolutionHeight = 5

        var patternX = 0
        var patternY = 0

        var i = 0

        for (y in 0 until vertexResolutionHeight step width) {
            for (x in 0 until vertexResolutionWidth step width) {
                val mainCurrent = y * vertexResolution + x

                for (pattern in PATTERN.get(patternY).get(patternX)) {
                    var current = mainCurrent
                    for (w in 0 until  width) {
                        val next = getNext(current, pattern, vertexResolution)

                        val ok = when(pattern) {
                            Vector.BOTTOM_RIGHT -> getRow(current, vertexResolution) + 1 == getRow(next, vertexResolution)
                            Vector.TOP_RIGHT -> getRow(current, vertexResolution) == getRow(next, vertexResolution) + 1
                            Vector.RIGHT -> getRow(current, vertexResolution) == getRow(next, vertexResolution)
                        }

                        if (ok && isOnMap(next, vertexResolution)) {
                            indices[i++] = current.toShort()
                            indices[i++] = next.toShort()
                        }

                        current = next

                    }
                }

                patternX = ++patternX % PATTERN.get(patternY).size
            }

            patternY = ++patternY % PATTERN.size
        }
    }

    private fun getNext(current: Int, vector: Vector, vertexResolution: Int): Int {
        return when(vector) {
            Vector.BOTTOM_RIGHT -> current + vertexResolution + 1
            Vector.TOP_RIGHT -> current - vertexResolution + 1
            Vector.RIGHT -> current + 1
        }
    }

    private fun isOnMap(current: Int, vertexResolution: Int): Boolean {
        val row = getRow(current, vertexResolution)

        return row in 0..vertexResolution
    }

    private fun getRow(cell: Int, vertexResolution: Int) = cell / vertexResolution
}
