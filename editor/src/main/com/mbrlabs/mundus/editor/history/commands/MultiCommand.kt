package com.mbrlabs.mundus.editor.history.commands

import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.editor.history.Command

/**
 * A wrapper that Executes multiple commands at once.
 * Useful if you perform an action using multiple commands (Ex. Scale+Rotate object)
 * and you want to execute both in a single undo/redo step.
 *
 * @author JamesTKhan
 * @version August 27, 2023
 */
class MultiCommand : Command {
    private var commands = Array<Command>()

    override fun execute() {
        for (command in commands) {
            command.execute()
        }
    }

    override fun undo() {
        // undo, but in reverse order
        for (i in commands.size - 1 downTo 0) {
            commands[i].undo()
        }
    }

    fun addCommand(command: Command) {
        commands.add(command)
    }
}