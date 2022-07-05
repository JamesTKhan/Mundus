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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.RigidBodyPhysicsComponent
import com.mbrlabs.mundus.editor.ui.widgets.PhysicsWidget

/**
 * @author James Pooley
 * @version July 03, 2022
 */
class PhysicsComponentWidget(physicsComponent: RigidBodyPhysicsComponent) : ComponentWidget<RigidBodyPhysicsComponent>("Physics Component", physicsComponent) {

    private val settingsContainer = VisTable()

    init {
        this.component = physicsComponent
        setupUI()
    }

    private fun setupUI() {
        collapsibleContent.add(VisLabel("Physics Settings")).left().row()
        collapsibleContent.addSeparator().padBottom(5f).row()
        settingsContainer.add(PhysicsWidget(component)).padLeft(10f)
        collapsibleContent.add(settingsContainer).left().row()
    }

    override fun setValues(go: GameObject) {
        val c = go.findComponentByType(Component.Type.PHYSICS)
        if (c != null) {
            component = c as RigidBodyPhysicsComponent
        }
    }
}