package com.mbrlabs.mundus.editor.ui.modules.outline

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.kryo.KryoManager
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent
import com.mbrlabs.mundus.editor.events.TerrainRemovedEvent
import com.mbrlabs.mundus.editor.tools.ToolManager
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.Log

/**
 * Holds code for Outlines right click menu. Separated from Outline class
 * due to its complexity
 *
 * @author JamesTKhan
 * @version August 03, 2022
 */
class OutlineRightClickMenu(outline: Outline) : PopupMenu() {
    companion object {
        private val TAG = OutlineRightClickMenu::class.java.simpleName
    }

    private val outline: Outline
    private val projectManager: ProjectManager = Mundus.inject()
    private val kryoManager: KryoManager = Mundus.inject()
    private val toolManager: ToolManager = Mundus.inject()

    init {
        this.outline = outline
    }

    //region Main menu
    private val duplicate: MenuItem = MenuItem("Duplicate")
    private val rename: MenuItem = MenuItem("Rename")
    private val delete: MenuItem = MenuItem("Delete")
    private val moveMenuItem: MenuItem = MenuItem("Move")
    private val moveMenu: MoveSubMenu = MoveSubMenu()
    private val addMenuItem: MenuItem = MenuItem("Add")
    private val addMenu: AddSubMenu = AddSubMenu()
    private val actionsMenuItem: MenuItem = MenuItem("Actions")
    private val actionsMenu: ActionSubMenu = ActionSubMenu()

    private var selectedGO: GameObject? = null
    private var currentNode: Outline.OutlineNode? = null

    init {
        moveMenuItem.subMenu = moveMenu
        addMenuItem.subMenu = addMenu
        actionsMenuItem.subMenu = actionsMenu

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
                    outline.duplicateGO(selectedGO!!, selectedGO!!.parent)
                    Mundus.postEvent(SceneGraphChangedEvent())
                }
            }
        })

        // delete game object
        delete.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (selectedGO != null) {
                    outline.removeGo(selectedGO!!)
                    if (toolManager.isSelected(selectedGO!!)) {
                        toolManager.activeTool!!.onDisabled()
                    }

                    val terrainComponent = selectedGO!!.findComponentByType(Component.Type.TERRAIN) as TerrainComponent?
                    if (terrainComponent != null) {
                        Mundus.postEvent(TerrainRemovedEvent(terrainComponent))
                    }
                }
            }
        })

        addItem(moveMenuItem)
        addItem(addMenuItem)
        addItem(actionsMenuItem)
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
    fun show(node: Outline.OutlineNode?, go: GameObject?, x: Float, y: Float) {
        selectedGO = go
        currentNode = node
        showMenu(UI, x, y)
        moveMenu.show()
        actionsMenu.show()

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


        // some assets can not be duplicated
        duplicate.isDisabled = selectedGO == null
                || selectedGO!!.findComponentByType(Component.Type.TERRAIN) != null
                || selectedGO!!.findComponentByType(Component.Type.WATER) != null
    }

    fun showRenameDialog() {
        val node = outline.tree.findNode(selectedGO!!)
        val goNode = node.actor as Outline.NodeTable

        val renameDialog = Dialogs.showInputDialog(
            UI, "Rename", "",
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
    //endregion Main Menu

    //region Move Sub Menu
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
                    val currentNodeIndex = parentChildArray.indexOf(currentNode!!.value)
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
                    val currentNodeIndex = parentChildArray.indexOf(currentNode!!.value)
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
                    val currentNodeIndex = parentChildArray.indexOf(currentNode!!.value)

                    // If already at top, return
                    if (currentNodeIndex == 0) return

                    parentChildArray.removeValue(currentNode!!.value, true)
                    parentChildArray.insert(0, currentNode!!.value)
                    updateChildren()
                }
            })

            moveToBottom.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val parentChildArray = getParentChildArray()
                    val currentNodeIndex = parentChildArray.indexOf(currentNode!!.value)

                    // If already at bottom, return
                    if (currentNodeIndex == parentChildArray.size - 1) return

                    parentChildArray.removeValue(currentNode!!.value, true)
                    parentChildArray.insert(parentChildArray.size, currentNode!!.value)
                    updateChildren()
                }
            })
        }

        fun show() {
            moveMenuItem.isDisabled = currentNode == null
            if (currentNode == null) return

            val parentChildArray: Array<GameObject> = getParentChildArray()

            // Disable move menu items depending on nodes current index
            val nodeIndex = parentChildArray.indexOf(currentNode!!.value, true)
            moveUp.isDisabled = nodeIndex == 0
            moveToTop.isDisabled = nodeIndex == 0
            moveDown.isDisabled = nodeIndex == parentChildArray.size - 1
            moveToBottom.isDisabled = nodeIndex == parentChildArray.size - 1
        }

        /**
         * Get the parents child array of current node, for root nodes .parent is null
         */
        fun getParentChildArray(): Array<GameObject> {
            return currentNode!!.value.parent.children
        }

        private fun updateChildren() {
            outline.buildTree(currentNode!!.value.sceneGraph)
        }
    }
    //endregion Move Sub Menu

    //region Add Sub Menu
    /**
     * A submenu to allow to add GameObjects to the scene
     */
    private inner class AddSubMenu : PopupMenu() {
        private val addEmpty: MenuItem = MenuItem("Add Empty")
        private val addTerrain: MenuItem = MenuItem("Add Terrain")
        private val addWater: MenuItem = MenuItem("Add Water")
        init {
            addItem(addEmpty)
            addItem(addTerrain)
            addItem(addWater)

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
                        outline.addGoToTree(null, go)
                    } else {
                        Log.trace(TAG, "Add empty game object [{}] child in node [{}].", go, selectedGO)
                        // update sceneGraph
                        selectedGO!!.addChild(go)
                        // update outline
                        val n = outline.tree.findNode(selectedGO!!)
                        outline.addGoToTree(n, go)
                    }
                    Mundus.postEvent(SceneGraphChangedEvent())
                }
            })

            // add terrainAsset
            addTerrain.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    UI.showDialog(UI.addTerrainDialog)
                }
            })

            // add waterAsset
            addWater.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    UI.showDialog(UI.addWaterDialog)
                }
            })
        }

    }
    //endregion Add Sub Menu

    //region Action Sub Menu
    /**
     * A submenu to allow adding GameObjects to the scene
     */
    private inner class ActionSubMenu : PopupMenu() {
        private val toggleActive: MenuItem = MenuItem("Toggle active")

        init {
            addItem(toggleActive)

            toggleActive.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    currentNode!!.value.active = !currentNode!!.value.active
                    outline.buildTree(currentNode!!.value.sceneGraph)
                }
            })
        }

        fun show() {
            if (currentNode == null) {
                actionsMenuItem.isDisabled = true
                return
            } else {
                actionsMenuItem.isDisabled = false
            }

            if (currentNode!!.value.active) {
                toggleActive.text = "Deactivate"
            } else {
                toggleActive.text = "Activate"
            }
        }

    }
    //endregion Action Sub Menu

}
