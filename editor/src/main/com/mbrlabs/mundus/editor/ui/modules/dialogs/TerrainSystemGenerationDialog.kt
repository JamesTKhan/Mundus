package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.ui.widgets.NoiseGeneratorWidget

class TerrainSystemGenerationDialog : BaseDialog("Generation") {

    private val noiseGeneratorWidget : NoiseGeneratorWidget = NoiseGeneratorWidget()

    init {
        isResizable = true

        setupUI()
    }

    private fun setupUI() {
        val root = VisTable()

        root.padTop(6f).padRight(6f).padBottom(22f)

        root.add(noiseGeneratorWidget).pad(4f).fillX().expandX()

        add(root).expand().fill().row()
    }

}
