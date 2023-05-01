package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget

class CustomPropertiesWidget : BaseInspectorWidget("Custom Properties") {

    private val customProperties = VisTable()

    init {
        isDeletable = false

        setupUI()
    }
    override fun onDelete() {
        // The custom properties component can't be deleted.
    }

    override fun setValues(go: GameObject) {
        customProperties.clearChildren()

        // TODO add custom properties
    }

    private fun setupUI() {
        collapsibleContent.add(customProperties).row()

        val addButton = VisTextButton("Add")
        collapsibleContent.add(addButton)

        addButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                customProperties.add(VisTextField()).padBottom(3f).padRight(3f)
                customProperties.add(VisTextField()).padBottom(3f).row()
            }
        })
    }
}
