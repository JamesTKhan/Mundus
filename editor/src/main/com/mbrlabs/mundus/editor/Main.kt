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

@file:JvmName("Main")

package com.mbrlabs.mundus.editor

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.kotcrab.vis.ui.util.OsUtils
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.StartOnFirstThreadHelper

const private val TAG = "Main"

const val VERSION = "v0.4.0"
const val TITLE = "Mundus $VERSION"

@Suppress("UNUSED_PARAMETER")
fun main(arg: Array<String>) {
    // Temporary fix for MacOS, we should be able to remove when we update libGDX to 1.11.0 and use
    //  gdx-lwjgl3-glfw-awt-macos extension instead https://libgdx.com/news/2022/05/gdx-1-11
    StartOnFirstThreadHelper.startNewJvmIfRequired()
    Log.init()
    launchEditor()
}

private fun launchEditor() {
    val config = Lwjgl3ApplicationConfiguration()
    val editor = Editor()
    config.setWindowListener(editor)

    // Set initial window size. See https://github.com/mbrlabs/Mundus/issues/11
    val dm = Lwjgl3ApplicationConfiguration.getDisplayMode()
    if (OsUtils.isMac()) {
        config.setWindowedMode((dm.width * 0.80f).toInt(), (dm.height * 0.80f).toInt())
    } else {
        config.setWindowedMode((dm.width * 0.95f).toInt(), (dm.height * 0.95f).toInt())
    }

    config.setTitle(TITLE)
    config.setWindowSizeLimits(1350, 1, 9999, 9999)
    config.setWindowPosition(-1, -1)
    config.setWindowIcon("icon/logo.png")

    Lwjgl3Application(editor, config)
    Log.info(TAG, "Shutting down [{}]", TITLE)
}


