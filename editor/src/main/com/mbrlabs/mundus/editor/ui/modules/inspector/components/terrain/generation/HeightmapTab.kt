package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.generation

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.TerrainHeightCommand
import com.mbrlabs.mundus.editor.terrain.Terraformer
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.utils.isImage

class HeightmapTab(private val terrainComponent: TerrainComponent) : Tab(false, false) {

    private val root = VisTable()

    private val hmInput = FileChooserField()
    private val loadHeightMapBtn = VisTextButton("Load heightmap")
    private val loadHeightMapMaxHeight = FloatFieldWithLabel("Min/Max height", -1, true)

    private val history: CommandHistory = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        root.align(Align.left)

        root.add(loadHeightMapMaxHeight).pad(5f).left().fillX().expandX().row()
        root.add(hmInput).pad(5f).left().expandX().fillX().row()
        root.add(loadHeightMapBtn).pad(5f).right().row()

        setupListeners()
    }

    override fun getTabTitle(): String = "Heightmap"

    override fun getContentTable(): Table = root

    private fun setupListeners() {
        loadHeightMapBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val hm = hmInput.file
                val max = loadHeightMapMaxHeight.float

                if (max == 0f) loadHeightMapMaxHeight.text = "100" // if min/max left blank, set to 100

                if (hm != null && hm.exists() && isImage(hm)) {
                    loadHeightMap(hm)
                    projectManager.current().assetManager.addModifiedAsset(terrainComponent.terrainAsset)
                } else {
                    Dialogs.showErrorDialog(UI, "Please select a heightmap image")
                }
            }
        })
    }

    private fun loadHeightMap(heightMap: FileHandle) {
        val terrain = terrainComponent.terrainAsset.terrain
        val command = TerrainHeightCommand(terrain)
        command.setHeightDataBefore(terrain.heightData)

        val minMax = loadHeightMapMaxHeight.float

        val originalMap = Pixmap(heightMap)

        // scale pixmap if it doesn't fit the terrainAsset
        if (originalMap.width != terrain.vertexResolution || originalMap.height != terrain.vertexResolution) {
            val scaledPixmap = Pixmap(terrain.vertexResolution, terrain.vertexResolution,
                    originalMap.format)
            scaledPixmap.drawPixmap(originalMap, 0, 0, originalMap.width, originalMap.height, 0, 0,
                    scaledPixmap.width, scaledPixmap.height)
            originalMap.dispose()
            Terraformer.heightMap(terrainComponent).maxHeight(minMax).map(scaledPixmap).terraform()
            scaledPixmap.dispose()
        } else {
            Terraformer.heightMap(terrainComponent).maxHeight(minMax).map(originalMap).terraform()
            originalMap.dispose()
        }

        command.setHeightDataAfter(terrain.heightData)
        history.add(command)
    }
}
