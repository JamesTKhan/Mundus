package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent

class TerrainVerticesChangedEvent(val terrainComponent: TerrainComponent) {
    interface TerrainVerticesChangedEventListener {
        @Subscribe
        fun onTerrainVerticesChanged(event: TerrainVerticesChangedEvent)
    }
}
