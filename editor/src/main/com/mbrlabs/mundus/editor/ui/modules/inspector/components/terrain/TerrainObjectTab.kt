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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.widgets.ImprovedSlider
import com.mbrlabs.mundus.editor.ui.widgets.TerrainObjectLayerWidget

class TerrainObjectTab(private val parentWidget: TerrainComponentWidget) : BaseBrushTab(parentWidget, TerrainBrush.BrushMode.TERRAIN_OBJECT) {

    private val root = VisTable()
    private val terrainObjectLayerWidget = TerrainObjectLayerWidget(parentWidget.component.terrainAsset.terrainObjectLayerAsset, parentWidget.component)
    private val actionWidget = VisTable()
    private val addingActionWidget = VisTable()

    init {
        root.add(terrainObjectLayerWidget).expand().fill().padBottom(5f).row()

        val radioButtonTable = VisTable()
        radioButtonTable.add(VisRadioButton("Adding")).expand().align(Align.center)
        radioButtonTable.add(VisRadioButton("Removing")).expand().align(Align.center)
        root.add(radioButtonTable).expandX().fillX().row()

        actionWidget.add(addingActionWidget).expandX().fillX()

        root.add(actionWidget).expandX().fillX()

        setupAddingActionWidget()

//        root.setDebug(true, true) // TODO remove later
    }

    override fun getTabTitle(): String = "Objets"

    override fun getContentTable(): Table = root

    override fun onShow() {
        super.onShow()

        terrainObjectLayerWidget.textureGrid.clearHighlight()

    }

    private fun setupAddingActionWidget() {
        val strengthSlider = ImprovedSlider(0f, 1f, 0.1f)
        addingActionWidget.add(VisLabel("Strength")).left().row()
        strengthSlider.value = TerrainBrush.getStrength() // TODO
        addingActionWidget.add(strengthSlider).expandX().fillX().row()
    }
}
