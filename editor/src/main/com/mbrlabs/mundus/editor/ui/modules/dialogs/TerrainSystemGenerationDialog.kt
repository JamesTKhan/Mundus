package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisDialog
import com.mbrlabs.mundus.commons.assets.TerrainAsset
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainManagerComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.ProceduralGenerationWidget

class TerrainSystemGenerationDialog : BaseDialog("Generation") {

    private val root = ProceduralGenerationWidget(false, false, false, false)

    private var terrainManagerComponent : TerrainManagerComponent? = null

    init {
        isResizable = true

        setupUI()
        setupListeners()
    }

    override fun show(stage: Stage?): VisDialog {
        val selectedGameObject = UI.outline.getSelectedGameObject()
        terrainManagerComponent = selectedGameObject!!.findComponentByType(Component.Type.TERRAIN_MANAGER) as TerrainManagerComponent

        val proceduralGeneration = terrainManagerComponent!!.proceduralGeneration

        if (proceduralGeneration != null) {
            root.init(proceduralGeneration)
        } else {
            root.init()
        }

        return super.show(stage)
    }

    override fun close() {
        super.close()
        terrainManagerComponent = null
    }

    private fun setupUI() {
        add(root).expand().fill().row()
    }

    private fun setupListeners() {
        root.generateButtonListener = object : ProceduralGenerationWidget.GenerateButtonListener {
            override fun generate() {
                generateTerrain()
            }
        }
    }

    private fun generateTerrain() {
        val firstTerrain = terrainManagerComponent!!.findFirstTerrainChild()
        if (firstTerrain == null) {
            Mundus.postEvent(LogEvent(LogType.ERROR, "The selected object has not terrain child!"))
            return
        }

        var terrainAsset : TerrainAsset
        var j = 0
        var firstInRowTerrain = firstTerrain
        do {
            var i = 0
            var t = firstInRowTerrain

            do {
                terrainAsset = t.terrainAsset

                root.terraform(i, j, t)
                terrainAsset.applyDependencies()

                t = t.leftNeighbor
                i++
            } while (t != null)

            firstInRowTerrain = firstInRowTerrain.topNeighbor
            j++
        } while (firstInRowTerrain != null)

        updateProceduralGenerationInfo()
    }

    private fun updateProceduralGenerationInfo() {
        var proceduralGeneration = terrainManagerComponent!!.proceduralGeneration

        if (proceduralGeneration == null) {
            proceduralGeneration = TerrainManagerComponent.ProceduralGeneration()
            proceduralGeneration.minHeight = root.getMinHeightValue()
            proceduralGeneration.maxHeight = root.getMaxHeightValue()
            terrainManagerComponent!!.proceduralGeneration = proceduralGeneration
        }

        proceduralGeneration.noiseModifiers.clear()
        root.uploadNoiseModifiers(proceduralGeneration.noiseModifiers)
    }

}
