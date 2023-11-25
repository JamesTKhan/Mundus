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

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.utils.Colors

class TerrainObjectLayerWidget(var allowChange: Boolean = true) : VisTable() {

    private val layerNameLabel: VisLabel = VisLabel()
    private val editBtn: VisTextButton = VisTextButton("Edit")
    private val duplicatedBtn: VisTextButton = VisTextButton("Duplicate")
    private val changedBtn: VisTextButton = VisTextButton("Change")

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

        root.add(VisLabel("Objects:")).padLeft(5f).left().row()

        add(root).expand().fill()
    }
}
