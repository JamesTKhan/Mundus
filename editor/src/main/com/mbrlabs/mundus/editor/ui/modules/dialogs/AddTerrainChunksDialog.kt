package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.assets.TerrainAsset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.commons.terrain.TerrainLoader
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.assets.MetaSaver
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.ElevationModifier
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.NoiseModifier
import com.mbrlabs.mundus.editor.terrain.noise.modifiers.TerrainModifier
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.IntegerFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.NoiseGeneratorWidget
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel
import com.mbrlabs.mundus.editor.utils.createTerrainGO
import java.util.concurrent.Executors


/**
 * @author JamesTKhan
 * @version October 24, 2022
 */
class AddTerrainChunksDialog : BaseDialog("Add Terrain Chunks") {
    private var loadingDialog: VisDialog? = null
    private lateinit var parentGO: GameObject
    private lateinit var modifierTable: VisTable
    private val name = VisTextField("Terrain")

    private val vertexResolution = IntegerFieldWithLabel("", -1, false)
    private val terrainWidth = IntegerFieldWithLabel("", -1, false)
    private val minHeight = FloatFieldWithLabel("", -1, true)
    private val maxHeight = FloatFieldWithLabel("", -1, true)
    private val gridX = IntegerFieldWithLabel("", -1, false)
    private val gridZ = IntegerFieldWithLabel("", -1, false)

    private val generateBtn = VisTextButton("Generate Terrain")
    private val noiseGeneratorWidget : NoiseGeneratorWidget

    private var projectManager : ProjectManager
    private var kryoManager : KryoManager
    private var metaSaver : MetaSaver

    init {
        isResizable = true
        projectManager = Mundus.inject()
        kryoManager = Mundus.inject()
        metaSaver = Mundus.inject()

        noiseGeneratorWidget = NoiseGeneratorWidget()

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        vertexResolution.text = Terrain.DEFAULT_VERTEX_RESOLUTION.toString()
        terrainWidth.text = Terrain.DEFAULT_SIZE.toString()

        minHeight.text = (-50f).toString()
        maxHeight.text = 50f.toString()

        gridX.text = "2"
        gridZ.text = "2"

        val root = VisTable()
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root).row()

        // left table
        val leftTable = VisTable()
        leftTable.defaults().pad(4f)
        leftTable.left().top()

        root.add(VisLabel("Terrain Settings")).row()

        leftTable.add(VisLabel("Name: ")).left().padBottom(10f)
        leftTable.add(name).fillX().expandX().row()

        leftTable.add(
            ToolTipLabel("Vertex resolution: ", "This will determine the vertices count when squared. 180 = 32,400 vertices. \n" +
                "The default value (or lower) is recommended for performance. \nSettings this over 180 may cause issues on some devices.")
        ).left()
        leftTable.add(vertexResolution).fillX().expandX().row()
        leftTable.add(ToolTipLabel("Terrain width: ", "Size of the terrain, in meters.")).left()
        leftTable.add(terrainWidth).fillX().expandX().row()

        leftTable.add(ToolTipLabel("Min height", "The minimum height any point on the generated terrain will have. Can be negative")).left()
        leftTable.add(minHeight).left().row()
        leftTable.add(ToolTipLabel("Max height", "The maximum height any point on the generated terrain will have.")).left()
        leftTable.add(maxHeight).left().row()

        leftTable.add(ToolTipLabel("X Iterations", "The number of Terrain Chunks to create on the X axis")).left()
        leftTable.add(gridX).left().row()
        leftTable.add(ToolTipLabel("Z Iterations", "The number of Terrain Chunks to create on the Z axis")).left()
        leftTable.add(gridZ).left().row()

        leftTable.add(generateBtn).fillX()

        root.add(leftTable).top().fillX().expandX()
        root.addSeparator(true)

        // Center Table
        root.add(noiseGeneratorWidget).pad(4f).fillX().expandX()

        modifierTable = VisTable()
        buildModifierTable()

