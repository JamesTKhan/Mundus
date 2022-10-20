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

package com.mbrlabs.mundus.editor.ui.modules.menu

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.InputValidator
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneAddedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.Log

/**
 * @author Marcus Brummer
 * @version 23-12-2015
 */
class SceneMenu : Menu("Scenes"),
        ProjectChangedEvent.ProjectChangedListener,
        SceneAddedEvent.SceneAddedListener {

    companion object {
        private val TAG = SceneMenu::class.java.simpleName
        private const val DELETE_BUTTON_NAME = "delete_scene_submenu"
    }

    private val sceneItems = Array<MenuItem>()
    private val addScene = MenuItem("Add scene")

    private val projectManager: ProjectManager = Mundus.inject()
    private val kryoManager : KryoManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)

        addScene.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Dialogs.showInputDialog(UI, "Add Scene", "Name:", SceneNameValidator(), object : InputDialogAdapter() {
                    override fun finished(input: String) {
                        val newSceneName = input.trim()
                        val project = projectManager.current()
                        val scene = projectManager.createScene(project, newSceneName)
                        projectManager.changeScene(project, scene.name)
                        Mundus.postEvent(SceneAddedEvent(scene))
                    }
                })
            }
        })
        addItem(addScene)

        addSeparator()
        buildSceneUi()
    }

    private fun buildSceneUi() {
        // remove old items
        for (item in sceneItems) {
            removeActor(item)
        }
        // add new items
        for (scene in projectManager.current().scenes) {
            buildMenuItem(scene)
        }

    }

    private fun buildMenuItem(sceneName: String): MenuItem {
        val menuItem = MenuItem(sceneName)

        val subMenus = PopupMenu()
        subMenus.addItem(buildOpenSubMenuItem(sceneName))
        subMenus.addItem(buildDeleteSubMenuItem(sceneName, menuItem))
        menuItem.subMenu = subMenus

        addItem(menuItem)
        sceneItems.add(menuItem)

        return menuItem
    }

    private fun buildOpenSubMenuItem(sceneName: String): MenuItem {
        val menuItem = MenuItem("Open")
        menuItem.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                projectManager.changeScene(projectManager.current(), sceneName)
                updateDeleteButtonEnable(sceneName)
            }
        })
        return menuItem
    }

    private fun buildDeleteSubMenuItem(sceneName: String, screenMenu: MenuItem): MenuItem {
        val menuItem = MenuItem("Delete")
        menuItem.name = DELETE_BUTTON_NAME
        menuItem.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Dialogs.showOptionDialog(UI, "Deleting scene", "Are you sure you want to delete '$sceneName' scene?", Dialogs.OptionDialogType.YES_CANCEL, object : OptionDialogAdapter() {
                    override fun yes() {
                        // Delete scene file
                        projectManager.deleteScene(projectManager.current(), sceneName)

                        // Delete scene from project
                        kryoManager.saveProjectContext(projectManager.current())

                        // Delete scene from UI
                        sceneItems.removeValue(screenMenu, true)
                        removeActor(screenMenu)
                        pack()

                        Log.trace(TAG, "SceneMenu", "Scene [{}] deleted.", sceneName)
                    }
                })
            }
        })

        // The current scene's delete button will be disabled
        if (projectManager.current().activeSceneName.equals(sceneName)) {
            disableMenuItem(menuItem)
        }

        return menuItem
    }

    private fun updateDeleteButtonEnable(currentSceneName: String) {
        for (mi in sceneItems) {
            val deleteMenuItem = mi.subMenu.findActor<MenuItem>(DELETE_BUTTON_NAME)

            // Enable other delete buttons and disable current delete button
            if (mi.text.contentEquals(currentSceneName)) {
                disableMenuItem(deleteMenuItem)
            } else {
                enableMenuItem(deleteMenuItem)
            }
        }
    }

    private fun disableMenuItem(menuItem: MenuItem) {
        menuItem.touchable = Touchable.disabled
        menuItem.isDisabled = true
    }

    private fun enableMenuItem(menuItem: MenuItem) {
        menuItem.touchable = Touchable.enabled
        menuItem.isDisabled = false
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        buildSceneUi()
    }

    override fun onSceneAdded(event: SceneAddedEvent) {
        val sceneName = event.scene!!.name
        buildMenuItem(sceneName)
        updateDeleteButtonEnable(sceneName)

        // Save context here so that the scene name above is persisted in .pro file
        kryoManager.saveProjectContext(projectManager.current())

        Log.trace(TAG, "SceneMenu", "New scene [{}] added.", sceneName)
    }

    inner class SceneNameValidator : InputValidator {
        override fun validateInput(input: String): Boolean {
            return input.isNotBlank()
        }

    }

}
