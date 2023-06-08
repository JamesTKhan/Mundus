package com.mbrlabs.mundus.editor.events

class ToolActivatedEvent {

    interface ToolActivatedEventListener {
        @Subscribe
        fun onToolActivatedEvent(event: ToolActivatedEvent)
    }
}
