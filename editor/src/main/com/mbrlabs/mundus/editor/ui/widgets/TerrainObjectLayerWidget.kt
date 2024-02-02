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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.ModelAsset
import com.mbrlabs.mundus.commons.assets.TerrainObjectLayerAsset
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.utils.TextureProvider
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.assets.AssetModelFilter
import com.mbrlabs.mundus.editor.assets.AssetTerrainObjectLayerFilter
import com.mbrlabs.mundus.editor.assets.EditorModelAsset
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.utils.Colors
import com.mbrlabs.mundus.editor.utils.IdUtils
import java.io.FileNotFoundException
import java.io.IOException

class TerrainObjectLayerWidget(var asset: TerrainObjectLayerAsset, val terrainComponent: TerrainComponent?, var allowChange: Boolean = true) : VisTable() {

    interface LayerChangedListener {
        fun layerChanged(terrainObjectLayerAsset: TerrainObjectLayerAsset)
    }

    /**
     * An optional listener for when the layer is changed.
     */
    var layerChangedListener: LayerChangedListener? = null

    private val projectManager: ProjectManager = Mundus.inject()
    private val toolManager: ToolManager = Mundus.inject()

    private val filter = AssetTerrainObjectLayerFilter()

    private val layerNameLabel: VisLabel = VisLabel()
    private val editBtn: VisTextButton = VisTextButton("Edit")
    private val duplicatedBtn: VisTextButton = VisTextButton("Duplicate")
    private val changedBtn: VisTextButton = VisTextButton("Change")

    internal val textureGrid = TextureGrid<EditorModelAssetWithPosition>(40, 5)
    private val addObjectBtn = VisTextButton("Add Object")

    private val root = VisTable()

    private val rightClickMenu = TerrainObjectRightClickMenu()

