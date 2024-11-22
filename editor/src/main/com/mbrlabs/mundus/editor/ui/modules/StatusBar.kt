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

package com.mbrlabs.mundus.editor.ui.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.plugin.PluginManagerProvider
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.events.PluginsLoadedEvent
import com.mbrlabs.mundus.editor.input.FreeCamController
import com.mbrlabs.mundus.editor.plugin.LabelWidgetImpl
import com.mbrlabs.mundus.editor.utils.formatFloat
import com.mbrlabs.mundus.pluginapi.StatusBarExtension
import org.pf4j.PluginManager

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
class StatusBar : VisTable(), PluginsLoadedEvent.PluginsLoadedEventListener {

    companion object {
        const val EMPTY_TEXT = ""
    }

    private val root = VisTable()
    private val left = VisTable()
    private val right = VisTable()

    private val pluginTexts = VisTable()

    private val mousePos = VisLabel()
    private val fpsLabel = VisLabel()
    private val camPos = VisLabel()

    private val speed01 = VisTextButton("0.1x")
    private val speed1 = VisTextButton("1.0x")
    private val speed10 = VisTextButton("10x")

    private val freeCamController: FreeCamController = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()
    private var pluginManager: PluginManager = Mundus.inject<PluginManagerProvider>().pluginManager

    init {
        Mundus.registerEventListener(this)

        background = VisUI.getSkin().getDrawable("menu-bg")
        root.align(Align.left or Align.center)
        add(root).expand().fill()

        left.align(Align.left)
        left.padLeft(10f)
        right.align(Align.right)
        right.padRight(10f)
        root.add(left).left().expand().fill()
        root.add(right).right().expand().fill()

        // left
        left.add(VisLabel("Editor Cam Speed: ")).left()
        left.add(speed01)
        left.add(speed1)
        left.add(speed10)

        // right
        right.add(pluginTexts).right()

        right.add(mousePos).right()
        right.addSeparator(true).padLeft(5f).padRight(5f)
        right.add(camPos).right()
        right.addSeparator(true).padLeft(5f).padRight(5f)
        right.add(fpsLabel).right()

        setupListeners()
        handleCameraMovementSpeedChange()
    }

    /**
     * Disables the button corresponding to the currently
     * selected camera movement speed.
     */
    private fun handleCameraMovementSpeedChange() {
        val cameraMovementSpeed: Float = freeCamController.getVelocity()

        speed01.isDisabled = false
        speed1.isDisabled = false
        speed10.isDisabled = false

        when (cameraMovementSpeed) {
            freeCamController.SPEED_01 -> speed01.isDisabled = true
            freeCamController.SPEED_1 -> speed1.isDisabled = true
            freeCamController.SPEED_10 -> speed10.isDisabled = true
        }
    }

    /**
     * Set movement speed of the camera.
     *
     * @param movementSpeed the new speed of camera movement
     */
    private fun setCameraMovementSpeed(movementSpeed: Float) {
        freeCamController.setVelocity(movementSpeed)
        handleCameraMovementSpeedChange()
    }

    fun setupListeners() {
        speed01.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
               setCameraMovementSpeed(freeCamController.SPEED_01)
            }
        })

        speed1.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                setCameraMovementSpeed(freeCamController.SPEED_1)
            }
        })

        speed10.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                setCameraMovementSpeed(freeCamController.SPEED_10)
            }
        })
    }

    override fun onPluginsLoaded(event: PluginsLoadedEvent) {
        pluginManager.getExtensions(StatusBarExtension::class.java).forEach {
            val pluginLabel = VisLabel()
            pluginTexts.add(pluginLabel).right()
            pluginTexts.addSeparator(true).padLeft(5f).padRight(5f)

            try {
                it.createStatusBarLabel(LabelWidgetImpl(pluginLabel))
            } catch (ex: Exception) {
                Mundus.postEvent(LogEvent(LogType.ERROR, "Exception during create plugin status bar label! $ex"))
            }
        }
    }

    override fun act(delta: Float) {
        setFps(Gdx.graphics.framesPerSecond)
        setCamPos(projectManager.current().currScene.cam.position)
        super.act(delta)
    }

    fun setMousePos(x: Float, y: Float, z: Float) {
        mousePos.setText("mousePos: ${formatFloat(x, 2)}, ${formatFloat(y, 2)}, ${formatFloat(z, 2)}")
    }

    fun clearMousePos() {
        mousePos.setText(EMPTY_TEXT)
    }

    private fun setFps(fps: Int) {
        this.fpsLabel.setText("fps: $fps")
    }

    private fun setCamPos(pos: Vector3) {
        camPos.setText("camPos: ${formatFloat(pos.x, 2)}, ${formatFloat(pos.y, 2)}, ${formatFloat(pos.z, 2)}")
    }

}
