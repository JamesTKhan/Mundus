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
package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisLabel
import com.mbrlabs.mundus.commons.assets.TerrainAsset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.IntegerFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.createTerrainGO

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
class AddTerrainDialog : BaseDialog("Add Terrain") {
    companion object {
        private val TAG = AddTerrainDialog::class.java.simpleName
    }
    // UI elements
    private val name = VisTextField("Terrain")
    private val vertexResolution = IntegerFieldWithLabel("", -1, false)
    private val terrainWidth = IntegerFieldWithLabel("", -1, false)
    private val terrainDepth = IntegerFieldWithLabel("", -1, false)
    private val positionX = FloatFieldWithLabel("", -1, true)
    private val positionY = FloatFieldWithLabel("", -1, true)
    private val positionZ = FloatFieldWithLabel("", -1, true)
    private val generateBtn = VisTextButton("Generate Terrain")

    private var projectManager : ProjectManager
    private var kryoManager : KryoManager

    init {
        isResizable = true
        projectManager = Mundus.inject()
        kryoManager = Mundus.inject()
        setDefaults()
        setupUI()
        setupListeners()
    }

    private fun setDefaults() {
        vertexResolution.text = "180"
        terrainWidth.text = "1200"
        positionX.text = "0"
        positionY.text = "0"
        positionZ.text = "0"
    }

    private fun setupUI() {
        val root = Table()
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        // left table
        val content = VisTable()
        content.defaults().pad(4f)
        content.left().top()
        content.add(VisLabel("Name: ")).left().padBottom(10f)
        content.add(name).fillX().expandX().row()
        content.add(ToolTipLabel("Vertex resolution: ", "This will determine the vertices count when squared. 180 = 32,400 vertices.")).left().padBottom(10f)
        content.add(vertexResolution).fillX().expandX().row()
        content.add(ToolTipLabel("Terrain width: ", "Size of the terrain, in meters.")).left().padBottom(10f)
        content.add(terrainWidth).fillX().expandX().row()
        content.add(VisLabel("Position on x-axis:")).left().padBottom(10f)
        content.add(positionX).fillX().expandX().row()
        content.add(VisLabel("Position on y-axis:")).left().padBottom(10f)
        content.add(positionY).fillX().expandX().row()
        content.add(VisLabel("Position on z-axis: ")).left().padBottom(10f)
        content.add(positionZ).fillX().expandX().row()
//        content.add(VisLabel("Terrain depth")).left().padBottom(10f)
//        content.add(terrainDepth).fillX().expandX().row()
        content.add(generateBtn).fillX().expand().colspan(2).bottom()
        root.add(content)
    }

    private fun setupListeners() {

        // terraform btn
        generateBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                createTerrain()
            }
        })
    }

    private fun createTerrain() {
        try {
            val terrainName: String = name.text
            val res: Int = vertexResolution.int
            val width: Int = terrainWidth.int
            //val depth: Int = terrainDepth.int
            val posX: Float = positionX.float
            val posY: Float = positionY.float
            val posZ: Float = positionZ.float

            if (res == 0 || width == 0) return

            try {
                Log.trace(TAG, "Add terrain game object in root node.")
                val context = projectManager.current()
                val sceneGraph = context.currScene.sceneGraph
                val goID = projectManager.current().obtainID()

                // Save context here so that the ID above is persisted in .pro file
                kryoManager.saveProjectContext(projectManager.current())

                //val name = "Terrain $goID"
                val asset: TerrainAsset
                try {
                    // create asset
                    asset = context.assetManager.createTerraAsset(
                        terrainName,
                        res, width
                    )
                } catch (ex: AssetAlreadyExistsException) {
                    Dialogs.showErrorDialog(stage, "An asset with that name already exists.")
                    return
                }

                asset.load()
                asset.applyDependencies()

                val terrainGO = createTerrainGO(sceneGraph,
                    Shaders.terrainShader, goID, terrainName, asset)
                // update sceneGraph
                sceneGraph.addGameObject(terrainGO)
                terrainGO.setLocalPosition(posX, posY, posZ)

                context.currScene.terrains.add(asset)
                projectManager.current().assetManager.addNewAsset(asset)
                Mundus.postEvent(AssetImportEvent(asset))
                Mundus.postEvent(SceneGraphChangedEvent())
                UI.toaster.success("Terrain Generated")
                close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (nfe: NumberFormatException) {
            Log.error(TAG, nfe.message)
        }
    }
}