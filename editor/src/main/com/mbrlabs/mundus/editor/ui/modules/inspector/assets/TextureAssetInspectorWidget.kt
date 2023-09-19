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

package com.mbrlabs.mundus.editor.ui.modules.inspector.assets

import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget

import org.apache.commons.io.FileUtils

/**
 * @author Marcus Brummer
 * @version 15-10-2016
 */
class TextureAssetInspectorWidget : BaseInspectorWidget(TextureAssetInspectorWidget.TITLE) {

    companion object {
        private val TITLE = "Texture Asset"
    }

    private val name = VisLabel()
    private val width = VisLabel()
    private val height = VisLabel()
    private val fileSize = VisLabel()
    private val magFilterSelectBox: VisSelectBox<TextureFilter>
    private val minFilterSelectBox: VisSelectBox<TextureFilter>

    private var textureAsset: TextureAsset? = null
    private var projectManager : ProjectManager = Mundus.inject()

    init {
        collapsibleContent.add(name).growX().row()
        collapsibleContent.add(width).growX().row()
        collapsibleContent.add(height).growX().row()

        val minFilters = Array<TextureFilter>()
        for (filter in TextureFilter.entries)
            minFilters.add(filter)

        minFilterSelectBox = VisSelectBox<TextureFilter>()
        minFilterSelectBox.items = minFilters
        minFilterSelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                textureAsset?.let {
                    it.setFilter(minFilterSelectBox.selected, it.texture?.magFilter)
                    projectManager.current().assetManager.addModifiedAsset(it)
                }
            }
        })

        val magFilters = Array<TextureFilter>()
        magFilters.add(TextureFilter.Nearest, TextureFilter.Linear)

        magFilterSelectBox = VisSelectBox<TextureFilter>()
        magFilterSelectBox.items = magFilters
        magFilterSelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                textureAsset?.let {
                    it.setFilter(it.texture?.minFilter, magFilterSelectBox.selected)
                    projectManager.current().assetManager.addModifiedAsset(it)
                }
            }
        })

        val filterTable = VisTable()
        filterTable.defaults().padBottom(4f)
        filterTable.add(VisLabel("Filters")).growX().row()
        filterTable.addSeparator().colspan(2).padBottom(4f).row()
        filterTable.add(VisLabel("Min Filter:")).growX()
        filterTable.add(minFilterSelectBox).growX().row()
        filterTable.add(VisLabel("Mag Filter:")).growX()
        filterTable.add(magFilterSelectBox).growX().row()

        collapsibleContent.add(filterTable).padTop(4f).growX().row()
    }

    fun setTextureAsset(texture: TextureAsset) {
        this.textureAsset = texture
        updateUI()
    }

    private fun updateUI() {
        name.setText("Name: " + textureAsset?.name)
        width.setText("Width: " + textureAsset?.texture?.width + " px")
        height.setText("Height: " + textureAsset?.texture?.height + " px")

        val mb = FileUtils.sizeOf(textureAsset?.file?.file()) / 1000000f
        fileSize.setText("Size: $mb mb")

        minFilterSelectBox.selected = textureAsset?.texture?.minFilter
        magFilterSelectBox.selected = textureAsset?.texture?.magFilter
    }

    override fun onDelete() {
        // nope
    }

    override fun setValues(go: GameObject) {
        // nope
    }

}
