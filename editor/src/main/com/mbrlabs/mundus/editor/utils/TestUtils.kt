/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import java.util.*

/**
 * @author Marcus Brummer
 * *
 * @version 07-12-2015
 */
object TestUtils {

    fun createABunchOfModelsOnTheTerrain(count: Int, model: Model, terrainComponent: TerrainComponent): Array<ModelInstance> {
        val boxInstances = Array<ModelInstance>()
        val rand = Random()

        val tv3 = Vector3()

        for (i in 0..count - 1) {
            val mi = ModelInstance(model)
            terrainComponent.modelInstance.transform.getTranslation(tv3)
            mi.transform.setTranslation(tv3)
            val x = terrainComponent.terrainAsset.terrain.terrainWidth * rand.nextFloat()
            val z = terrainComponent.terrainAsset.terrain.terrainDepth * rand.nextFloat()
            val y = terrainComponent.terrainAsset.terrain.getHeightAtWorldCoord(x, z, terrainComponent.modelInstance.transform)
            mi.transform.translate(x, y, z)
            boxInstances.add(mi)
        }

        return boxInstances
    }

}
