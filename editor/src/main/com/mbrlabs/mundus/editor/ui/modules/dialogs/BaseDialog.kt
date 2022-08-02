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

package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisDialog

/**
 * Base dialog to extend from. Tracks if it is currently open or not.
 *
 * @author Marcus Brummer
 * @version 25-11-2015
 */
open class BaseDialog(title: String) : VisDialog(title) {
    protected var dialogOpen = false

    override fun show(stage: Stage?): VisDialog {
        dialogOpen = true
        return super.show(stage)
    }

    override fun close() {
        super.close()
        dialogOpen = false
    }

    init {
        addCloseButton()
    }

}
