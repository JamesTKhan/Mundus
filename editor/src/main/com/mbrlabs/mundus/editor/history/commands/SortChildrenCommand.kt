package com.mbrlabs.mundus.editor.history.commands

import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.history.Command

class SortChildrenCommand(private val childArray: Array<GameObject>) : Command {

    private val originalSorting = Array<Int>()

    init {
        childArray.forEach { originalSorting.add(it.id) }
    }

    override fun execute() {
        childArray.sort(GameObjectExecuteComparator())
        Mundus.postEvent(SceneGraphChangedEvent())
    }

    override fun undo() {
        childArray.sort(GameObjectUndoComparator(originalSorting))
        Mundus.postEvent(SceneGraphChangedEvent())
    }

    inner class GameObjectExecuteComparator : Comparator<GameObject> {
        override fun compare(left: GameObject, right: GameObject): Int = left.name.compareTo(right.name)
    }

    inner class GameObjectUndoComparator(private val originalSorting: Array<Int>) : Comparator<GameObject> {
        override fun compare(left: GameObject, right: GameObject): Int = originalSorting.indexOf(left.id) - originalSorting.indexOf(right.id)
    }
}
