package com.mbrlabs.mundus.editor.events

import main.com.mbrlabs.mundus.editorcommons.Subscribe

/**
 * Event which is posted when files are dropped from an operating system into the mundus window.
 *
 * @author JamesTKhan
 * @version July 18, 2022
 */
class FilesDroppedEvent(val files: Array<out String>?) {

    interface FilesDroppedListener {
        @Subscribe
        fun onFilesDropped(event: FilesDroppedEvent)
    }
}