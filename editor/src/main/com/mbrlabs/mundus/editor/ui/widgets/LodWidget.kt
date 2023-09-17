package com.mbrlabs.mundus.editor.ui.widgets

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.TerrainAsset

/**
 *
 * Widget to diplay terrain LOD info.
 */ //TODO: Make pretty
class LodWidget(var asset : TerrainAsset) : VisTable() {
    private val lodLabel: VisLabel = VisLabel()
    private val root: VisTable = VisTable()

    init {
        setupUI()
    }

    private fun setupUI() {

        add(root).grow().row()
        root.add(lodLabel).grow()
    }

    private fun updateUI() {
        lodLabel.setText("LOD Levels:" + asset.terrain.lodLevels + " / Current LOD Multiplier: " + (asset.terrain.currentLod + 1))
    }

    override fun act(delta: Float) {
        super.act(delta)
        updateUI()
    }
}