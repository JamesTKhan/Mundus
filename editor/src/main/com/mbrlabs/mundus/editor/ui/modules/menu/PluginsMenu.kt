/*
 * Copyright (c) 2023. See AUTHORS file.
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.events.PluginsLoadedEvent
import com.mbrlabs.mundus.editor.plugin.RootWidgetImpl
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.pluginapi.MenuExtension
import org.pf4j.DefaultPluginManager

class PluginsMenu : Menu("Plugins"), PluginsLoadedEvent.PluginsLoadedEventListener{

    companion object {
        const val NO_PLUGINS_LOADED_TEXT = "No Plugins Loaded"
    }

    private val pluginManager = Mundus.inject<DefaultPluginManager>()

    init {
        Mundus.registerEventListener(this)
    }

    override fun onPluginsLoaded(event: PluginsLoadedEvent) {
        val menuExtensions = pluginManager.getExtensions(MenuExtension::class.java)

        if (menuExtensions.isNotEmpty()) {
            for (menuExtension in pluginManager.getExtensions(MenuExtension::class.java)) {
                val menuItem = MenuItem(menuExtension.menuName)
                menuItem.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        val dialog = BaseDialog(menuExtension.menuName)
                        val root = RootWidgetImpl()
                        try {
                            menuExtension.setupDialogRootWidget(root)
                            dialog.add(root)
                            UI.showDialog(dialog)
                        } catch (ex: Exception) {
                            Mundus.postEvent(LogEvent(LogType.ERROR, "Exception during setup plugin's root widget! $ex"))
                        }
                    }
                })

                addItem(menuItem)
            }
        } else {
            addItem(MenuItem(NO_PLUGINS_LOADED_TEXT))
        }
    }
}
