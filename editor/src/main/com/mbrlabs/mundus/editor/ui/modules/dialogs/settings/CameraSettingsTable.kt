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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.settings

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author James Pooley
 * @version July 10, 2022
 */
class CameraSettingsTable : BaseSettingsTable(), ProjectChangedEvent.ProjectChangedListener{

    private var initialized = false
    private val projectManager: ProjectManager = Mundus.inject()
    private val kryoManager: KryoManager = Mundus.inject()

    private val nearPlane = VisTextField("0")
    private val farPlane = VisTextField("0")
    private val fov = VisTextField("0")

    init {
        top().left()
        defaults().left().pad(4f)
        padRight(5f).padLeft(6f)

        add(VisLabel("Camera Settings")).row()
        addSeparator().padBottom(10f)

        val label = VisLabel()
        label.setText("Camera Settings are global for the entire project. You can change camera values at a per scene level" +
                " by accessing the scene.cam object and modifying it in your runtime application.")
        label.wrap = true
        label.width = SettingsDialog.WIDTH * 0.7f
        add(label).expandX().prefWidth(SettingsDialog.WIDTH * 0.7f).row()


        add(VisLabel("Near Plane")).row()
        add(nearPlane).left().row()

        add(VisLabel("Far Plane")).row()
        add(farPlane).left().row()

        add(VisLabel("Field Of View")).row()
        add(fov).left().row()

        registerListeners()
    }

    override fun act(delta: Float) {
        if (!initialized) {
            updateValues()
            initialized = true
        }
        super.act(delta)
    }

    private fun updateValues() {
        nearPlane.text = projectManager.current().settings.cameraSettings.nearPlane.toString()
        farPlane.text = projectManager.current().settings.cameraSettings.farPlane.toString()
        fov.text = projectManager.current().settings.cameraSettings.fieldOfView.toString()
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        updateValues()
    }

    private fun registerListeners() {
        nearPlane.textFieldFilter = FloatDigitsOnlyFilter(true)
        nearPlane.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if(nearPlane.isInputValid && !nearPlane.isEmpty) {
                    try {
                        projectManager.current().currScene.cam.near = nearPlane.text.toFloat()
                    }
                    catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + nearPlane.name))
                    }
                }
            }
        })

        farPlane.textFieldFilter = FloatDigitsOnlyFilter(true)
        farPlane.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if(farPlane.isInputValid && !farPlane.isEmpty) {
                    try {
                        projectManager.current().currScene.cam.far = farPlane.text.toFloat()
                    }
                    catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + farPlane.name))
                    }
                }
            }
        })

        fov.textFieldFilter = FloatDigitsOnlyFilter(true)
        fov.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if(fov.isInputValid && !fov.isEmpty) {
                    try {
                        projectManager.current().currScene.cam.fieldOfView = fov.text.toFloat()
                    }
                    catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + fov.name))
                    }
                }
            }
        })
    }

    override fun onSave() {
        val cameraSettings = projectManager.current().settings?.cameraSettings ?: return
        cameraSettings.nearPlane =  nearPlane.text.toFloat()
        cameraSettings.farPlane =  farPlane.text.toFloat()
        cameraSettings.fieldOfView = fov.text.toFloat()

        kryoManager.saveProjectContext(projectManager.current())
        UI.toaster.success("Settings saved")
    }

}