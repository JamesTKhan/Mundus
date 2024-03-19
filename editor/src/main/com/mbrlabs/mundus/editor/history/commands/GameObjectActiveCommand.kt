package com.mbrlabs.mundus.editor.history.commands

import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.history.Command
import com.mbrlabs.mundus.editorcommons.events.GameObjectModifiedEvent

/**
 * Command to change the active state of a game object.
 * @author JamesTKhan
 * @version August 27, 2023
 */
class GameObjectActiveCommand(val gameObject: GameObject, val active: Boolean) : Command {
    override fun execute() {
        gameObject.active = active
        Mundus.postEvent(GameObjectModifiedEvent(gameObject))
        Mundus.postEvent(SceneGraphChangedEvent())
    }

    override fun undo() {
        gameObject.active = !active
        Mundus.postEvent(GameObjectModifiedEvent(gameObject))
        Mundus.postEvent(SceneGraphChangedEvent())
    }
}