package com.mbrlabs.mundus.editor.events

import main.com.mbrlabs.mundus.editorcommons.Subscribe

class ToolDeactivatedEvent {

    interface ToolDeactivatedEventListener {
        @Subscribe
        fun onToolDeactivatedEvent(event: ToolDeactivatedEvent)
    }
}
