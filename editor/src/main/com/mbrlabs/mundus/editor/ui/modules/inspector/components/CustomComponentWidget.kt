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
