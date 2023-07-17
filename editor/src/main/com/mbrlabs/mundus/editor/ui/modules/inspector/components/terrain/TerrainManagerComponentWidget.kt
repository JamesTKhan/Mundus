package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.TerrainLayerAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.scene3d.components.TerrainManagerComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetTerrainLayerFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.ComponentWidget

/**
 * @author JamesTKhan
 * @version July 17, 2023
 */
class TerrainManagerComponentWidget(terrainManagerComponent: TerrainManagerComponent) :
    ComponentWidget<TerrainManagerComponent>("Terrain Manager Component", terrainManagerComponent) {

    private val filter: AssetTerrainLayerFilter = AssetTerrainLayerFilter()
    private lateinit var assetPickerListener: AssetPickerDialog.AssetPickerListener

    var root = VisTable()
    private var projectManager : ProjectManager = Mundus.inject()

    private val updateBtn: VisTextButton = VisTextButton("Update all layers")

    init {
        setupUI()
        setupListeners()
    }

    override fun setValues(go: GameObject) {
        val c = go.findComponentByType(Component.Type.TERRAIN_MANAGER)
        if (c != null) {
            component = c as TerrainManagerComponent
        }
    }

    private fun setupUI() {
        collapsibleContent.add(updateBtn).row()
    }

    private fun setupListeners() {
        assetPickerListener = object : AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                val layer = (asset as? TerrainLayerAsset)!!

                Dialogs.showConfirmDialog(
                    UI,
                    "Change Layer",
                    "If the selected layer has less texture channels assigned than existing terrains, Splat map paint data for the exceeding channels will be lost." +
                            "\nDo you want to continue?",
                    arrayOf("Cancel", "Yes"),
                    arrayOf(0, 1)
                ) { r: Int ->
                    if (r == 1) {
                        setTerrainLayerAsset(layer)
                    }
                }.padBottom(20f).pack()
            }
        }

        updateBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.assetSelectionDialog.show(false, filter, assetPickerListener)
            }
        })
    }

    private fun setTerrainLayerAsset(layer: TerrainLayerAsset) {
        val modifiedTerrains = Array<TerrainComponent>()
        component.setTerrainLayerAsset(layer, modifiedTerrains)

        // If the switched layer uses splat textures, we need to create splatmaps for all terrains missing them
        val createSplatMaps = layer.activeLayerCount > 0
        for (terrain in modifiedTerrains) {
            projectManager.current().assetManager.addModifiedAsset(terrain.terrainAsset)

            if (createSplatMaps) {
                projectManager.current().assetManager.createSplatmapForTerrain(terrain)
            }
        }
    }
}