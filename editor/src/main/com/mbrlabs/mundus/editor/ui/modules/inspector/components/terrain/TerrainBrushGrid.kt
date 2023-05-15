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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.GlobalBrushSettingsChangedEvent
import com.mbrlabs.mundus.editor.events.ToolActivatedEvent
import com.mbrlabs.mundus.editor.events.ToolDeactivatedEvent
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.tools.brushes.CircleBrush
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FaTextButton
import com.mbrlabs.mundus.editor.ui.widgets.ImprovedSlider

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
class TerrainBrushGrid(private val parent: TerrainComponentWidget,
                       private val brushMode: TerrainBrush.BrushMode)
    : VisTable(), GlobalBrushSettingsChangedEvent.GlobalBrushSettingsChangedListener, ToolDeactivatedEvent.ToolDeactivatedEventListener, ToolActivatedEvent.ToolActivatedEventListener {

    private val brushItems = Array<BrushItem>()
    private val grid = GridGroup(40f, 0f)
    private val strengthSlider = ImprovedSlider(0f, 1f, 0.1f)

    private val toolManager: ToolManager = Mundus.inject()
    private val buttons: ArrayList<FaTextButton> = arrayListOf()

    init {
        Mundus.registerEventListener(this)
        align(Align.left)
        add(VisLabel("Brushes:")).padBottom(10f).padLeft(5f).left().row()

        val brushGridContainerTable = VisTable()
        brushGridContainerTable.setBackground("menu-bg")

        // grid
        for (brush in toolManager.terrainBrushes) {
            val brushItem = BrushItem(brush)
            brushItems.add(brushItem)
            grid.addActor(brushItem)
        }
        brushGridContainerTable.add(grid).expand().fill().row()

        // brush settings
        val settingsTable = VisTable()
        settingsTable.add(VisLabel("Strength")).left().row()
        strengthSlider.value = TerrainBrush.getStrength()
        settingsTable.add(strengthSlider).expandX().fillX().row()
        strengthSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                TerrainBrush.setStrength(strengthSlider.value)
            }
        })

        add(brushGridContainerTable).expand().fill().padLeft(5f).padRight(5f).row()
        add(settingsTable).expand().fill().padLeft(5f).padRight(5f).padTop(5f).row()
    }

    fun clearSelectedButtonStyle() {
        buttons.forEach { it.style = FaTextButton.styleNoBg }
    }

    fun activateBrush(brush: TerrainBrush) {
        try {
            brush.mode = brushMode
            toolManager.activateTool(brush)
            brush.setTerrainComponent(parent.component)
        } catch (e: TerrainBrush.ModeNotSupportedException) {
            e.printStackTrace()
            Dialogs.showErrorDialog(UI, e.message)
        }

    }

    fun hideBrushes() {
        for (brushItem in brushItems) {
            if (brushItem.brush is CircleBrush) continue
            brushItem.isVisible = false
        }
    }

    fun showCircleBrush() {
        for (brushItem in brushItems) {
            if (brushItem.brush is CircleBrush) {
                brushItem.isVisible = true
                break
            }
        }
    }

    override fun onSettingsChanged(event: GlobalBrushSettingsChangedEvent) {
        strengthSlider.value = TerrainBrush.getStrength()
    }

    override fun onToolDeactivatedEvent(event: ToolDeactivatedEvent) {
        clearSelectedButtonStyle()
    }

    override fun onToolActivatedEvent(event: ToolActivatedEvent) {
        clearSelectedButtonStyle()
    }

    /**
     */
    private inner class BrushItem(val brush: TerrainBrush) : VisTable() {
        init {
            val button = FaTextButton(brush.iconFont)
            button.name = brush.name
            add(button)
            buttons.add(button)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    activateBrush(brush)
                    button.style = FaTextButton.styleActive
                }
            })
        }
    }

}
