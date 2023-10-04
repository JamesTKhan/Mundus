package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent

/**
 * An event to indicate that a terrain's LoD needs to be built/rebuilt.
 * @author JamesTKhan
 * @version October 04, 2023
 */
class TerrainLoDRebuildEvent(val terrainComponent: TerrainComponent) {
    interface TerrainLoDRebuildEventListener {
        @Subscribe
        fun onTerrainLoDRebuild(event: TerrainLoDRebuildEvent)
    }
}