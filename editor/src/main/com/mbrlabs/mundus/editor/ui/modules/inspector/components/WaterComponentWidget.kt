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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent
import com.mbrlabs.mundus.editor.ui.widgets.WaterWidget

/**
 * @author JamesTKhan
 * @version 04-05-2022
 */
class WaterComponentWidget(waterComponent: WaterComponent) :
        ComponentWidget<WaterComponent>("Water Component", waterComponent) {

    private val settingsContainer = VisTable()

    init {
        val label = VisLabel()
        label.wrap = true
        label.setText("NOTE: All water instances must have the same height (Y value) for proper reflections.")
        collapsibleContent.add(label).grow().padBottom(10f).row()

        settingsContainer.add(WaterWidget(waterComponent)).grow()
        collapsibleContent.add(settingsContainer).left().grow().row()
    }

    override fun setValues(go: GameObject) {
        val c: WaterComponent? = go.findComponentByType(Component.Type.WATER)
        if (c != null) {
            this.component = c
        }
    }

}
