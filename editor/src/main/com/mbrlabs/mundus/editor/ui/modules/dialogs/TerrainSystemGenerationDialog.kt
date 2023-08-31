package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.NoiseGeneratorWidget
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel

class TerrainSystemGenerationDialog : BaseDialog("Generation") {

    private val minHeight = FloatFieldWithLabel("", -1, true)
    private val maxHeight = FloatFieldWithLabel("", -1, true)

    private val noiseGeneratorWidget : NoiseGeneratorWidget = NoiseGeneratorWidget()

    init {
        isResizable = true

        setupUI()
    }

    private fun setupUI() {
        val root = VisTable()
        root.padTop(6f).padRight(6f).padBottom(22f)

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

        // Center Table
        root.add(noiseGeneratorWidget).pad(4f).fillX().expandX()

        add(root).expand().fill().row()
    }

}
