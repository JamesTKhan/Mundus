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

package com.mbrlabs.mundus.editor.tools.terrain

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.mbrlabs.mundus.commons.assets.TerrainObject
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush.TerrainModifyComparison
import com.mbrlabs.mundus.editor.utils.IdUtils

class ObjectTool : RadiusTerrainTool() {

    enum class Action {
        ADDING,
        REMOVING
    }

    companion object {
        var action = Action.ADDING
        var strength = 0.5f
        var randomBetweenVertices = 0f
        var minSpaceBetweenObjects = 0.1f
        var xRotationMin = -1f
        var xRotationMax = -1f
        var yRotationMin = -1f
        var yRotationMax = -1f
        var zRotationMin = -1f
        var zRotationMax = -1f
        var xScaleMin = -1f
        var xScaleMax = -1f
        var yScaleMin = -1f
        var yScaleMax = -1f
        var zScaleMin = -1f
        var zScaleMax = -1f

        fun reset() {
            action = Action.ADDING
            strength = 0.5f
            randomBetweenVertices = 0f
            minSpaceBetweenObjects = 0.1f
            xRotationMin = -1f
            xRotationMax = -1f
            yRotationMin = -1f
            yRotationMax = -1f
            zRotationMin = -1f
            zRotationMax = -1f
            xScaleMin = -1f
            xScaleMax = -1f
            yScaleMin = -1f
            yScaleMax = -1f
            zScaleMin = -1f
            zScaleMax = -1f
        }

        private val addingDistanceComparison: TerrainModifyComparison = TerrainModifyComparison { terrainBrush: TerrainBrush, terrainComponent: TerrainComponent, vertexPos: Vector3, localBrushPos: Vector3? ->
            if (!radiusDistanceComparison.compare(terrainBrush, terrainComponent, vertexPos, localBrushPos)) {
                return@TerrainModifyComparison false
            }

            val terrainObjectsAsset = terrainComponent.terrainAsset.terrainObjectsAsset
            for (i in 0..<terrainObjectsAsset.terrainObjectNum) {
                val terrainObject = terrainObjectsAsset.getTerrainObject(i)
                val terrainObjectPos = terrainObject.position

                val distance = vertexPos.dst(terrainObjectPos)
                if (distance < minSpaceBetweenObjects) {
                    return@TerrainModifyComparison false
                }
            }

            true
        }

        @JvmStatic
        fun shouldGenerate() = MathUtils.random() < 0.25f * strength

        @JvmStatic
        val updater = TerrainBrush.TerrainModifyAction { brush: TerrainBrush, terrainComponent: TerrainComponent, x: Int, z: Int, localBrushPos: Vector3, vertexPos: Vector3 ->
            val terrainObjectsAsset = terrainComponent.terrainAsset.terrainObjectsAsset

            val num = terrainObjectsAsset.terrainObjectNum

            for (i in 0..<num) {
                val terrainObject = terrainObjectsAsset.getTerrainObject(i)
                val terrainObjectPos = terrainObject.position

                if (radiusDistanceComparison.compare(brush, terrainComponent, terrainObjectPos, localBrushPos)) {
                    terrainObjectPos.y = terrainComponent.terrainAsset.terrain.getHeightAtLocalCoord(terrainObjectPos.x, terrainObjectPos.z)
                }
            }
        }

        private val modifier = TerrainBrush.TerrainModifyAction { brush: TerrainBrush, terrainComponent: TerrainComponent, x: Int, z: Int, localBrushPos: Vector3, vertexPos: Vector3 ->
            val modelPos = TerrainBrush.getBrushingModelPos()

            val posX = vertexPos.x + MathUtils.random(-randomBetweenVertices, randomBetweenVertices)
            val posZ = vertexPos.z + MathUtils.random(-randomBetweenVertices, randomBetweenVertices)

            val terrainObject = TerrainObject()
            terrainObject.id = IdUtils.generateUUID()
            terrainObject.layerPos = modelPos
            terrainObject.position = Vector3(posX, terrainComponent.terrainAsset.terrain.getHeightAtLocalCoord(posX, posZ), posZ)
            terrainObject.rotation = createRotation()
            terrainObject.scale = createScale()

            terrainComponent.terrainAsset.terrainObjectsAsset.addTerrainObject(terrainObject)
        }

        private fun createRotation(): Vector3 {
            var x = 0f
            if (0 < xRotationMin && 0 < xRotationMax) {
                if (MathUtils.isEqual(xRotationMin, xRotationMax)) {
                    x = xRotationMin
                } else {
                    x = MathUtils.random(xRotationMin, xRotationMax)
                }
            }

            var y = 0f
            if (0 < yRotationMin && 0 < yRotationMax) {
                if (MathUtils.isEqual(yRotationMin, yRotationMax)) {
                    y = yRotationMin
                } else {
                    y = MathUtils.random(yRotationMin, yRotationMax)
                }
            }

            var z = 0f
            if (0 < zRotationMin && 0 < zRotationMax) {
                if (MathUtils.isEqual(zRotationMin, zRotationMax)) {
                    z = zRotationMin
                } else {
                    z = MathUtils.random(zRotationMin, zRotationMax)
                }
            }

            return Vector3(x, y, z)
        }

        private fun createScale(): Vector3 {
            var x = 1f
            if (0 < xScaleMin && 0 < xScaleMax) {
                if (MathUtils.isEqual(xScaleMin, xScaleMax)) {
                    x = xScaleMin
                } else {
                    x = MathUtils.random(xScaleMin, xScaleMax)
                }
            }

            var y = 1f
            if (0 < yScaleMin && 0 < yScaleMax) {
                if (MathUtils.isEqual(yScaleMin, yScaleMax)) {
                    y = yScaleMin
                } else {
                    y = MathUtils.random(yScaleMin, yScaleMax)
                }
            }

            var z = 1f
            if (0 < zScaleMin && 0 < zScaleMax) {
                if (MathUtils.isEqual(zScaleMin, zScaleMax)) {
                    z = zScaleMin
                } else {
                    z = MathUtils.random(zScaleMin, zScaleMax)
                }
            }

            return Vector3(x, y, z)
        }
    }

    override fun act(brush: TerrainBrush) {
        brush.terrainObject(action, modifier, addingDistanceComparison, radiusDistanceComparison, true)
    }
}
