/*
 * Copyright (c) 2022. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.commons.terrain.attributes.TerrainLayerAttribute
import com.mbrlabs.mundus.commons.terrain.layers.HeightTerrainLayer
import com.mbrlabs.mundus.commons.terrain.layers.SlopeTerrainLayer
import com.mbrlabs.mundus.commons.terrain.layers.TerrainLayer
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.widgets.TerrainLayerWidget

/**
 * @author JamesTKhan
 * @version November 04, 2022
 */
class TerrainLayerTab(private val parentWidget: TerrainComponentWidget) : Tab(false, false) {

    private val table = VisTable()
    private val layerTable = VisTable()

    init {
        layerTable.defaults().pad(4f)

        setupUI()

        buildLayerTable()
        table.add(layerTable).fillX().expandX()
    }

    /**
     * Clears and rebuilds the Layer Table that contains all the layer sections and fields
     */
    private fun buildLayerTable() {
        layerTable.clear()

        val terrain = parentWidget.component.terrainAsset.terrain

        if (terrain.terrainTexture.has(TerrainLayerAttribute.HeightLayer)) {
            val attr = terrain.terrainTexture.get(TerrainLayerAttribute.HeightLayer) as TerrainLayerAttribute
            layerTable.add(VisLabel("Height Layers")).row()
            layerTable.addSeparator()
            addLayerSection(attr.terrainLayers!!, terrain)
        }

        if (terrain.terrainTexture.has(TerrainLayerAttribute.SlopeLayer)) {
            val attr = terrain.terrainTexture.get(TerrainLayerAttribute.SlopeLayer) as TerrainLayerAttribute
            layerTable.add(VisLabel("Slope Layers")).row()
            layerTable.addSeparator()
            addLayerSection(attr.terrainLayers!!, terrain)
        }

        layerTable.pack()
    }

    private fun addLayerSection(terrainLayers: Array<TerrainLayer>, terrain: Terrain) {
        for (i in 0 until terrainLayers.size) {
            val layer = terrainLayers[i]

            val widget = TerrainLayerWidget(layer, terrain, i)
            layerTable.add(widget).row()

            // Layer removed listener
            widget.setListener(object : TerrainLayerWidget.OnLayerRemovedListener {
                override fun onLayerRemoved(layer: TerrainLayer) {
                    removeTerrainLayerFromAttribute(layer)
                    buildLayerTable()
                }
            })

            // Layer swapped listener
            widget.setListener(object : TerrainLayerWidget.OnLayerSwapListener {
                override fun onLayerSwap(layer: TerrainLayer, swapDirection: Int) {
                    swapLayers(layer, swapDirection)
                    buildLayerTable()
                }
            })
        }
    }

    private fun setupUI() {
        val addHeightLayer = VisTextButton("Add Height Layer")
        addHeightLayer.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                UI.assetSelectionDialog.show(false,
                    AssetTextureFilter(),
                    object : AssetPickerDialog.AssetPickerListener {
                        override fun onSelected(asset: Asset?) {
                            val terrain = parentWidget.component.terrainAsset.terrain
                            asset as TextureAsset

                            var attr =
                                terrain.terrainTexture.get(TerrainLayerAttribute.HeightLayer) as TerrainLayerAttribute?
                            if (attr == null) {
                                attr = TerrainLayerAttribute(TerrainLayerAttribute.HeightLayer)
                            }

                            val sizeFactor =
                                if (attr.terrainLayers.size > 0) (attr.terrainLayers.size + 1) / 10f else 0f
                            val suggestedMin = (terrain.maxHeight + terrain.minHeight) * (.2f + sizeFactor)
                            val suggestedMax = (terrain.maxHeight + terrain.minHeight) * (.5f + sizeFactor)

                            attr.terrainLayers.add(
                                HeightTerrainLayer(
                                    asset,
                                    suggestedMin,
                                    suggestedMax
                                )
                            )

                            terrain.terrainTexture.set(attr)
                            buildLayerTable()
                        }
                    })
            }
        })

        val addSlopeLayer = VisTextButton("Add Slope Layer")
        addSlopeLayer.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                UI.assetSelectionDialog.show(false, AssetTextureFilter(), object : AssetPickerDialog.AssetPickerListener {
                        override fun onSelected(asset: Asset?) {
                            val terrain = parentWidget.component.terrainAsset.terrain
                            asset as TextureAsset

                            var attr =
                                terrain.terrainTexture.get(TerrainLayerAttribute.SlopeLayer) as TerrainLayerAttribute?
                            if (attr == null) {
                                attr = TerrainLayerAttribute(TerrainLayerAttribute.SlopeLayer)
                            }

                            val sizeFactor = if (attr!!.terrainLayers.size > 0) (attr!!.terrainLayers.size + 1) / 10f else 0f
                            val suggestedMin = (terrain.maxHeight + terrain.minHeight) * (.1f + sizeFactor)
                            val suggestedMax = (terrain.maxHeight + terrain.minHeight) * (.5f + sizeFactor)

                            attr!!.terrainLayers.add(SlopeTerrainLayer(asset, suggestedMin, suggestedMax, 5f))

                            terrain.terrainTexture.set(attr)
                            buildLayerTable()
                        }
                    })
            }
        })

        val btnTable = VisTable()
        btnTable.defaults().pad(4f)
        btnTable.add(addHeightLayer)
        btnTable.add(addSlopeLayer)

        table.add(btnTable).row()
    }

    private fun swapLayers(layer: TerrainLayer, swapDirection: Int) {
        val terrain = parentWidget.component.terrainAsset.terrain

        var attr: TerrainLayerAttribute? = null

        if (layer is SlopeTerrainLayer) {
            attr = terrain.terrainTexture.get(TerrainLayerAttribute.SlopeLayer) as TerrainLayerAttribute
        } else if (layer is HeightTerrainLayer) {
            attr = terrain.terrainTexture.get(TerrainLayerAttribute.HeightLayer) as TerrainLayerAttribute
        }

        if (attr == null) return

        val index = attr.terrainLayers.indexOf(layer, true)

        // If the request is to swap "upwards" but its already at index 0, do nothing
        if (index == 0 && swapDirection < 0) return

        // If the request is to swap "downwards" but its already at the bottom, do nothing
        if (index + swapDirection >= attr.terrainLayers.size) return

        attr.terrainLayers.swap(index, index + swapDirection)
    }

    /**
     * Removes the layer from the Terrains Layer Attribute
     * and removes the attribute if the layer array is empty after removal
     */
    private fun removeTerrainLayerFromAttribute(layer: TerrainLayer) {
        val terrain = parentWidget.component.terrainAsset.terrain

        var attr: TerrainLayerAttribute? = null
        var mask: Long = 0

        if (layer is SlopeTerrainLayer) {
            attr = terrain.terrainTexture.get(TerrainLayerAttribute.SlopeLayer) as TerrainLayerAttribute
            mask = TerrainLayerAttribute.SlopeLayer
        } else if (layer is HeightTerrainLayer) {
            attr = terrain.terrainTexture.get(TerrainLayerAttribute.HeightLayer) as TerrainLayerAttribute
            mask = TerrainLayerAttribute.HeightLayer
        }

        if (attr == null) return

        attr.terrainLayers.removeValue(layer as TerrainLayer?, true)
        if (attr.terrainLayers.isEmpty) terrain.terrainTexture.remove(mask)
    }

    override fun getTabTitle(): String {
        return "Layers"
    }

    override fun getContentTable(): Table {
        return table
    }
}