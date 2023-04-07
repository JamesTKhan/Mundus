package com.mbrlabs.mundus.editor.core.helperlines

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.terrain.Terrain

class RectangleHelperLineObject(width: Int,
                                terrainComponent: TerrainComponent) : HelperLineObject(width, terrainComponent) {

    override fun calculateIndicesNum(width: Int, terrain: Terrain): Int {
        val vertexResolution = terrain.vertexResolution

        return vertexResolution * 2 * ((vertexResolution / width) + 1) * 2
    }

    override fun fillIndices(width: Int, indices: ShortArray, vertexResolution: Int) {
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
}