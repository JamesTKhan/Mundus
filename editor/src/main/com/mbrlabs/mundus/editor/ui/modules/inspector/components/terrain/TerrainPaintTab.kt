/*
 * Copyright (c) 2016. See AUTHORS file.
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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.terrain.SplatTexture
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.assets.MetaSaver
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.ui.widgets.TextureGrid
import com.mbrlabs.mundus.editor.utils.Log
import java.io.IOException

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
class TerrainPaintTab(private val parentWidget: TerrainComponentWidget) : BaseBrushTab(parentWidget, TerrainBrush.BrushMode.PAINT) {

    companion object {
        private val TAG = TerrainPaintTab::class.java.simpleName
    }

    private val root = VisTable()
    private val addTextureBtn = VisTextButton("Add Texture")
    private val textureGrid = TextureGrid<SplatTexture>(40, 5)
    private val rightClickMenu = TextureRightClickMenu()

    private val projectManager: ProjectManager = Mundus.inject()
    private val metaSaver: MetaSaver = Mundus.inject()

    init {
        root.align(Align.left)

        // brushes
        root.add(terrainBrushGrid).expand().fill().padBottom(5f).row()

        // textures
        root.add(VisLabel("Textures:")).padLeft(5f).left().row()
        textureGrid.background = VisUI.getSkin().getDrawable("menu-bg")
        root.add(textureGrid).expand().fill().pad(5f).row()

        // add texture
        root.add(addTextureBtn).padRight(5f).right().row()

        setupAddTextureBrowser()
        setupTextureGrid()
    }

    override fun onShow() {
        super.onShow()

        // At tab open the first (base) texture will be selected
        TerrainBrush.setPaintChannel(SplatTexture.Channel.BASE)
        textureGrid.highlightFirst();
    }

    fun setupAddTextureBrowser() {
        addTextureBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.assetSelectionDialog.show(false, AssetTextureFilter(), object: AssetPickerDialog.AssetPickerListener {
                    override fun onSelected(asset: Asset?) {
                        try {
                            addTexture(asset as TextureAsset)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            UI.toaster.error("Error while creating the splatmap")
                        }
                    }
                })

            }
        })
    }

    @Throws(IOException::class)
    private fun addTexture(textureAsset: TextureAsset) {
        val assetManager = projectManager.current().assetManager

        val terrainAsset = this@TerrainPaintTab.parentWidget.component.terrainAsset
        val terrainTexture = terrainAsset.terrain.terrainTexture

        assetManager.addModifiedAsset(terrainAsset)

        // channel base
        if (terrainAsset.splatBase == null) {
            terrainAsset.splatBase = textureAsset
            terrainAsset.applyDependencies()
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.BASE))
            return
        }

        // create splatmap
        if (terrainAsset.splatmap == null) {
            try {
                val splatmap = assetManager.createPixmapTextureAsset(terrainAsset.meta.terrain.splatMapResolution)
                terrainAsset.splatmap = splatmap
                terrainAsset.applyDependencies()
                metaSaver.save(terrainAsset.meta)
                Mundus.postEvent(AssetImportEvent(splatmap))
            } catch (e: AssetAlreadyExistsException) {
                Log.exception(TAG, e)
                return
            }

        }

        // channel r
        if (terrainAsset.splatR == null) {
            terrainAsset.splatR = textureAsset
            terrainAsset.applyDependencies()
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.R))
            return
        }

        // channel g
        if (terrainAsset.splatG == null) {
            terrainAsset.splatG = textureAsset
            terrainAsset.applyDependencies()
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.G))
            return
        }

        // channel b
        if (terrainAsset.splatB == null) {
            terrainAsset.splatB = textureAsset
            terrainAsset.applyDependencies()
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.B))
            return
        }

        // channel a
        if (terrainAsset.splatA == null) {
            terrainAsset.splatA = textureAsset
            terrainAsset.applyDependencies()
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.A))
            return
        }

        Dialogs.showErrorDialog(UI, "Not more than 5 textures per terrainAsset please :)")
    }

    private fun setupTextureGrid() {
        textureGrid.setListener { texture, leftClick ->
            val tex = texture as SplatTexture
            if (leftClick) {
                TerrainBrush.setPaintChannel(tex.channel)
            } else {
                rightClickMenu.setChannel(tex.channel)
                rightClickMenu.show()
            }
        }

        setTexturesInUiGrid()
    }

    private fun setTexturesInUiGrid() {
        textureGrid.removeTextures()
        val terrainTexture = parentWidget.component.terrainAsset.terrain.terrainTexture
        if (terrainTexture.getTexture(SplatTexture.Channel.BASE) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.BASE))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.R) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.R))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.G) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.G))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.B) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.B))
        }
        if (terrainTexture.getTexture(SplatTexture.Channel.A) != null) {
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.A))
        }
    }

    override fun getTabTitle(): String {
        return "Paint"
    }

    override fun getContentTable(): Table {
        return root
    }

    /**

     */
    private inner class TextureRightClickMenu : PopupMenu() {

        private val removeTexture = MenuItem("Remove texture")
        private val changeTexture = MenuItem("Change texture")
        private val addNormalMap = MenuItem("Add Normal Map")
        private val removeNormalMap = MenuItem("Remove Normal Map")

        private var channel: SplatTexture.Channel? = null

        init {
            addItem(removeTexture)
            addItem(changeTexture)
            addItem(addNormalMap)

            removeTexture.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (channel != null) {
                        val terrain = parentWidget.component.terrainAsset
                        if (channel == SplatTexture.Channel.R) {
                            terrain.splatR = null
                            terrain.splatRNormal = null
                        } else if (channel == SplatTexture.Channel.G) {
                            terrain.splatG = null
                            terrain.splatGNormal = null
                        } else if (channel == SplatTexture.Channel.B) {
                            terrain.splatB = null
                            terrain.splatBNormal = null
                        } else if (channel == SplatTexture.Channel.A) {
                            terrain.splatA = null
                            terrain.splatANormal = null
                        } else {
                            UI.toaster.error("Can't remove the base texture")
                            return
                        }

                        terrain.applyDependencies()
                        setTexturesInUiGrid()
                        projectManager.current().assetManager.addModifiedAsset(terrain)
                    }
                }
            })

            changeTexture.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (channel != null) {

                        UI.assetSelectionDialog.show(false, AssetTextureFilter(), object: AssetPickerDialog.AssetPickerListener {
                            override fun onSelected(asset: Asset?) {
                                if (channel != null) {
                                    val terrain = parentWidget.component.terrainAsset
                                    if (channel == SplatTexture.Channel.BASE) {
                                        terrain.splatBase = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.R) {
                                        terrain.splatR = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.G) {
                                        terrain.splatG = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.B) {
                                        terrain.splatB = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.A) {
                                        terrain.splatA = asset as TextureAsset
                                    }
                                    parentWidget.component.applyMaterial()
                                    terrain.applyDependencies()
                                    setTexturesInUiGrid()
                                    projectManager.current().assetManager.addModifiedAsset(terrain)
                                }
                            }
                        })

                    }
                }
            })

            addNormalMap.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (channel != null) {

                        UI.assetSelectionDialog.show(false, AssetTextureFilter(), object: AssetPickerDialog.AssetPickerListener {
                            override fun onSelected(asset: Asset?) {
                                if (channel != null) {
                                    val terrain = parentWidget.component.terrainAsset
                                    if (channel == SplatTexture.Channel.BASE) {
                                        terrain.splatBaseNormal = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.R) {
                                        terrain.splatRNormal = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.G) {
                                        terrain.splatGNormal = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.B) {
                                        terrain.splatBNormal = asset as TextureAsset
                                    } else if (channel == SplatTexture.Channel.A) {
                                        terrain.splatANormal = asset as TextureAsset
                                    }

                                    terrain.applyDependencies()
                                    projectManager.current().assetManager.addModifiedAsset(terrain)
                                }
                            }
                        })

                    }
                }
            })

            removeNormalMap.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (channel != null) {
                        val terrain = parentWidget.component.terrainAsset
                        if (channel == SplatTexture.Channel.BASE) {
                            terrain.splatBaseNormal = null
                        }
                        if (channel == SplatTexture.Channel.R) {
                            terrain.splatRNormal = null
                        } else if (channel == SplatTexture.Channel.G) {
                            terrain.splatGNormal = null
                        } else if (channel == SplatTexture.Channel.B) {
                            terrain.splatBNormal = null
                        } else if (channel == SplatTexture.Channel.A) {
                            terrain.splatANormal = null
                        }

                        terrain.applyDependencies()
                        projectManager.current().assetManager.addModifiedAsset(terrain)
                    }
                }
            })

        }

        fun setChannel(channel: SplatTexture.Channel) {
            this.channel = channel
        }

        fun show() {
            updateMenuVisibility()
            showMenu(UI, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
        }

        private fun updateMenuVisibility() {
            // Show/Hide remove normal map button conditionally
            var normalMapRemoveVisible = false
            val terrain = parentWidget.component.terrainAsset
            if (channel == SplatTexture.Channel.BASE && terrain.splatBaseNormal != null) {
                normalMapRemoveVisible = true
            } else if (channel == SplatTexture.Channel.R && terrain.splatRNormal != null) {
                normalMapRemoveVisible = true
            } else if (channel == SplatTexture.Channel.G && terrain.splatGNormal != null) {
                normalMapRemoveVisible = true
            } else if (channel == SplatTexture.Channel.B && terrain.splatBNormal != null) {
                normalMapRemoveVisible = true
            } else if (channel == SplatTexture.Channel.A && terrain.splatANormal != null) {
                normalMapRemoveVisible = true
            }

            if (normalMapRemoveVisible && !removeNormalMap.hasParent()) {
                addItem(removeNormalMap)
            } else if (!normalMapRemoveVisible) {
                removeNormalMap.remove()
            }
            pack()
        }

    }

}
