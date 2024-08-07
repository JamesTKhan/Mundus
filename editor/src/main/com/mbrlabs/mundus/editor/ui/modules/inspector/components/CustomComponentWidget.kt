/*
 * Copyright (c) 2024. See AUTHORS file.
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

import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent
import com.mbrlabs.mundus.editor.plugin.RootWidgetImpl

class CustomComponentWidget<T : AbstractComponent>(
    title: String,
    rootWidget: RootWidgetImpl,
    componentWidget: T
) : ComponentWidget<T>(title, componentWidget) {

    init {
        setupUI(rootWidget)
    }

    private fun setupUI(rootWidget: RootWidgetImpl) {
        collapsibleContent.add(rootWidget).grow().row()
    }

    override fun setValues(go: GameObject) {
        // NOOP
    }
}
