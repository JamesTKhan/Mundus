package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent

class TerrainAddedEvent(val terrainComponent: TerrainComponent) {

    interface TerrainAddedEventListener {
        @Subscribe
        fun onTerrainAdded(event: TerrainAddedEvent)
    }
}
