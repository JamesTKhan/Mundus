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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.tools

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetDeletedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.AutoFocusScrollPane

/**
 * Dialog to display the Asset Clean Up utility
 *
 * @author JamesTKhan
 * @version July 29, 2022
 */
class AssetCleanUpDialog : BaseDialog(TITLE) {

    companion object {
        private const val TITLE = "Asset Clean Up"
    }

    private val assetTable = VisTable()
    private val root = VisTable()
    private val loadingRoot = VisTable()
    private val pane = AutoFocusScrollPane(assetTable)
    private val selectAll = VisCheckBox("Select All")
    private val yes = VisTextButton("Yes")
    private val cancel = VisTextButton("Cancel")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        setupUI()
        setupListeners()
        pane.fadeScrollBars = false
    }

    private fun setupUI() {
        loadingRoot.add(VisLabel("Searching...")).left().pad(10f).row()
        add(loadingRoot).top().left()

        root.padTop(6f).padRight(6f).padBottom(10f)
        root.defaults().pad(4f)

        val label = VisLabel()
        label.wrap = true
        label.setText("Note: Running this process multiple times may find more assets due to removals from previous scan.")

        root.add(label).grow().left().colspan(2).padBottom(10f).row()

        val deleteLabel = VisLabel("This cannot be undone. Delete selected assets?")
        deleteLabel.color.set(Color.SCARLET)
        root.add(deleteLabel).grow().left().colspan(2).padBottom(10f).row()
        root.add(pane).colspan(2).fillX().expandX().row()
        root.add(yes).expandX().fillX()
        root.add(cancel).expandX().fillX()
    }

    private fun setupListeners() {
        // Yes
        yes.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                for (actor in assetTable.children) {
                    if (actor is VisCheckBox && actor.isChecked) {
                        if (actor.userObject == null) continue
                        val asset = actor.userObject as Asset
                        projectManager.current().assetManager.deleteAsset(asset)
                    }
                }
                Mundus.postEvent(AssetDeletedEvent())
                close()
            }
        })

        // cancel
        cancel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                close()
            }
        })

        selectAll.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                val checkAll = selectAll.isChecked
                for (ckBox in assetTable.children) {
                    if (ckBox is VisCheckBox) {
                        ckBox.isChecked = checkAll
                    }
                }
            }
        })

    }

    fun setAssetsToDelete(assets: Array<Asset>) {
        loadingRoot.remove()
        add(root).top().left()

        if (assets.isEmpty) {
            assetTable.add("No unused assets found.")
            pack()
            centerWindow()
            return
        }

        assetTable.add(selectAll).left().row()
        selectAll.isChecked = true
        assetTable.addSeparator()
        for (asset in assets) {
            val ckBox = VisCheckBox(asset.name)
            ckBox.userObject = asset
            ckBox.isChecked = true
            assetTable.add(ckBox).left().row()
        }
        pack()

        if (height > UI.viewport.screenHeight * .4f) {
            this.height = UI.viewport.screenHeight * .4f
        }

        centerWindow()
    }

    override fun close() {
        super.close()
        clearChildren()
    }

}
