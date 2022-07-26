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

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuItem
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
class WindowMenu : Menu("Window") {

    val settings = MenuItem("Settings")
    private val versionInfo = MenuItem("Version Info")
    private val keyboardShortcuts = MenuItem("Keyboard Shortcuts")

    init {
        settings.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.ALT_LEFT, Input.Keys.S)
        addItem(settings)
        addItem(keyboardShortcuts)
        addItem(versionInfo)

        settings.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.settingsDialog)
            }
        })

        versionInfo.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.versionDialog)
            }
        })

        keyboardShortcuts.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.showDialog(UI.keyboardShortcuts)
            }
        })
    }

}
