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

package com.mbrlabs.mundus.editor

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.PerformanceCounter
import com.kotcrab.vis.ui.widget.VisLabel
import com.mbrlabs.mundus.commons.Scene
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.utils.GlUtils

/**
 * A preview window to see running physics.
 *
 * @author James Pooley
 * @version July 05, 2022
 */
class Preview: Lwjgl3WindowAdapter(), ApplicationListener {
    private lateinit var sceneGraph: SceneGraph
    private lateinit var scene: Scene
    private var projectManager: ProjectManager
    private lateinit var camController: FirstPersonCameraController

    private var stage: Stage
    private var perfLabel = VisLabel("Perf")

    private var performanceCounter: PerformanceCounter

    init {
        Mundus.registerEventListener(this)
        projectManager = Mundus.inject()
        stage = Stage()
        stage.addActor(perfLabel)
        performanceCounter = PerformanceCounter(this::class.java.simpleName)
    }

    override fun create() {

    }

    fun loadScene(sceneName: String) {
        scene = projectManager.loadScene(projectManager.current(), sceneName)
        sceneGraph = scene.sceneGraph

        camController = FirstPersonCameraController(scene.cam)
        camController.setVelocity(20f)

        scene.runPhysics()
        scene.physicsSystem.setDebugDrawMode(btIDebugDraw.DebugDrawModes.DBG_DrawAabb)
    }

    fun setCamPosition(cam: PerspectiveCamera) {
        scene.cam.position.set(cam.position)
        scene.cam.direction.set(cam.direction)
        scene.cam.up.set(cam.up)
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (scene.physicsSystem.isRunning) {
                scene.pausePhysics()
            } else {
                scene.runPhysics()
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            scene.physicsSystem.setDebugDrawMode(btIDebugDraw.DebugDrawModes.DBG_NoDebug)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            scene.physicsSystem.setDebugDrawMode(btIDebugDraw.DebugDrawModes.DBG_DrawAabb)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            scene.physicsSystem.setDebugDrawMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe)
        }

        Gdx.input.inputProcessor = camController
        GlUtils.clearScreen(Color.BLACK)

        performanceCounter.tick()
        performanceCounter.start()
        sceneGraph.update()
        scene.render()
        performanceCounter.stop()

        camController.update()

        val mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        perfLabel.setText(buildString {
            append("Free Memory")
            append(convertToMegaByte(Runtime.getRuntime().freeMemory()))
            append("mb\n")
            append("Mem Usage")
            append(convertToMegaByte(mem))
            append("mb\n")
        })
        stage.act()
        stage.draw()

        performanceCounter.reset()
    }

    override fun pause() {
    }

    override fun resume() {

    }

    override fun dispose() {

    }

    private fun convertToMegaByte(mem: Long): Long {
        return mem / 1048576
    }
}