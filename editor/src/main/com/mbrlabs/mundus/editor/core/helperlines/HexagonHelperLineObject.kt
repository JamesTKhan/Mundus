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

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
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
        var i = 0
        calculate(width, terrain.vertexResolution) { ++i }

        return i
    }

    override fun fillIndices(width: Int, indices: ShortArray, vertexResolution: Int) {
        var i = 0
        calculate(width, vertexResolution) {pos -> indices[i++] = pos}
    }

    override fun calculateCenterOfHelperObjects(): Array<HelperLineCenterObject> {
        val centerOfHelperObjects = Array<HelperLineCenterObject>()

        val terrain = terrainComponent.terrainAsset.terrain
        val vertexResolution = terrain.vertexResolution

        val widthOffset = terrain.terrainWidth.toFloat() / (vertexResolution - 1).toFloat()
        val depthOffset = terrain.terrainDepth.toFloat() / (vertexResolution - 1).toFloat()

        var terrainY = 2 * width * depthOffset
        var cellY = 0

        while (terrainY + 1 <= terrain.terrainDepth) {
            var terrainX = 0f
            var cellX = 0

            while (terrainX + 1 <= terrain.terrainWidth) {
                val y = if (cellX % 2 == 1) terrainY - width * depthOffset else terrainY
                val fullCell = terrainY + width * depthOffset <= terrain.terrainWidth && terrainX + 3 * width * widthOffset <= terrain.terrainWidth

                centerOfHelperObjects.add(HelperLineCenterObject(cellX, cellY, Vector2(terrainX + 1.5f * width * widthOffset, y), fullCell))

                ++cellX
                terrainX += 2 * width * widthOffset
            }

            ++cellY
            terrainY += 2 * width * depthOffset
        }

        return centerOfHelperObjects
    }

    private fun calculate(width: Int, vertexResolution: Int, method: (pos: Short) -> Unit) {
        var patternY = 0

        for (y in 0 until vertexResolution + width step width) {
            var patternX = 0
            for (x in 0 until vertexResolution step width) {
                val mainCurrent = y * vertexResolution + x

                for (pattern in PATTERN.get(patternY).get(patternX)) {
                    var current = mainCurrent
                    for (w in 0 until  width) {
                        val next = getNext(current, pattern, vertexResolution)

                        if (isOnMap(current, vertexResolution) && isOk(current, next, vertexResolution, pattern)) {
                            method.invoke(current.toShort())
                            method.invoke(next.toShort())
                        } else {
                            break
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
            Vector.BOTTOM_RIGHT -> currentRow + 1 == nextRow && isOnMap(next, vertexResolution)
            Vector.TOP_RIGHT -> currentRow == nextRow + 1 && isOnMap(next, vertexResolution)
            Vector.RIGHT -> currentRow == nextRow
        }
    }

    private fun getRow(cell: Int, vertexResolution: Int) = cell / vertexResolution
}