    init {
        layerNameLabel.color = Colors.TEAL
        layerNameLabel.wrap = true

        val description = "Terrain object layers determine what objects a terrain uses.\n" +
                "They can be shared with multiple terrains.\n" +
                "Changing an object here will update all terrains using the layer."
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
            val assetPickerListener = object : AssetPickerDialog.AssetPickerListener {
                override fun onSelected(asset: Asset?) {
                    val layer = (asset as? TerrainObjectLayerAsset)!!

                    val oldLayerCount = this@TerrainObjectLayerWidget.asset.activeLayerCount
                    val newLayerCount = layer.activeLayerCount

                    if (oldLayerCount > newLayerCount) {
                        Dialogs.showConfirmDialog(
                                UI,
                                "Change Object Layer",
                                "The new object layer has less models assigned than the current one. Terrain objects for the exceeding models will be lost." +
                                        "\nDo you want to continue?",
                                arrayOf("Cancel", "Yes"),
                                arrayOf(0, 1)
                        ) { r: Int ->
                            if (r == 1) {
                                for (i in newLayerCount ..< oldLayerCount) {
                                    applyDependenciesAfterRemoving(i)
                                }
                                setTerrainObjectLayerAsset(layer)
                            }
                        }.padBottom(20f).pack()
                    } else {
                        setTerrainObjectLayerAsset(layer)
                    }
                }
            }

            changedBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    UI.assetSelectionDialog.show(false, filter, assetPickerListener)
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
                                    addModel(asset as EditorModelAsset)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    UI.toaster.error("Error while adding model object")
                                }
                            }
                        }
                )
            }
        })

        duplicatedBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Dialogs.showInputDialog(UI, "Name:", "", object : InputDialogAdapter() {
                    override fun finished(input: String?) {
                        if (input != null) {
                            try {
                                val newLayer = projectManager.current().assetManager.createTerrainObjectLayerAsset(input)
                                newLayer.duplicateObjectLayerAsset(asset)
                                projectManager.current().assetManager.addModifiedAsset(newLayer)
                                Mundus.postEvent(AssetImportEvent(asset))
                                if (allowChange) {
                                    asset = newLayer
                                    layerNameLabel.setText(asset.name)
                                    layerChangedListener?.layerChanged(asset)
                                    setTexturesInUiGrid()
                                }
                            } catch (e: AssetAlreadyExistsException) {
                                Dialogs.showErrorDialog(UI, "That asset already exists. Try a different name.")
                            } catch (e: FileNotFoundException) {
                                Dialogs.showErrorDialog(UI, "Invalid asset name.")
                            }
                        }
                    }
                })
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
        layerChangedListener?.layerChanged(objectLayerAsset)
        this@TerrainObjectLayerWidget.asset = objectLayerAsset
        setupTextureGrid()
    }

    private fun setupTextureGrid() {
        textureGrid.setListener { texture, leftClick ->
            val tex = texture as EditorModelAssetWithPosition
            if (leftClick) {
                if (terrainComponent != null) {
                    TerrainBrush.setBrushingModelPos(tex.pos)
                    val tool = toolManager.terrainBrushes.first()
                    tool.mode = TerrainBrush.BrushMode.TERRAIN_OBJECT
                    tool.setTerrainComponent(terrainComponent)
                    toolManager.activateTool(tool)
                }
            } else {
                rightClickMenu.show(tex.pos)
            }
        }

        setTexturesInUiGrid()
    }

    private fun setTexturesInUiGrid() {
        textureGrid.removeTextures()

        for ((index, model) in asset.models.withIndex()) {
            textureGrid.addTexture(EditorModelAssetWithPosition(index, model as EditorModelAsset))
        }
    }

    @Throws(IOException::class)
    private fun addModel(modelAsset: EditorModelAsset) {
        val assetManager = projectManager.current().assetManager

        val terrainObjectLayerAsset = asset
        assetManager.addModifiedAsset(terrainObjectLayerAsset)

        terrainObjectLayerAsset.addModel(modelAsset)

        val modelPos = terrainObjectLayerAsset.activeLayerCount - 1
        textureGrid.addTexture(EditorModelAssetWithPosition(modelPos, modelAsset))
    }

    private fun applyDependenciesAfterRemoving(layerPos: Int) {
        projectManager.current().currScene.sceneGraph.findAllByComponent(Component.Type.TERRAIN).forEach {
            val component = it.findComponentByType(Component.Type.TERRAIN) as TerrainComponent

            if (component.terrainAsset.terrainObjectLayerAsset == asset) {
                val terrainObjectsAsset = component.terrainAsset.terrainObjectsAsset
                var modified = false

                for (i in terrainObjectsAsset.terrainObjectNum -1 downTo 0) {
                    val terrainObject = terrainObjectsAsset.getTerrainObject(i)

                    if (terrainObject.layerPos == layerPos) {
                        terrainObjectsAsset.removeObject(i)
                        modified = true
                    } else if (layerPos < terrainObject.layerPos) {
                        terrainObject.layerPos -= 1
                        modified = true
                    }
                }

                if (modified) {
                    component.applyTerrainObjects()
                    projectManager.current().assetManager.addModifiedAsset(component.terrainAsset)
                }
            }
        }
    }

    private fun applyDependenciesAfterChanging(layerPos: Int) {
        projectManager.current().currScene.sceneGraph.findAllByComponent(Component.Type.TERRAIN).forEach {
            val component = it.findComponentByType(Component.Type.TERRAIN) as TerrainComponent

            if (component.terrainAsset.terrainObjectLayerAsset == asset) {
                val terrainObjectsAsset = component.terrainAsset.terrainObjectsAsset
                var modified = false

                for (i in terrainObjectsAsset.terrainObjectNum -1 downTo 0) {
                    val terrainObject = terrainObjectsAsset.getTerrainObject(i)

                    if (terrainObject.layerPos == layerPos) {
                        // If generate new ID then the apply method will remove old terrain objects and will create new objects with new model asset
                        terrainObject.id = IdUtils.generateUUID()
                        modified = true
                    }
                }

                if (modified) {
                    component.applyTerrainObjects()
                    projectManager.current().assetManager.addModifiedAsset(component.terrainAsset)
                }
            }
        }
    }

    class EditorModelAssetWithPosition(val pos: Int, val modeleAsset: EditorModelAsset) : TextureProvider {
        override fun getTexture(): Texture = modeleAsset.texture
    }

    private inner class TerrainObjectRightClickMenu : PopupMenu() {

        private val removeObject = MenuItem("Remove object")
        private val changeObject = MenuItem("Change object")

        private var layerPos = -1

        init {
            addItem(removeObject)
            addItem(changeObject)

            setupSubscriptions()
        }

        fun show(layerPos: Int) {
            this.layerPos = layerPos
            showMenu(UI, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
        }

        private fun setupSubscriptions() {
            removeObject.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    val terrainObjectLayerAsset = asset
                    terrainObjectLayerAsset.removeModel(layerPos)
                    applyDependenciesAfterRemoving(layerPos)
                    setTexturesInUiGrid()
                    projectManager.current().assetManager.addModifiedAsset(terrainObjectLayerAsset)
                }
            })

            changeObject.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    UI.assetSelectionDialog.show(
                            false,
                            AssetModelFilter(),
                            object : AssetPickerDialog.AssetPickerListener {
                                override fun onSelected(asset: Asset?) {
                                    val terrainObjectLayerAsset = this@TerrainObjectLayerWidget.asset
                                    terrainObjectLayerAsset.change(asset as ModelAsset, layerPos)
                                    applyDependenciesAfterChanging(layerPos)
                                    setTexturesInUiGrid()
                                    projectManager.current().assetManager.addModifiedAsset(terrainObjectLayerAsset)
                                }
                            })
                }
            })
        }
    }
}
