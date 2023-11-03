package com.mbrlabs.mundus.editor.events

import main.com.mbrlabs.mundus.editorcommons.Subscribe

class ToolActivatedEvent {

    interface ToolActivatedEventListener {
        @Subscribe
        fun onToolActivatedEvent(event: ToolActivatedEvent)
    }
}
