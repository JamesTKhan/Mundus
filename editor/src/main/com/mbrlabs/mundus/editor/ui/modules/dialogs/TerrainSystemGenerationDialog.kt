package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
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

    private val noiseGeneratorWidget : NoiseGeneratorWidget = NoiseGeneratorWidget()

    private lateinit var modifierTable: VisTable

    init {
        isResizable = true

        setupUI()
    }

    private fun setupUI() {
        val root = VisTable()
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root).expand().fill().row()

        // left table
        val leftTable = VisTable()
        leftTable.defaults().pad(4f)
        leftTable.left().top()

        leftTable.add(ToolTipLabel("Min height", "The minimum height any point on the generated terrain will have. Can be negative")).left()
        leftTable.add(minHeight).left().row()
        leftTable.add(ToolTipLabel("Max height", "The maximum height any point on the generated terrain will have.")).left()
        leftTable.add(maxHeight).left().row()

        root.add(leftTable).top().fillX().expandX()
        root.addSeparator(true)

        // Center table
        root.add(noiseGeneratorWidget).pad(4f).fillX().expandX()

        // Right table
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

}
