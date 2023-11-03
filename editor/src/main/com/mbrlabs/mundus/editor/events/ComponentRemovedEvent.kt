package com.mbrlabs.mundus.editor.events

import main.com.mbrlabs.mundus.editorcommons.Subscribe

/**
 * @author JamesTKhan
 * @version June 02, 2022
 */
class ComponentRemovedEvent {
    interface ComponentRemovedListener {
        @Subscribe
        fun onComponentRemoved(event: ComponentRemovedEvent)
    }
}