package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.editor.ui.widgets.LightWidget

class LightComponentWidget(lightComponent: LightComponent) : ComponentWidget<LightComponent>("Light Component", lightComponent) {

    private val settingsContainer = VisTable()

    init {
        this.component = lightComponent
        setupUI()
    }

    private fun setupUI() {
        collapsibleContent.add(VisLabel("Settings")).left().row()
        collapsibleContent.addSeparator().padBottom(5f).row()
        settingsContainer.add(LightWidget(component)).padLeft(10f)
        collapsibleContent.add(settingsContainer).left().row()
    }

    override fun setValues(go: GameObject) {
        val c: LightComponent? = go.findComponentByType(Component.Type.LIGHT)
        if (c != null) {
            component = c
        }
    }

}