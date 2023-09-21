package com.mbrlabs.mundus.editor.ui.modules.dialogs.terrain

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.ElevationModifier
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.NoiseModifier
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.TerrainModifier
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.AddTerrainChunksDialog
import com.mbrlabs.mundus.editor.ui.modules.dialogs.NoiseModifierDialog
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.IntegerFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.NoiseGeneratorWidget
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel

/**
 * @author JamesTKhan
 * @version July 03, 2023
 */
class ProceduralTerrainTab(var dialog: AddTerrainChunksDialog) : Tab(false, false) {

    private val root = VisTable()

    private val vertexResolution = IntegerFieldWithLabel("", -1, false)
    private val terrainWidth = IntegerFieldWithLabel("", -1, false)
    private val minHeight = FloatFieldWithLabel("", -1, true)
    private val maxHeight = FloatFieldWithLabel("", -1, true)
    private val gridX = IntegerFieldWithLabel("", -1, false)
    private val gridZ = IntegerFieldWithLabel("", -1, false)
    private val lodSize = IntegerFieldWithLabel("", -1, false)
    private val lodThreshold = FloatFieldWithLabel("", -1, false)

    private lateinit var modifierTable: VisTable
    private val generateBtn = VisTextButton("Generate Terrain")
    private val noiseGeneratorWidget : NoiseGeneratorWidget = NoiseGeneratorWidget()
    private val name = VisTextField("Terrain")

    init {
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {
        // terraform btn
        generateBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                noiseGeneratorWidget.generator
                    .minHeight(minHeight.float)
                    .maxHeight(maxHeight.float)

                dialog.createTerrainChunk(vertexResolution.int, terrainWidth.int, gridX.int, gridZ.int, name.text, lodSize.int, lodThreshold.float)
            }
        })

        vertexResolution.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                noiseGeneratorWidget.setNoiseTextureWidth(vertexResolution.int)
            }
        })
    }

    private fun setupUI() {
        vertexResolution.text = Terrain.DEFAULT_UV_SCALE.toString()
        terrainWidth.text = Terrain.DEFAULT_SIZE.toString()

        minHeight.text = (-50f).toString()
        maxHeight.text = 50f.toString()

        gridX.text = "2"
        gridZ.text = "2"

        lodSize.text = Terrain.DEFAULT_LODS.toString()
        lodThreshold.text = Terrain.DEFAULT_LOD_THRESHOLD.toString()

        root.padTop(6f).padRight(6f).padBottom(22f)

        // left table
        val leftTable = VisTable()
        leftTable.defaults().pad(4f)
        leftTable.left().top()

        root.add(VisLabel("Terrain Settings")).row()

        leftTable.add(VisLabel("Name: ")).left().padBottom(10f)
        leftTable.add(name).fillX().expandX().row()

        leftTable.add(
            ToolTipLabel("Vertex resolution: ", "This will determine the vertices count when squared. 180 = 32,400 vertices. \n" +
                    "The default value (or lower) is recommended for performance. \nSettings this over 180 may cause issues on some devices.")
        ).left()
        leftTable.add(vertexResolution).fillX().expandX().row()
        leftTable.add(ToolTipLabel("Terrain width: ", "Size of the terrain, in meters.")).left()
        leftTable.add(terrainWidth).fillX().expandX().row()

        leftTable.add(ToolTipLabel("Min height", "The minimum height any point on the generated terrain will have. Can be negative")).left()
        leftTable.add(minHeight).left().row()
        leftTable.add(ToolTipLabel("Max height", "The maximum height any point on the generated terrain will have.")).left()
        leftTable.add(maxHeight).left().row()

        leftTable.add(ToolTipLabel("X Iterations", "The number of Terrain Chunks to create on the X axis")).left()
        leftTable.add(gridX).left().row()
        leftTable.add(ToolTipLabel("Z Iterations", "The number of Terrain Chunks to create on the Z axis")).left()
        leftTable.add(gridZ).left().row()

        leftTable.add(ToolTipLabel("LOD Levels", "The number of reduced resolution LOD models to create")).left()
        leftTable.add(lodSize).left().row()
        leftTable.add(ToolTipLabel("LOD Draw Distance", "First distance between camara and terrain when the renderer will switch to the next lower LOD model")).left()
        leftTable.add(lodThreshold).left().row()

        leftTable.add(generateBtn).fillX()

        root.add(leftTable).top().fillX().expandX()
        root.addSeparator(true)

        // Center Table
        root.add(noiseGeneratorWidget).pad(4f).fillX().expandX()

        modifierTable = VisTable()
        buildModifierTable()

        root.addSeparator(true)
        root.add(modifierTable).top()
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

    fun terraform(xOffset: Int, yOffset: Int, terrain: TerrainComponent) {
        noiseGeneratorWidget.generator.offset(xOffset, yOffset).setTerrain(terrain.terrainAsset.terrain).terraform()
    }

    override fun getTabTitle(): String {
        return "Procedural Terrain"
    }

    override fun getContentTable(): Table {
        return root
    }
}