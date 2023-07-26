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

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.terrain.Terrain

class HexagonHelperLineShape(width: Int, terrainComponent: TerrainComponent) : HelperLineShape(width, terrainComponent) {

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
        var i = 0
        calculate(width, terrain.vertexResolution) { ++i }

        return i
    }

    override fun fillIndices(width: Int, indices: ShortArray, vertexResolution: Int) {
        var i = 0
        calculate(width, vertexResolution) {pos -> indices[i++] = pos}
    }

    private fun calculate(width: Int, vertexResolution: Int, method: (pos: Short) -> Unit) {
        val bottomTerrainChunksVertexResolution = calculateBottomTerrainChunksVertexResolution()
        val rightTerrainChunksVertexResolution = calculateRightTerrainChunksVertexResolution()

        val yUsedWidth = bottomTerrainChunksVertexResolution % width
        val xUsedWidth = rightTerrainChunksVertexResolution % width

        val yInit = if (yUsedWidth == 0) 0 else -yUsedWidth
        val xInit = if (xUsedWidth == 0) 0 else -xUsedWidth

        var patternY = ((bottomTerrainChunksVertexResolution - yUsedWidth) / width) % PATTERN.size

        for (y in yInit until vertexResolution + width step width) {
            var patternX = ((rightTerrainChunksVertexResolution - xUsedWidth) / width) % PATTERN.get(patternY).size

            for (x in xInit until vertexResolution step width) {
                val mainCurrent = y * vertexResolution + x

                for (pattern in PATTERN.get(patternY).get(patternX)) {
                    var current = mainCurrent
                    for (w in 0 until  width) {
                        val next = getNext(current, pattern, vertexResolution)

                        if (current >= 0 && next >= 0 && x + w >= 0) {
                            if (isOnMap(current, vertexResolution) && isOk(current, next, vertexResolution, pattern) && isOnMap(next, vertexResolution)) {
                                method.invoke(current.toShort())
                                method.invoke(next.toShort())
                            } else if (!isOk(current, next, vertexResolution, pattern)) {
                                break
                            }
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

    private fun isOnMap(current: Int, vertexResolution: Int): Boolean  = getRow(current, vertexResolution) in 0 until vertexResolution

    private fun isOk(current: Int, next: Int, vertexResolution: Int, pattern: Vector): Boolean {
        val currentRow = getRow(current, vertexResolution)
        val nextRow = getRow(next, vertexResolution)

        return when(pattern) {
            Vector.BOTTOM_RIGHT -> currentRow + 1 == nextRow
            Vector.TOP_RIGHT -> currentRow == nextRow + 1
            Vector.RIGHT -> currentRow == nextRow
        }
    }

    private fun getRow(cell: Int, vertexResolution: Int) = cell / vertexResolution
}
