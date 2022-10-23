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

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.widgets.ImprovedSlider

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
class TerrainSettingsTab(private val parentWidget: TerrainComponentWidget) : Tab(false, false) {

    private val table = VisTable()
    private val uvSlider = ImprovedSlider(.5f, 120f, .5f)

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        table.align(Align.left)
        table.add(VisLabel("Settings")).row()

        table.add(VisLabel("UV scale")).left().row()
        uvSlider.value = parentWidget.component.terrainAsset.terrain.uvScale.x
        table.add(uvSlider).expandX().fillX().row()
        uvSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val assetManager = projectManager.current().assetManager
                assetManager.addModifiedAsset(parentWidget.component.terrainAsset)
                parentWidget.component.updateUVs(Vector2(uvSlider.value, uvSlider.value))
            }
        })
    }

    override fun getTabTitle(): String {
        return "Settings"
    }

    override fun getContentTable(): Table {
        return table
    }

}
