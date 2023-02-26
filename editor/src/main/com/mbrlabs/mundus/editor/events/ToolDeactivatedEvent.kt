package com.mbrlabs.mundus.editor.events

class ToolDeactivatedEvent {

    interface ToolDeactivatedEventListener {
        @Subscribe
        fun onToolDeactivatedEvent(event: ToolDeactivatedEvent)
    }
}
