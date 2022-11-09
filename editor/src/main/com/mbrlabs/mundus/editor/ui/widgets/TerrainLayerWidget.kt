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

package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.commons.terrain.layers.HeightTerrainLayer
import com.mbrlabs.mundus.commons.terrain.layers.SlopeTerrainLayer
import com.mbrlabs.mundus.commons.terrain.layers.TerrainLayer
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.utils.Fa

/**
 * @author JamesTKhan
 * @version November 06, 2022
 */
class TerrainLayerWidget(var layer: TerrainLayer, var terrain: Terrain, var index: Int) : VisTable() {

    private var innerTable = VisTable()
    private val textureGrid = TextureGrid<TerrainLayer>(60, 5)
    private val rightClickMenu = TextureRightClickMenu()

    var onLayerRemovedListener: OnLayerRemovedListener? = null
    var onLayerSwapListener: OnLayerSwapListener? = null

    /**
     * Called when the X button is pressed to remove the layer is pressed
     */
    interface OnLayerRemovedListener {
        fun onLayerRemoved(layer: TerrainLayer)
    }

    /**
     * Called when the up/down arrows are pressed
     */
    interface OnLayerSwapListener {
        fun onLayerSwap(layer: TerrainLayer, swapDirection: Int)
    }

    init {
        innerTable.defaults().left().pad(2f)
        textureGrid.background = VisUI.getSkin().getDrawable("menu-bg")
        textureGrid.addTexture(layer)

        textureGrid.setListener { _, leftClick ->
            if (!leftClick) {
                rightClickMenu.show()
            }
        }

        setupUI()
    }

    private fun setupUI() {
        val patch = VisUI.getSkin().getDrawable("window")
        background = patch

        addHeaderButtons()

        val nameField = VisTextField()

        if (layer.name == null) {
            layer.name = createLayerName(layer)
        }

        nameField.text = layer.name
        nameField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                layer.name = nameField.text
            }
        })
        innerTable.add(ToolTipLabel("Name", "Name of the layer for convenience."))
        innerTable.add(nameField).row()

        if (layer is SlopeTerrainLayer) {

            val slopeStrengthSlider = ImprovedSlider(0.0f, 10.0f, 0.1f)
            slopeStrengthSlider.value = (layer as SlopeTerrainLayer).strength
            slopeStrengthSlider.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    (layer as SlopeTerrainLayer).strength = slopeStrengthSlider.value
                }
            })
            innerTable.add(ToolTipLabel("Slope Factor", "This value is multiplied to the surface normal.\n" +
                    "The higher the value, the more visible the slope texture will become.\n" +
                    "Higher values will result in the slope texture appearing on smaller inclines/angles. "))
            innerTable.add(slopeStrengthSlider).left().row()

        }

        if (layer is HeightTerrainLayer) {

            val heightStep = Math.abs(terrain.minHeight + terrain.maxHeight) / 20f

            val minHeightSlider = ImprovedSlider(terrain.minHeight, terrain.maxHeight, heightStep)
            minHeightSlider.value = (layer as HeightTerrainLayer).minHeight
            minHeightSlider.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    (layer as HeightTerrainLayer).minHeight = minHeightSlider.value
                }
            })
            innerTable.add(ToolTipLabel("Height Blend Start", "The height at which this texture starts blending."))
            innerTable.add(minHeightSlider).left().row()

            val maxHeightSlider = ImprovedSlider(terrain.minHeight, terrain.maxHeight, heightStep)
            maxHeightSlider.value = (layer as HeightTerrainLayer).maxHeight
            maxHeightSlider.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    (layer as HeightTerrainLayer).maxHeight = maxHeightSlider.value
                }
            })
            innerTable.add(ToolTipLabel("Height Blend End", "The height at which this texture is fully blended.\n" +
                    "The further apart this value is, the more gradually the texture fades in."))
            innerTable.add(maxHeightSlider).left().row()

        }

        innerTable.add(textureGrid).expandX().fillX().colspan(2)

        add(innerTable).row()
    }

    private fun createLayerName(layer: TerrainLayer): String {
        return if (layer is SlopeTerrainLayer) {
            "Slope Layer " + (index + 1)
        } else {
            "Height Layer " + (index + 1)
        }
    }

    private fun addHeaderButtons() {
        val buttonTable = VisTable()
        buttonTable.defaults().pad(0f,2f,0f,2f)

        val moveUpBtn = FaTextButton(Fa.CARET_UP)
        buttonTable.add(moveUpBtn)
        moveUpBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onLayerSwapListener?.onLayerSwap(layer, -1)
            }
        })

        val moveDownBtn = FaTextButton(Fa.CARET_DOWN)
        buttonTable.add(moveDownBtn)
        moveDownBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onLayerSwapListener?.onLayerSwap(layer, 1)
            }
        })

        val deleteBtn = FaTextButton(Fa.TIMES)
        buttonTable.add(deleteBtn).row()
        deleteBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onLayerRemovedListener?.onLayerRemoved(layer)
            }
        })

        add(buttonTable).right().row()
    }

    private inner class TextureRightClickMenu : PopupMenu() {
        private val changeTexture = MenuItem("Change texture")

        init {
            addItem(changeTexture)

            changeTexture.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    UI.assetSelectionDialog.show(
                        false,
                        AssetTextureFilter(),
                        object : AssetPickerDialog.AssetPickerListener {
                            override fun onSelected(asset: Asset?) {
                                textureGrid.removeTextures()
                                layer.textureAsset = asset as TextureAsset
                                textureGrid.addTexture(layer)
                            }
                        })
                }
            })
        }

        fun show() {
            showMenu(UI, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
        }
    }

    fun setListener(listener: OnLayerRemovedListener) {
        onLayerRemovedListener = listener
    }

    fun setListener(listener: OnLayerSwapListener) {
        onLayerSwapListener = listener
    }

}