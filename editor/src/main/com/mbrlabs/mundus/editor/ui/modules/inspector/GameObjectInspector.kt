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

package com.mbrlabs.mundus.editor.ui.modules.inspector

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.ComponentWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.IdentifierWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.LightComponentWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.ModelComponentWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.TransformWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.TerrainComponentWidget
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.WaterComponentWidget

/**
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class GameObjectInspector : VisTable(), ProjectChangedEvent.ProjectChangedListener,
    SceneChangedEvent.SceneChangedListener {

    private val identifierWidget = IdentifierWidget()
    private val transformWidget = TransformWidget()
    private val componentWidgets: Array<ComponentWidget<*>> = Array()
    private val addComponentBtn = VisTextButton("Add Component")
    private val componentTable = VisTable()

    private var gameObject: GameObject? = null

    init {
        Mundus.registerEventListener(this)

        align(Align.top)
        add(identifierWidget).growX().pad(7f).row()
        add(transformWidget).growX().pad(7f).row()
        for (cw in componentWidgets) {
            componentTable.add<BaseInspectorWidget>(cw).row()
        }
        add(componentTable).growX().pad(7f).row()
        add(addComponentBtn).expandX().fill().top().center().pad(10f).row()

        addComponentBtn.addListener(object : ClickListener () {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.addComponentDialog)
            }
        })
    }

    fun setGameObject(gameObject: GameObject?) {
        this.gameObject = gameObject

        // build ui
        buildComponentWidgets()
        componentTable.clearChildren()
        for (cw in componentWidgets) {
            componentTable.add(cw).grow().row()
        }

        // update
        updateGameObject()
    }

    fun updateGameObject() {
        if (gameObject != null) {
            identifierWidget.isVisible = true
            transformWidget.isVisible = true
            addComponentBtn.isVisible = true

            identifierWidget.setValues(gameObject!!)
            transformWidget.setValues(gameObject!!)

            for (cw in componentWidgets) {
                cw.setValues(gameObject!!)
            }
        } else {
            identifierWidget.isVisible = false
            transformWidget.isVisible = false
            addComponentBtn.isVisible = false
        }
    }

    private fun buildComponentWidgets() {
        componentWidgets.clear()
        if (gameObject != null) {
            for (component in gameObject!!.components) {
                // model component widget!!
                if (component.type == Component.Type.MODEL) {
                    componentWidgets.add(ModelComponentWidget(component as ModelComponent))
                    // terrainAsset component widget
                } else if (component.type == Component.Type.TERRAIN) {
                    componentWidgets.add(TerrainComponentWidget(component as TerrainComponent))
                } else if (component.type == Component.Type.WATER) {
                    componentWidgets.add(WaterComponentWidget(component as WaterComponent))
                } else if (component.type == Component.Type.LIGHT) {
                    componentWidgets.add(LightComponentWidget(component as LightComponent))
                }
            }
        }
    }

    fun addComponent(component: Component) {
        for (widget in componentWidgets) {
            if (widget.component == component) {
                // The inspector already has a widget for this component, ignore.
                return
            }
        }

        if (component is LightComponent) {
            componentWidgets.add(LightComponentWidget(component))
            componentTable.add(componentWidgets.last()).grow().row()
        }
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        setGameObject(null)
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        setGameObject(null)
    }

}
