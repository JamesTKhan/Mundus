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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.utils.Fa

/**
 * A table containing both a label and tool tip icon.
 *
 * @author JamesTKhan
 * @version July 05, 2022
 */
class ToolTipLabel(val label: String, toolTipText: String) : VisTable() {
    var fieldLabel = VisLabel(label)
    var fieldInfo = FaLabel(Fa.INFO_CIRCLE)

    init {
        defaults().padRight(4f)
        add(fieldLabel, fieldInfo).align(Align.left)
        Tooltip.Builder(toolTipText, Align.left).target(fieldInfo).build()

        fieldInfo.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                super.enter(event, x, y, pointer, fromActor)
                fieldInfo.style = FaLabel.styleActive
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                super.exit(event, x, y, pointer, toActor)
                fieldInfo.style = FaLabel.style
            }
        })
    }

}