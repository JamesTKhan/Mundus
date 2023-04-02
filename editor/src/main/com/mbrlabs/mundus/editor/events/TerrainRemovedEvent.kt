package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent

class TerrainRemovedEvent(val terrainComponent: TerrainComponent) {

    interface TerrainRemovedEventListener {
        @Subscribe
        fun onTerrainRemoved(event: TerrainRemovedEvent)
    }
}
