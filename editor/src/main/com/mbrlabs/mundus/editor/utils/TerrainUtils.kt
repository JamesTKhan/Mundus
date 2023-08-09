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

@file:JvmName("TerrainUtils")

package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.assets.TerrainAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.scene3d.components.PickableTerrainComponent

private var tempVI = VertexInfo()

fun createTerrainGO(sg: SceneGraph, goID: Int, goName: String,
                    terrainAsset: TerrainAsset): GameObject {
    val terrainGO = GameObject(sg, null, goID)
    terrainGO.name = goName

    val terrainComponent = PickableTerrainComponent(terrainGO)
    terrainComponent.terrainAsset = terrainAsset
    terrainGO.components.add(terrainComponent)
    terrainComponent.encodeRaypickColorId()

    return terrainGO
}

fun getRayIntersection(terrains: Array<TerrainComponent>, ray: Ray, out: Vector3): Vector3? {
    for (terrain in terrains) {
        val result = getRayIntersection(terrain, ray, out)
        if (result != null) {
            return result
        }
    }
    return null
}

fun getRayIntersection(terrain: TerrainComponent, ray: Ray, out: Vector3): Vector3? {
    val terr = terrain.terrainAsset.terrain

    terr.getRayIntersection(out, ray, terrain.modelInstance.transform)
    if (terr.isOnTerrain(out.x, out.z, terrain.modelInstance.transform)) {
        return out
    }

    return null
}

fun getRayIntersectionAndUp(terrains: Array<TerrainComponent>, ray: Ray): VertexInfo? {
    for (terrain in terrains) {
        val terr = terrain.terrainAsset.terrain
        terr.getRayIntersection(tempVI.position, ray, terrain.modelInstance.transform)
        if (terr.isOnTerrain(tempVI.position.x, tempVI.position.z, terrain.modelInstance.transform)) {
            terr.getNormalAtWordCoordinate(tempVI.normal, tempVI.position.x, tempVI.position.z, terrain.modelInstance.transform)
            return tempVI
        }
    }
    return null
}

