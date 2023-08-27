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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.GameObjectActiveCommand

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
class IdentifierWidget : VisTable() {

    private val active = VisCheckBox("", true)
    private val name = VisTextField("Name")
    private val tag = VisTextField("Untagged")

    private val projectManager: ProjectManager = Mundus.inject()
    private val history: CommandHistory = Mundus.inject()

    init {
        setupUI()
        setupListeners()
    }

    override fun act(delta: Float) {
        super.act(delta)

        // If this widget is visible and the currentSelection variable is null (for example a brush tool selected) then
        // disable this widget
        val projectContext = projectManager.current()
        val touchable = if (projectContext.currScene.currentSelection != null) Touchable.enabled else Touchable.disabled
        this.touchable = touchable
    }

    private fun setupUI() {
        add(active).padBottom(4f).left().top()
        add(name).padBottom(4f).left().top().expandX().fillX().row()
        add(VisLabel("Tag: ")).left().top()
        add(tag).top().left().expandX().fillX().row()
    }

    private fun setupListeners() {

        active.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val projectContext = projectManager.current()
                val go = projectContext.currScene.currentSelection ?: return
                if (go.active == active.isChecked) return

                val command = GameObjectActiveCommand(go, active.isChecked)
                command.execute()
                history.add(command)
            }
        })

        name.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val projectContext = projectManager.current()
                val selectedGO = projectContext.currScene.currentSelection ?: return
                selectedGO.name = name.text
                Mundus.postEvent(SceneGraphChangedEvent())
            }
        })

    }

    fun setValues(go: GameObject) {
        active.isChecked = go.active
        name.text = go.name
    }

}
