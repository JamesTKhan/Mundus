package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.TerrainAsset
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainManagerComponent
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.ElevationModifier
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.NoiseModifier
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.TerrainModifier
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.NoiseGeneratorWidget
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel

class TerrainSystemGenerationDialog : BaseDialog("Generation") {

    private val minHeight = FloatFieldWithLabel("", -1, true)
    private val maxHeight = FloatFieldWithLabel("", -1, true)
    private val generateBtn = VisTextButton("Generate")

    private val noiseGeneratorWidget : NoiseGeneratorWidget = NoiseGeneratorWidget()

    private lateinit var modifierTable: VisTable

    init {
        isResizable = true

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val root = VisTable()
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root).expand().fill().row()

        // TODO load these values from previous settings
        minHeight.text = (-50f).toString()
        maxHeight.text = 50f.toString()

        // left table
        val leftTable = VisTable()
        leftTable.defaults().pad(4f)
        leftTable.left().top()

        leftTable.add(ToolTipLabel("Min height", "The minimum height any point on the generated terrain will have. Can be negative")).left()
        leftTable.add(minHeight).left().row()
        leftTable.add(ToolTipLabel("Max height", "The maximum height any point on the generated terrain will have.")).left()
        leftTable.add(maxHeight).left().row()

        leftTable.add(generateBtn).expandY().fillX().bottom()

        root.add(leftTable).top().fillX().expandX().fillY().expandY()
        root.addSeparator(true)

        // Center table
        root.add(noiseGeneratorWidget).pad(4f).fillX().expandX()

        // Right table
        modifierTable = VisTable()
        buildModifierTable()

        root.addSeparator(true)
        root.add(modifierTable).top()
    }

    private fun setupListeners() {
        generateBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                noiseGeneratorWidget.generator
                        .minHeight(minHeight.float)
                        .maxHeight(maxHeight.float)

                val selectedGameObject = UI.outline.getSelectedGameObject()
                if (selectedGameObject == null) {
                    Mundus.postEvent(LogEvent(LogType.ERROR,"There is no selected game object on Outline!"))
                    return
                }

                val terrainManagerComponent = selectedGameObject.findComponentByType(Component.Type.TERRAIN_MANAGER) as TerrainManagerComponent?
                if (terrainManagerComponent == null) {
                    Mundus.postEvent(LogEvent(LogType.ERROR, "The selected object has not Terrain Manager Component!"))
                    return
                }
                val firstTerrain = terrainManagerComponent.findFirstTerrainChild()
                if (firstTerrain == null) {
                    Mundus.postEvent(LogEvent(LogType.ERROR, "The selected object has not terrain child!"))
                    return
                }

                var terrainAsset : TerrainAsset
                var terrain : Terrain
                var j = 0
                var firstInRowTerrain = firstTerrain
                do {
                    var i = 0
                    var t = firstInRowTerrain

                    do {
                        terrainAsset = t.terrainAsset
                        terrain = terrainAsset.terrain

                        noiseGeneratorWidget.generator.offset(i, j).setTerrain(terrain).terraform()
                        terrainAsset.applyDependencies()

                        t = t.leftNeighbor
                        i++
                    } while (t != null)

                    firstInRowTerrain = firstInRowTerrain.topNeighbor
                    j++
                } while (firstInRowTerrain != null)
            }
        })
    }

    private fun buildModifierTable() {
        modifierTable.clear()

        val addModifierBtn = VisTextButton("Add Modifier")

        modifierTable.defaults().pad(4f)
        modifierTable.left().top()
        modifierTable.add(addModifierBtn).left().row()
        modifierTable.addSeparator().row()

        addModifierBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val mod = ElevationModifier()
                addModifierToList(mod)
                noiseGeneratorWidget.generator.modifiers.add(mod)
            }
        })

        for (mod in noiseGeneratorWidget.generator.modifiers) {
            addModifierToList(mod)
        }
    }

    private fun addModifierToList(mod: TerrainModifier) {
        val button = VisTextButton(mod.name)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (mod is NoiseModifier) {
                    val dialog = NoiseModifierDialog(mod)
                    dialog.show(UI)
                }
                super.clicked(event, x, y)
            }
        })

        modifierTable.add(button).left().row()
    }

}
