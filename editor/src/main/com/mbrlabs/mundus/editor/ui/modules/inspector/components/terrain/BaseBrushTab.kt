package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.UI

abstract class BaseBrushTab(private val parent: TerrainComponentWidget,
                            private val mode: TerrainBrush.BrushMode)
    : Tab(false, false) {

    protected val terrainBrushGrid: TerrainBrushGrid = TerrainBrushGrid(parent, mode);
    private val toolManager: ToolManager = Mundus.inject()

    /**
     * Clears selection.
     */
    override fun onHide() {
        super.onHide()

        // Set the active tool to default translate tool if changing tab
        val projectManager: ProjectManager = Mundus.inject()
        if (toolManager.activeTool is TerrainBrush) {
            toolManager.activateTool(toolManager.translateTool)
            projectManager.current().currScene.currentSelection = UI.outline.getSelectedGameObject()
            UI.statusBar.clearMousePos()
        }

        terrainBrushGrid.clearSelectedButtonStyle()
    }

}
