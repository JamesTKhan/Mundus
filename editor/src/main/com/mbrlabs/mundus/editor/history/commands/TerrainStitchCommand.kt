package com.mbrlabs.mundus.editor.history.commands

import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.editor.history.Command

/**
 * Wrapper for multiple TerrainHeightCommands since Terrain Stitching modifies multiple terrains.
 *
 * On construction, setHeightDataBefore is called immediately.
 * Implementations must call setHeightDataAfter after modifying terrains but before executing.
 *
 * @author JamesTKhan
 * @version June 26, 2023
 */
class TerrainStitchCommand(private var terrains: Array<Terrain>) : Command {
    private var terrainHeightCommands = HashMap<Terrain, TerrainHeightCommand>()

    init {
        for (terrain in terrains) {
            val command = TerrainHeightCommand(terrain)
            command.setHeightDataBefore(terrain.heightData)
            terrainHeightCommands[terrain] = command
        }
    }

    fun setHeightDataAfter() {
        for (terrain in terrains) {
            val command = terrainHeightCommands[terrain]
            command!!.setHeightDataAfter(terrain.heightData)
        }
    }

    override fun execute() {
        for (terrain in terrains) {
            val command = terrainHeightCommands[terrain]
            command!!.execute()
        }
    }

    override fun undo() {
        for (terrain in terrains) {
            val command = terrainHeightCommands[terrain]
            command!!.undo()
        }
    }

}