package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editorcommons.Subscribe

class TerrainNewNeighborEvent(val terrainComponent: TerrainComponent) {

    interface TerrainNewNeighborEventListener {
        @Subscribe
        fun onNewTerrainNeighbor(event: TerrainNewNeighborEvent)
    }
}
