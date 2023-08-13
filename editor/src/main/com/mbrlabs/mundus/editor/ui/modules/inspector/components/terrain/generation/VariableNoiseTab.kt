package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.generation

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.TerrainHeightCommand
import com.mbrlabs.mundus.editor.terrain.Terraformer
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.IntegerFieldWithLabel

class VariableNoiseTab(private val terrainComponent: TerrainComponent) : Tab(false, false) {

    private val root = VisTable()

    private val noiseBtn = VisTextButton("Generate Noise")
    private val noiseType = VisSelectBox<String>()
    private val noiseSeed = IntegerFieldWithLabel("Seed", -1, false)
    private val noiseOctaves = IntegerFieldWithLabel("Octaves", -1, false)
    private val noiseFrequency = FloatFieldWithLabel("Frequency", -1, false)
    private val noiseMinHeight = FloatFieldWithLabel("Min height", -1, true)
    private val noiseMaxHeight = FloatFieldWithLabel("Max height", -1, true)
    private val noiseInvert = FloatFieldWithLabel("Lacurnarity", -1, false)
    private val noiseGain = FloatFieldWithLabel("Gain", -1, false)


    private val history: CommandHistory = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        root.align(Align.left)
        root.add(noiseType).pad(5f).left().fillX().expandX().row()
        root.add(noiseSeed).pad(5f).left().fillX().expandX().row()
        root.add(noiseFrequency).pad(5f).left().fillX().expandX().row()
        root.add(noiseOctaves).pad(5f).left().fillX().expandX().row()
        root.add(noiseGain).pad(5f).left().fillX().expandX().row()
        root.add(noiseInvert).pad(5f).left().fillX().expandX().row()
        root.add(noiseMinHeight).pad(5f).left().fillX().expandX().row()
        root.add(noiseMaxHeight).pad(5f).left().fillX().expandX().row()
        root.add(noiseBtn).pad(5f).right().row()
        noiseType.setItems("Value", "Value Fractal", "Perlin", "Perlin Fractal", "Simplex", "Simplex Fractal",
            "Cellular", "White Noise", "Cubic", "Cubic Fractal", "Foam", "Foam Fractal", "Honey", "Honey Fractal", "Mutant", "Mutant Fractal")
        setupListeners()
    }

    override fun getTabTitle(): String = "Variable Noise"

    override fun getContentTable(): Table = root

    private fun setupListeners() {
        noiseBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val noiseType = noiseType.selected
                val seed = noiseSeed.int
                val frequency = noiseFrequency.float
                val min = noiseMinHeight.float
                val max = noiseMaxHeight.float
                val lacunarity = noiseInvert.float
                val octaves = noiseOctaves.int
                val gain = noiseGain.float
                generateNoise(noiseType, seed, frequency, min, max, lacunarity, octaves, gain)
                projectManager.current().assetManager.addModifiedAsset(terrainComponent.terrainAsset)
            }
        })
    }

    private fun generateNoise(noiseType: String, seed: Int, frequency: Float, min: Float, max: Float, lacunarity: Float, octaves: Int, gain: Float) {
        val terrain = terrainComponent.terrainAsset.terrain
        val command = TerrainHeightCommand(terrain)
        command.setHeightDataBefore(terrain.heightData)

        Terraformer.simplex(terrainComponent).minHeight(min).maxHeight(max).seed(seed).frequency(frequency).octaves(octaves).gain(gain).lacunarity(lacunarity).noiseType(noiseType).terraform()

        command.setHeightDataAfter(terrain.heightData)
        history.add(command)
    }
}