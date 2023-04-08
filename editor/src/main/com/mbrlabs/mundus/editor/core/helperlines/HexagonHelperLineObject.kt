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
                arrayOf(arrayOf(Vector.TOP_RIGHT, Vector.BOTTOM_RIGHT), arrayOf(),             arrayOf(),                                      arrayOf(Vector.RIGHT)),
                arrayOf(arrayOf(),                                      arrayOf(Vector.RIGHT), arrayOf(Vector.TOP_RIGHT, Vector.BOTTOM_RIGHT), arrayOf()            )
        )
    }

    override fun calculateIndicesNum(width: Int, terrain: Terrain): Int {
        return Short.MAX_VALUE.toInt() // TODO calculate
    }

    override fun fillIndices(width: Int, indices: ShortArray, vertexResolution: Int) {
        var patternY = 0
        var i = 0

        for (y in 0 until vertexResolution + width step width) {
            var patternX = 0
            for (x in 0 until vertexResolution step width) {
                val mainCurrent = y * vertexResolution + x

                for (pattern in PATTERN.get(patternY).get(patternX)) {
                    var current = mainCurrent
                    for (w in 0 until  width) {
                        val next = getNext(current, pattern, vertexResolution)

                        if (isOnMap(current, vertexResolution) && isOk(current, next, vertexResolution, pattern)) {
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

        println("i: $i")
    }

    private fun getNext(current: Int, vector: Vector, vertexResolution: Int): Int {
        return when(vector) {
            Vector.BOTTOM_RIGHT -> current + vertexResolution + 1
            Vector.TOP_RIGHT -> current - vertexResolution + 1
            Vector.RIGHT -> current + 1
        }
    }

    private fun isOnMap(current: Int, vertexResolution: Int): Boolean  = getRow(current, vertexResolution) in 0 until vertexResolution

    private fun isOk(current: Int, next: Int, vertexResolution: Int, pattern: Vector): Boolean {
        val currentRow = getRow(current, vertexResolution)
        val nextRow = getRow(next, vertexResolution)

        return when(pattern) {
            Vector.BOTTOM_RIGHT -> currentRow + 1 == nextRow && isOnMap(next, vertexResolution)
            Vector.TOP_RIGHT -> currentRow == nextRow + 1 && isOnMap(next, vertexResolution)
            Vector.RIGHT -> currentRow == nextRow
        }
    }

    private fun getRow(cell: Int, vertexResolution: Int) = cell / vertexResolution
}
