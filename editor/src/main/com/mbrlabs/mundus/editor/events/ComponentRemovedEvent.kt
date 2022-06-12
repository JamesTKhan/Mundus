package com.mbrlabs.mundus.editor.events

/**
 * @author James Pooley
 * @version June 02, 2022
 */
class ComponentRemovedEvent {
    interface ComponentRemovedListener {
        @Subscribe
        fun onComponentRemoved(event: ComponentRemovedEvent)
    }
}