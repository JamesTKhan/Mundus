package com.mbrlabs.mundus.editor

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Timer
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.events.TerrainLoDRebuildEvent
import com.mbrlabs.mundus.editor.events.TerrainVerticesChangedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.LoDUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Scheduled LoD rebuilding when needed.
 * Listens to events that indicate an LoD needs to be rebuilt.
 * @author JamesTKhan
 * @version October 04, 2023
 */
class LevelOfDetailScheduler : TerrainVerticesChangedEvent.TerrainVerticesChangedEventListener,
    TerrainLoDRebuildEvent.TerrainLoDRebuildEventListener, Disposable {
    var executorService: ExecutorService = Executors.newSingleThreadExecutor()

    private var timer: Timer.Task? = null
    private var terrainSet = HashSet<TerrainComponent>()

    init {
        Mundus.registerEventListener(this)

        timer = Timer.schedule(object : Timer.Task() {
            override fun run() {

                if (terrainSet.isEmpty()) return

                val callable = LoDUtils.createTerrainLodProcessingTask(terrainSet) {
                    UI.toaster.success("Terrain Level of Details generated.")
                }

                executorService.submit(callable)
                terrainSet.clear()
            }
        }, 0f, 1f)

    }

    override fun onTerrainVerticesChanged(event: TerrainVerticesChangedEvent) {
        terrainSet.add(event.terrainComponent)
    }

    override fun onTerrainLoDRebuild(event: TerrainLoDRebuildEvent) {
        terrainSet.add(event.terrainComponent)
    }

    override fun dispose() {
        Mundus.unregisterEventListener(this)
        executorService.shutdown()
        timer?.cancel()
    }
}