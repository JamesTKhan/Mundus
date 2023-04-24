package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush

abstract class BaseBrushTab(private val parent: TerrainComponentWidget,
                            private val mode: TerrainBrush.BrushMode)
    : Tab(false, false) {

    protected val terrainBrushGrid: TerrainBrushGrid = TerrainBrushGrid(parent, mode);

    override fun onShow() {
        terrainBrushGrid.setupButtonStyleForSelectedBrush()
    }

    /**
     * Clears selection.
     */
    override fun onHide() {
        super.onHide()
        terrainBrushGrid.clearSelectedButtonStyle()
    }

}
