/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mbrlabs.mundus.editor.ui.modules

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.*
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.commons.terrain.Terrain
import com.mbrlabs.mundus.commons.utils.LightUtils
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.*
import com.mbrlabs.mundus.editor.history.CommandHistory
import com.mbrlabs.mundus.editor.history.commands.DeleteCommand
import com.mbrlabs.mundus.editor.scene3d.components.PickableLightComponent
import com.mbrlabs.mundus.editor.shader.Shaders
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.Colors
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.createTerrainGO
import com.mbrlabs.mundus.editor.utils.createWaterGO

/**
 * Outline shows overview about all game objects in the scene
 *
 * @author Marcus Brummer, codenigma
 * @version 01-10-2016
 */
// TODO refactor...kind of messy spaghetti code!
class Outline : VisTable(),
        ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener,
        SceneGraphChangedEvent.SceneGraphChangedListener,
        GameObjectSelectedEvent.GameObjectSelectedListener {

    private val content: VisTable
    private val tree: VisTree<OutlineNode, GameObject>
    private val scrollPane: ScrollPane
    private val dragAndDrop: DragAndDrop = DragAndDrop()
    private val rightClickMenu: RightClickMenu

    private val toolManager: ToolManager = Mundus.inject()
    private val projectManager: ProjectManager = Mundus.inject()
    private val history: CommandHistory = Mundus.inject()
    private val kryoManager: KryoManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)
        setBackground("window-bg")

        rightClickMenu = RightClickMenu()

        content = VisTable()
        content.align(Align.left or Align.top)

        tree = VisTree<OutlineNode, GameObject>()
        tree.selection.setProgrammaticChangeEvents(false)
        tree.indentSpacing = 10f
        scrollPane = VisScrollPane(tree)
        scrollPane.setFlickScroll(false)
        scrollPane.setFadeScrollBars(false)
        content.add(scrollPane).fill().expand()

        add(VisLabel(TITLE)).expandX().fillX().pad(3f).row()
        addSeparator().row()
        add(content).fill().expand()

        setupDragAndDrop()
        setupListeners()
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        // update to new sceneGraph
        Log.trace(TAG, "Project changed. Building scene graph.")
        buildTree(projectManager.current().currScene.sceneGraph)
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        // update to new sceneGraph
        Log.trace(TAG, "Scene changed. Building scene graph.")
        buildTree(projectManager.current().currScene.sceneGraph)
    }

    override fun onSceneGraphChanged(event: SceneGraphChangedEvent) {
        Log.trace(TAG, "SceneGraph changed. Building scene graph.")
        buildTree(projectManager.current().currScene.sceneGraph)
    }

    private fun setupDragAndDrop() {
        // source
        dragAndDrop.addSource(object : DragAndDrop.Source(tree) {
            override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): DragAndDrop.Payload? {
                val payload = DragAndDrop.Payload()
                val node = tree.getNodeAt(y)
                if (node != null) {
                    payload.`object` = node
                    return payload
                }

                return null
            }
        })

        // target
        dragAndDrop.addTarget(object : DragAndDrop.Target(tree) {
            override fun drag(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int): Boolean {
                // Select node under mouse if not over the selection.
                val overNode = tree.getNodeAt(y)
                if (overNode == null && tree.getSelection().isEmpty) {
                    return true
                }
                if (overNode != null && !tree.getSelection().contains(overNode)) {
                    tree.getSelection().set(overNode)
                }
                return true
            }

            override fun drop(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int) {
                val context = projectManager.current()
                val newParent = tree.getNodeAt(y)

                @Suppress("UNCHECKED_CAST")
                val node: Tree.Node<OutlineNode, GameObject, VisTable> = (payload.`object` as? Tree.Node<OutlineNode, GameObject, VisTable>) ?: return
                val draggedGo: GameObject = node.value

                // check if a go is dragged in one of its' children or
                // itself
                if (newParent != null) {
                    val parentGo = newParent.value
                    if (parentGo.isChildOf(draggedGo)) {
                        return
                    }
                }
                val oldParent = draggedGo.parent

                // remove child from old parent
                draggedGo.remove()

                // add to new parent
                if (newParent == null) {

                    // if moved from old parent
                    if (oldParent != null) {
                        // Convert draggedGo from old parents local space to world space
                        val world = draggedGo.transform.mulLeft(oldParent.transform)
                        world.getTranslation(tmpPos)
                        world.getRotation(tmpQuat, true)
                        world.getScale(tmpScale)

                        // add
                        context.currScene.sceneGraph.root.addChild(draggedGo)
                        draggedGo.setLocalPosition(tmpPos.x, tmpPos.y, tmpPos.z)
                        draggedGo.setLocalRotation(tmpQuat.x, tmpQuat.y, tmpQuat.z, tmpQuat.w)
                        draggedGo.setLocalScale(tmpScale.x, tmpScale.y, tmpScale.z)
                    } else {
                        // Is this scenario even possible right now? Null new and old parent.
                        val newPos = draggedGo.getPosition(tmpPos)
                        // new local position = World position
                        draggedGo.setLocalPosition(newPos.x, newPos.y, newPos.z)
                    }

                } else {
                    val parentGo = newParent.value

                    // Convert draggedGo to new parents local space
                    val local = draggedGo.transform.mulLeft(parentGo.transform.inv())
                    local.getTranslation(tmpPos)
                    local.getRotation(tmpQuat, true)
                    local.getScale(tmpScale)

                    // add
                    parentGo.addChild(draggedGo)
                    draggedGo.setLocalPosition(tmpPos.x, tmpPos.y, tmpPos.z)
                    draggedGo.setLocalRotation(tmpQuat.x, tmpQuat.y,tmpQuat.z, tmpQuat.w)
                    draggedGo.setLocalScale(tmpScale.x, tmpScale.y, tmpScale.z)
                }

                // update tree
                buildTree(projectManager.current().currScene.sceneGraph)
            }

        })
    }

    private fun setupListeners() {

        scrollPane.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                UI.scrollFocus = scrollPane
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                UI.scrollFocus = null
            }

        })

        tree.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (tapCount != 2)
                    return

                val go = tree.getNodeAt(y)?.value

                if (go != null) {
                    val context = projectManager.current()
                    val pos = Vector3()
                    go.transform.getTranslation(pos)

                    // just lerp in the direction of the object if certain distance away
                    if (pos.dst(context.currScene.cam.position) > 100)
                        context.currScene.cam.position.lerp(pos.cpy().add(0f,40f,0f), 0.5f)

                    context.currScene.cam.lookAt(pos)
                    context.currScene.cam.up.set(Vector3.Y)
                }

            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (Input.Buttons.LEFT != button) {
                    return true
                }
                return super.touchDown(event, x, y, pointer, button)
            }

            // right click menu listener
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (Input.Buttons.RIGHT != button) {
                    super.touchUp(event, x, y, pointer, button)
                    return
                }

                val node = tree.getNodeAt(y)
                var go: GameObject? = null
                if (node != null) {
                    go = node.value
                }
                rightClickMenu.show(node, go, Gdx.input.x.toFloat(), (Gdx.graphics.height - Gdx.input.y).toFloat())
            }

        })

        // select listener
        tree.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val selection = tree.getSelection()
                if (selection != null && selection.size() > 0) {
                    val go = selection.first().value
                    projectManager.current().currScene.sceneGraph.selected = go
                    toolManager.translateTool.gameObjectSelected(go)
                    Mundus.postEvent(GameObjectSelectedEvent(go))
                }
            }
        })

    }

    /**
     * Building tree from game objects in sceneGraph, clearing previous
     * sceneGraph

     * @param sceneGraph
     */
    private fun buildTree(sceneGraph: SceneGraph) {
        tree.clearChildren()

        var containsWater = false

        for (go in sceneGraph.gameObjects) {
            addGoToTree(null, go)

            if (containsWater) continue

            val waterComponent = go.findComponentByType(Component.Type.WATER)
            if (waterComponent != null) {
                containsWater = true
            }
        }

        sceneGraph.isContainsWater = containsWater

        // After tree is rebuilt, we must set the selected GO again if there is one
        if (projectManager.current().currScene.currentSelection != null) {
            val node = tree.findNode(projectManager.current().currScene.currentSelection)
            tree.selection.clear()
            if (node != null)
                tree.selection.add(node)
        }
    }

    /**
     * Adding game object to outline

     * @param treeParentNode
     * *
     * @param gameObject
     */
    private fun addGoToTree(treeParentNode: Tree.Node<OutlineNode, GameObject, NodeTable>?, gameObject: GameObject) {
        val leaf = OutlineNode(NodeTable(gameObject), gameObject)
        if (treeParentNode == null) {
            tree.add(leaf)
        } else {
            treeParentNode.add(leaf)
        }
        // Always expand after adding new node
        leaf.expandTo()
        if (gameObject.children != null) {
            for (goChild in gameObject.children) {
                addGoToTree(leaf, goChild)
            }
        }
    }

    /**
     * Removing game object from tree and outline

     * @param go
     */
    private fun removeGo(go: GameObject) {
        // run delete command, updating sceneGraph and outline
        val deleteCommand = DeleteCommand(go, tree.findNode(go))
        history.add(deleteCommand)
        deleteCommand.execute() // run delete
    }

    /**
     * Deep copy of all game objects

     * @param go
     * *            the game object for cloning, with children
     * *
     * @param parent
     * *            game object on which clone will be added
     */
    private fun duplicateGO(go: GameObject, parent: GameObject) {
        Log.trace(TAG, "Duplicate [{}] with parent [{}]", go, parent)
        val goCopy = GameObject(go, projectManager.current().obtainID())

        // Handle duplicated light components
        val lightComponent = goCopy.findComponentByType(Component.Type.LIGHT)
        if (lightComponent != null) {
            lightComponent as LightComponent

            // Remove the duplicated light component
            goCopy.removeComponent(lightComponent)
            lightComponent.remove()

            // This is a bit of a workaround, since we are in editor here, we replace the duplicated lightComponent
            // with a pickable version instead.
            val pickableLightComponent = PickableLightComponent(goCopy, lightComponent.light.lightType)
            LightUtils.copyLightSettings(lightComponent.light, pickableLightComponent.light)

            goCopy.addComponent(pickableLightComponent)
            Mundus.postEvent(ComponentAddedEvent(pickableLightComponent))
        }

        // add copy to tree
        val n = tree.findNode(parent)
        addGoToTree(n, goCopy)

        // add copy to scene graph
        parent.addChild(goCopy)

        // recursively clone child objects
        if (go.children != null) {
            for (child in go.children) {
                duplicateGO(child, goCopy)
            }
        }
    }

    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        val node = tree.findNode(event.gameObject!!)
        Log.trace(TAG, "Select game object [{}].", node.value)
        tree.selection.clear()
        tree.selection.add(node)
        node.expandTo()
    }

    /**
     * TODO
     */
    inner class NodeTable(go: GameObject) : VisTable() {

        val nameLabel: VisLabel = VisLabel()

        init {
            add(nameLabel).expand().fill()
            nameLabel.setText(go.name)
            if (!go.active) nameLabel.color = Colors.GRAY_888
        }
    }

    inner class OutlineNode(table: NodeTable, gameObject: GameObject) : Tree.Node<OutlineNode, GameObject, NodeTable>(table) {

        init {
            value = gameObject
        }
    }

    /**

     */
    private inner class RightClickMenu : PopupMenu() {

        /**
         * A submenu to allow moving nodes up and down within the outline tree
         */
        private inner class MoveSubMenu : PopupMenu() {
            private val moveUp: MenuItem = MenuItem("Move Up")
            private val moveDown: MenuItem = MenuItem("Move Down")
            private val moveToTop: MenuItem = MenuItem("Move To Top")
            private val moveToBottom: MenuItem = MenuItem("Move To Bottom")
            init {
                addItem(moveUp)
                addItem(moveDown)
                addItem(moveToTop)
                addItem(moveToBottom)

                moveUp.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val parentChildArray = getParentChildArray()
                        val currentNodeIndex = parentChildArray.indexOf(currentNode)
                        // If the current Node is NOT the first one...
                        if (currentNodeIndex > 0) {
                            parentChildArray.swap(currentNodeIndex, currentNodeIndex-1)
                            updateChildren()
                        }
                    }
                })

                moveDown.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val parentChildArray = getParentChildArray()
                        val currentNodeIndex = parentChildArray.indexOf(currentNode)
                        // If the current Node is NOT the last one...
                        if (currentNodeIndex < parentChildArray.size - 1) {
                            parentChildArray.swap(currentNodeIndex, currentNodeIndex+1)
                            updateChildren()
                        }
                    }
                })

                moveToTop.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val parentChildArray = getParentChildArray()
                        val currentNodeIndex = parentChildArray.indexOf(currentNode)

                        // If already at top, return
                        if (currentNodeIndex == 0) return

                        parentChildArray.removeValue(currentNode, true)
                        parentChildArray.insert(0, currentNode)
                        updateChildren()
                    }
                })

                moveToBottom.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        val parentChildArray = getParentChildArray()
                        val currentNodeIndex = parentChildArray.indexOf(currentNode)

                        // If already at bottom, return
                        if (currentNodeIndex == parentChildArray.size - 1) return

                        parentChildArray.removeValue(currentNode, true)
                        parentChildArray.insert(parentChildArray.size, currentNode)
                        updateChildren()
                    }
                })
            }

            fun show() {
                moveMenuItem.isDisabled = currentNode == null
                if (currentNode == null) return

                val parentChildArray: Array<OutlineNode> = getParentChildArray()

                // Disable move menu items depending on nodes current index
                val nodeIndex = parentChildArray.indexOf(currentNode, true)
                moveUp.isDisabled = nodeIndex == 0
                moveToTop.isDisabled = nodeIndex == 0
                moveDown.isDisabled = nodeIndex == parentChildArray.size - 1
                moveToBottom.isDisabled = nodeIndex == parentChildArray.size - 1
            }

            /**
             * Get the parents child array of current node, for root nodes .parent is null
             */
            fun getParentChildArray(): Array<OutlineNode> {
                return if (currentNode!!.parent == null) {
                    tree.rootNodes
                } else {
                    currentNode!!.parent.children
                }
            }

            private fun updateChildren() {
                if (currentNode!!.parent == null) {
                    tree.updateRootNodes()
                } else {
                    currentNode!!.parent.updateChildren()
                }
            }
        }

        private val addEmpty: MenuItem = MenuItem("Add Empty")
        private val addTerrain: MenuItem = MenuItem("Add terrain")
        private val addWater: MenuItem = MenuItem("Add water")
        private val duplicate: MenuItem = MenuItem("Duplicate")
        private val rename: MenuItem = MenuItem("Rename")
        private val delete: MenuItem = MenuItem("Delete")
        private val moveMenuItem: MenuItem = MenuItem("Move")
        private val moveMenu: MoveSubMenu = MoveSubMenu()

        private var selectedGO: GameObject? = null
        private var currentNode: OutlineNode? = null

        init {
            moveMenuItem.subMenu = moveMenu
            moveMenu.show()
            // add empty
            addEmpty.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val sceneGraph = projectManager.current().currScene.sceneGraph
                    val id = projectManager.current().obtainID()
                    // the new game object
                    val go = GameObject(sceneGraph, GameObject.DEFAULT_NAME, id)
                    // update outline
                    if (selectedGO == null) {
                        // update sceneGraph
                        Log.trace(TAG, "Add empty game object [{}] in root node.", go)
                        sceneGraph.addGameObject(go)
                        // update outline
                        addGoToTree(null, go)
                    } else {
                        Log.trace(TAG, "Add empty game object [{}] child in node [{}].", go, selectedGO)
                        // update sceneGraph
                        selectedGO!!.addChild(go)
                        // update outline
                        val n = tree.findNode(selectedGO!!)
                        addGoToTree(n, go)
                    }
                    Mundus.postEvent(SceneGraphChangedEvent())
                }
            })

            // add terrainAsset
            addTerrain.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    try {
                        Log.trace(TAG, "Add terrain game object in root node.")
                        val context = projectManager.current()
                        val sceneGraph = context.currScene.sceneGraph
                        val goID = projectManager.current().obtainID()

                        // Save context here so that the ID above is persisted in .pro file
                        kryoManager.saveProjectContext(projectManager.current())

                        val name = "Terrain " + goID
                        // create asset
                        val asset = context.assetManager.createTerraAsset(name,
                                Terrain.DEFAULT_VERTEX_RESOLUTION, Terrain.DEFAULT_SIZE)
                        asset.load()
                        asset.applyDependencies()

                        val terrainGO = createTerrainGO(sceneGraph,
                                Shaders.terrainShader, goID, name, asset)
                        // update sceneGraph
                        sceneGraph.addGameObject(terrainGO)
                        // update outline
                        addGoToTree(null, terrainGO)

                        context.currScene.terrains.add(asset)
                        projectManager.current().assetManager.addNewAsset(asset)
                        Mundus.postEvent(AssetImportEvent(asset))
                        Mundus.postEvent(SceneGraphChangedEvent())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })

            // add waterAsset
            addWater.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    try {
                        Log.trace(TAG, "Add water game object in root node.")
                        val context = projectManager.current()
                        val sceneGraph = context.currScene.sceneGraph
                        val goID = projectManager.current().obtainID()

                        // Save context here so that the ID above is persisted in .pro file
                        kryoManager.saveProjectContext(projectManager.current())

                        val name = "Water " + goID
                        // create asset
                        val asset = context.assetManager.createWaterAsset(name)
                        asset.load()
                        asset.applyDependencies()

                        val waterGO = createWaterGO(sceneGraph,
                                Shaders.waterShader, goID, name, asset)
                        // update sceneGraph
                        sceneGraph.addGameObject(waterGO)
                        // update outline
                        addGoToTree(null, waterGO)

                        projectManager.current().assetManager.addNewAsset(asset)
                        Mundus.postEvent(AssetImportEvent(asset))
                        Mundus.postEvent(SceneGraphChangedEvent())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })

            rename.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null) {
                        showRenameDialog()
                    }
                }
            })

            // duplicate node
            duplicate.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null && !duplicate.isDisabled) {
                        duplicateGO(selectedGO!!, selectedGO!!.parent)
                        Mundus.postEvent(SceneGraphChangedEvent())
                    }
                }
            })

            // delete game object
            delete.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (selectedGO != null) {
                        removeGo(selectedGO!!)
                    }
                }
            })


            addItem(moveMenuItem)
            addItem(addEmpty)
            addItem(addTerrain)
            addItem(addWater)
            addItem(rename)
            addItem(duplicate)
            addItem(delete)
        }

        /**
         * Right click event opens menu and enables more options if selected
         * game object is active.
         *
         * @param go
         * @param x
         * @param y
         */
        fun show(node: OutlineNode?, go: GameObject?, x: Float, y: Float) {
            selectedGO = go
            currentNode = node
            showMenu(UI, x, y)
            moveMenu.show()

            // check if game object is selected
            if (selectedGO != null) {
                // Activate menu options for selected game objects
                rename.isDisabled = false
                delete.isDisabled = false
            } else {
                // disable MenuItems which only works with selected item
                rename.isDisabled = true
                delete.isDisabled = true
            }

            // terrainAsset can not be duplicated
            duplicate.isDisabled = selectedGO == null || selectedGO!!.findComponentByType(Component.Type.TERRAIN) != null
        }

        fun showRenameDialog() {
            val node = tree.findNode(selectedGO!!)
            val goNode = node.actor as NodeTable

            val renameDialog = Dialogs.showInputDialog(UI, "Rename", "",
                    object : InputDialogAdapter() {
                        override fun finished(input: String?) {
                            Log.trace(TAG, "Rename game object [{}] to [{}].", selectedGO, input)
                            // update sceneGraph
                            selectedGO!!.name = input
                            // update Outline
                            //goNode.name.setText(input + " [" + selectedGO.id + "]");
                            goNode.nameLabel.setText(input)

                            Mundus.postEvent(SceneGraphChangedEvent())
                        }
                    })
            // set position of dialog to menuItem position
            val nodePosX = node.actor.x
            val nodePosY = node.actor.y
            renameDialog.setPosition(nodePosX, nodePosY)
        }
    }

    companion object {

        private val TITLE = "Outline"
        private val TAG = Outline::class.java.simpleName

        val tmpPos = Vector3()
        val tmpScale = Vector3()
        val tmpQuat = Quaternion()
    }
}
