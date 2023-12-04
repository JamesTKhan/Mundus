/*
 * Copyright (c) 2023. See AUTHORS file.
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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.ModelAsset
import com.mbrlabs.mundus.commons.assets.TerrainObjectLayerAsset
import com.mbrlabs.mundus.commons.utils.TextureProvider
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetModelFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.utils.Colors
import java.io.IOException

class TerrainObjectLayerWidget(var asset: TerrainObjectLayerAsset, var allowChange: Boolean = true) : VisTable() {

    private val projectManager: ProjectManager = Mundus.inject()

    private val layerNameLabel: VisLabel = VisLabel()
    private val editBtn: VisTextButton = VisTextButton("Edit")
    private val duplicatedBtn: VisTextButton = VisTextButton("Duplicate")
    private val changedBtn: VisTextButton = VisTextButton("Change")

    internal val textureGrid = TextureGrid<TmpTexture>(40, 5)
    private val addObjectBtn = VisTextButton("Add Object")

    private val root = VisTable()

    init {
        layerNameLabel.color = Colors.TEAL
        layerNameLabel.wrap = true

        val description = "TODO"
        val descLabel = ToolTipLabel("Object Layer", description)
        root.add(descLabel).expandX().fillX().row()
        root.addSeparator()

        val layerTable = VisTable()
        layerTable.add(layerNameLabel).grow()
        layerTable.add(editBtn).padLeft(4f).right()
        layerTable.add(duplicatedBtn).padLeft(4f).right()
        if (allowChange)
            layerTable.add(changedBtn).padLeft(4f).right().row()
        root.add(layerTable).expandX().fillX().row()

        layerNameLabel.setText(asset.name)

        // Objects
        root.add(VisLabel("Objects:")).padLeft(5f).left().row()
        textureGrid.background = VisUI.getSkin().getDrawable("menu-bg")
        root.add(textureGrid).expand().fill().pad(5f).row()

        // Add objects
        root.add(addObjectBtn).padRight(5f).right().row()

        add(root).expand().fill()

        setupListeners()
        setupTextureGrid()
    }

    private fun setupListeners() {
        if (allowChange) {
            changedBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    // TODO
                }
            })
        }

        addObjectBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.assetSelectionDialog.show(
                        false,
                        AssetModelFilter(),
                        object : AssetPickerDialog.AssetPickerListener {
                            override fun onSelected(asset: Asset?) {
                                try {
                                    addModel(asset as ModelAsset)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    UI.toaster.error("Error while TODO")
                                }
                            }
                        }
                )
            }
        })

        duplicatedBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // TODO
            }
        })

        editBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Mundus.postEvent(AssetSelectedEvent(asset))
                UI.docker.assetsDock.setSelected(asset)
            }
        })
    }

    fun setTerrainObjectLayerAsset(objectLayerAsset: TerrainObjectLayerAsset) {
        layerNameLabel.setText(objectLayerAsset.name)
//        layerChangedListener?.layerChanged(objectAsset)
        this@TerrainObjectLayerWidget.asset = objectLayerAsset
        setupTextureGrid()
    }

    private fun setupTextureGrid() {
        textureGrid.setListener { texture, leftClick ->
            val tex = texture as TmpTexture
//            if (leftClick) {
//                TerrainBrush.setPaintChannel(tex.channel)
//            } else {
//                rightClickMenu.setChannel(tex.channel)
//                rightClickMenu.show()
//            }
        }

        setTexturesInUiGrid()
    }

    private fun setTexturesInUiGrid() {
        textureGrid.removeTextures()

        for (model in asset.models) {
            textureGrid.addTexture(TmpTexture(model))
        }
    }

    @Throws(IOException::class)
    private fun addModel(modelAsset: ModelAsset) {
        val assetManager = projectManager.current().assetManager

        val terrainObjectLayerAsset = asset
        assetManager.addModifiedAsset(terrainObjectLayerAsset)

        terrainObjectLayerAsset.addModel(modelAsset)

        textureGrid.addTexture(TmpTexture(modelAsset))
    }

    // TODO temporary class until thumbnail PR won't be merged
    class TmpTexture(modelAsset: ModelAsset) : TextureProvider {

        private val texture: Texture

        init {
            val pixmap = Pixmap(50, 50, Pixmap.Format.RGBA8888)
            pixmap.setColor(Color(modelAsset.name.hashCode()))
            pixmap.fill()

            texture = Texture(pixmap)
            pixmap.dispose()
        }

        override fun getTexture(): Texture = texture
    }
}
