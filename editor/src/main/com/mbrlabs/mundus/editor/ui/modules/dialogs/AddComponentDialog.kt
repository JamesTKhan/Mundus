package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ComponentAddedEvent

class AddComponentDialog : BaseDialog("Add Component") {

    private lateinit var root: VisTable
    private lateinit var selectBox: VisSelectBox<Component.Type>
    private var addBtn = VisTextButton("Add Component")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        root = VisTable()
        root.defaults().pad(6f)

        // Component selector
        val selectorsTable = VisTable(true)
        selectorsTable.add(VisLabel("Component Type:"))
        selectBox = VisSelectBox<Component.Type>()
        selectorsTable.add(selectBox).left()
        root.add(selectorsTable).row()

        root.add(addBtn).left().growX()

        // Load types into select box
        val types = Array<Component.Type>()
        for (type in Component.Type.values())
            types.add(type)

        selectBox.items = types

        add(root)
    }

    private fun setupListeners() {
        addBtn.addListener(object : ClickListener () {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Add component to current game object
                val go = projectManager.current().currScene.currentSelection
                val component: Component

                when(selectBox.selected) {
                    Component.Type.MODEL -> TODO()
                    Component.Type.TERRAIN -> TODO()
                    Component.Type.LIGHT -> component = LightComponent(go)
                    Component.Type.PARTICLE_SYSTEM -> TODO()
                    Component.Type.WATER -> TODO()
                }

                go.addComponent(component)
                Mundus.postEvent(ComponentAddedEvent(component))
                close()
            }
        })
    }

}