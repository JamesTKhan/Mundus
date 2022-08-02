package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush

/**
 * @author JamesTKhan
 * @version July 17, 2022
 */
class TerrainSmoothTab(parent: TerrainComponentWidget) : Tab(false, false) {

    private val table = VisTable()
    private val brushGrid: TerrainBrushGrid

    init {
        table.align(Align.left)

        brushGrid = TerrainBrushGrid(parent, TerrainBrush.BrushMode.SMOOTH)
        table.add(brushGrid).expand().fill().row()
    }

    override fun getTabTitle(): String {
        return "Smooth"
    }

    override fun getContentTable(): Table {
        return table
    }

}