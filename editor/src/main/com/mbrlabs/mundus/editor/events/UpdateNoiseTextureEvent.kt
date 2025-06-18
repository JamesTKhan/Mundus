package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.editorcommons.Subscribe

/**
 * @author JamesTKhan
 * @version November 02, 2022
 */
class UpdateNoiseTextureEvent {
    interface UpdateNoiseTextureListener {
        @Subscribe
        fun onTextureUpdate(event: UpdateNoiseTextureEvent)
    }
}