        root.addSeparator(true)
        root.add(modifierTable).top()
    }

    private fun buildModifierTable() {
        modifierTable.clear()

        val addModifierBtn = VisTextButton("Add Modifier")

        modifierTable.defaults().pad(4f)
        modifierTable.left().top()
        modifierTable.add(addModifierBtn).left().row()
        modifierTable.addSeparator().row()

        addModifierBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val mod = ElevationModifier()
                addModifierToList(mod)
                noiseGeneratorWidget.generator.modifiers.add(mod)
            }
        })

        for (mod in noiseGeneratorWidget.generator.modifiers) {
            addModifierToList(mod)
        }
    }

    private fun addModifierToList(mod: TerrainModifier) {
        val button = VisTextButton(mod.name)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (mod is NoiseModifier) {
                    val dialog = NoiseModifierDialog(mod)
                    dialog.show(UI)
                }
                super.clicked(event, x, y)
            }
        })

        modifierTable.add(button).left().row()
    }

    private var generatingTerrain = false
    private var terraformingThreads = 0
    private var creationThreads = 0
    private var assetsToTerraform = HashMap<Vector2, TerrainComponent>()
    private var assetsToCreate = Array<IntArray>()
    private var terrainChunkMatrix: TerrainChunkMatrix? = null

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (assetsToCreate.size > 0) {
            runCreationThreads()
        }

        if (assetsToTerraform.size > 0 && terraformingThreads == 0) {
            runTerraformingThreads()
        }

        if (terrainChunkMatrix?.isDone() == true) {
            setupNeighborTerrains()
        }

        if (generatingTerrain) {
            color.a = 0.4f
            val label = loadingDialog!!.contentTable.getChild(0) as Label
            label.setText("Creating: $creationThreads" + " Terraform Queue: ${assetsToTerraform.size}")

            loadingDialog!!.pack()
            if (creationThreads == 0 && assetsToTerraform.isEmpty()) {
                generatingTerrain = false
                color.a = 1.0f
                loadingDialog?.hide()
            }
        }

        super.draw(batch, parentAlpha)
    }

    private fun setupListeners() {
        // terraform btn
        generateBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                createTerrainChunk()
            }
        })

        vertexResolution.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                noiseGeneratorWidget.setNoiseTextureWidth(vertexResolution.int)
            }
        })
    }

    fun createTerrainChunk() {
        val context = projectManager.current()
        val sceneGraph = context.currScene.sceneGraph
        val goID = projectManager.current().obtainID()

        // Save context here so that the ID above is persisted in .pro file
        kryoManager.saveProjectContext(projectManager.current())

        parentGO = GameObject(context.currScene.sceneGraph, "Terrain Chunks", goID)
        val res: Int = vertexResolution.int
        val width: Int = terrainWidth.int

        noiseGeneratorWidget.generator
            .minHeight(minHeight.float)
            .maxHeight(maxHeight.float)

        val xIteration = gridX.int
        val yIteration = gridZ.int

        var assetExists = false
        for (i in 0 until xIteration) {
            for (j in 0 until yIteration) {

                // Before we start, make sure the terrain name does not already exist
                val name = "${name.text}$i-$j.terra.meta"
                if (projectManager.current().assetManager.assetExists(name)) {
                    assetExists = true
                    assetsToCreate.clear()
                    Dialogs.showErrorDialog(UI, "Terrain with name $name already exists. Pick a different name or\n remove existing asset.")
                    break
                }

                assetsToCreate.add(intArrayOf(res, width, i, j))
            }
            if (assetExists) break
        }

        if (assetExists) return

        terrainChunkMatrix = TerrainChunkMatrix(xIteration, yIteration)

        generatingTerrain = true
        loadingDialog = Dialogs.showOKDialog(UI, "Generating Terrain", "Generating ")
        val button = loadingDialog!!.buttonsTable.getChild(0) as VisTextButton
        button.isDisabled = true

        sceneGraph.addGameObject(parentGO)
        Mundus.postEvent(SceneGraphChangedEvent())
    }

    private fun runCreationThreads() {
        val context = projectManager.current()
        val sceneGraph = context.currScene.sceneGraph
        // Start a new thread pool for creating the assets
        val executorService = Executors.newFixedThreadPool(10)

        for (arr in assetsToCreate) {
            val goID = projectManager.current().obtainID()

            // Submit thread job
            executorService.submit {
                creationThreads++
                val res = arr[0]
                val width = arr[1]
                val i = arr[2]
                val j = arr[3]

                val asset: TerrainAsset
                val loader: TerrainLoader
                try {
                    asset = createTerrainAsset(res, width, i, j)
                    loader = asset.startAsyncLoad()
                } catch (ex: AssetAlreadyExistsException) {
                    Dialogs.showErrorDialog(stage, "An asset with that name already exists.")
                    executorService.shutdownNow()
                    creationThreads--
                    //break
                    return@submit
                }

                // post a Runnable to the rendering thread that processes the result
                Gdx.app.postRunnable {
                    creationThreads--
                    asset.finishSyncLoad(loader)

                    // set base texture
                    val chessboard =
                        projectManager.current().assetManager.findAssetByID(EditorAssetManager.STANDARD_ASSET_TEXTURE_CHESSBOARD)
                    if (chessboard != null) {
                        asset.splatBase = chessboard as TextureAsset
                        asset.applyDependencies()
                        metaSaver.save(asset.meta)
                    }

                    projectManager.current().assetManager.addAsset(asset)

                    val terrainGO = createTerrainGO(
                        sceneGraph,
                        null, goID, "${name.text}$i-$j", asset
                    )

                    terrainGO.setLocalPosition((i * width).toFloat(), 0f, (j * width).toFloat())
                    val component = terrainGO.findComponentByType(Component.Type.TERRAIN) as TerrainComponent

                    context.currScene.terrains.add(component)
                    projectManager.current().assetManager.addNewAsset(asset)
                    Mundus.postEvent(AssetImportEvent(asset))

                    parentGO.addChild(terrainGO)
                    Mundus.postEvent(SceneGraphChangedEvent())

                    // Now Queue it up for terraforming
                    assetsToTerraform[Vector2(i.toFloat(), j.toFloat())] = component

                    // Add generated terrain chunk to matrix
                    terrainChunkMatrix!!.addTerrain(i, j, component)
                }
            }
        }

        // After the jobs are submitted, clear the queue and shutdown executor (shuts down AFTER jobs complete)
        assetsToCreate.clear()
        executorService.shutdown()
    }

    private fun runTerraformingThreads() {
        val grid = assetsToTerraform.entries.first().key
        val component = assetsToTerraform.entries.first().value
        val asset = component.terrainAsset
        assetsToTerraform.remove(grid)

        Thread {
            terraformingThreads++
            val generator = noiseGeneratorWidget.generator

            generator.offset(grid.x.toInt(), grid.y.toInt()).setTerrain(asset.terrain).terraform()

            // post a Runnable to the rendering thread that processes the result
            Gdx.app.postRunnable { // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
                terraformingThreads--
                asset.terrain.update()
            }
        }.start()
    }

    private fun createTerrainAsset(resolution: Int, width: Int, i: Int, j: Int): TerrainAsset {
        // create asset
        val asset: TerrainAsset = projectManager.current().assetManager.createTerraAssetAsync(
            "${name.text}$i-$j",
            resolution, width, 512
        )

        return asset
    }

    /**
     * Setups neighbor terrains for each terrain.
     */
    private fun setupNeighborTerrains() {
        val terrainComponents = terrainChunkMatrix!!.terrainComponents

        for (x in 0 until terrainComponents.size) {
            for (y in 0 until terrainComponents[x].size) {
                if (y-1 >= 0) {
                    terrainComponents[x][y]!!.topNeighbor = terrainComponents[x][y-1]
                }
                if (x+1 < terrainComponents.size) {
                    terrainComponents[x][y]!!.rightNeighbor = terrainComponents[x+1][y]
                }
                if (y+1 < terrainComponents[x].size) {
                    terrainComponents[x][y]!!.bottomNeighbor = terrainComponents[x][y+1]
                }
                if (x-1 >= 0) {
                    terrainComponents[x][y]!!.leftNeighbor = terrainComponents[x-1][y]
                }
            }
        }

        terrainChunkMatrix = null
    }

    inner class TerrainChunkMatrix(x: Int, y: Int) {

        private var remainingTerrainComponents = x * y
        val terrainComponents = Array(x) { Array<TerrainComponent?>(y) {null} }

        fun addTerrain(x: Int, y: Int, terrainComponent: TerrainComponent) {
            --remainingTerrainComponents

            terrainComponents[x][y] = terrainComponent
        }

        fun isDone(): Boolean = remainingTerrainComponents == 0
    }

}
