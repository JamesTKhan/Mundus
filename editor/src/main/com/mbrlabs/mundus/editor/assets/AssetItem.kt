package com.mbrlabs.mundus.editor.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.TerrainAsset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent
import com.mbrlabs.mundus.editor.ui.UI

/**
 * Asset item in the grid.
 */
class AssetItem(
    val asset: Asset,
    val thumbnailOverlay: Drawable,
    val selectedOverlay: Actor,
    val exportTerrainAsset: MenuItem,
    val assetOpsMenu: PopupMenu
) : VisTable() {

    private val nameLabel: VisLabel
    private var nameTable: VisTable
    val stack = Stack()

    init {
        setBackground("menu-bg")
        align(Align.center)
        nameLabel = VisLabel(asset.displayName, "tiny")
        nameLabel.wrap = true

        nameTable = VisTable()
        nameTable.add(nameLabel).grow().top().row()

        loadBackground()

        stack.add(nameTable)
        add(stack).grow().top().row()

        addListener(object : InputListener() {


            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (event!!.button == Input.Buttons.RIGHT) {
                    setSelected()

                    if (asset is TerrainAsset) {
                        if (!exportTerrainAsset.hasParent())
                            assetOpsMenu.addItem(exportTerrainAsset)
                    } else {
                        exportTerrainAsset.remove()
                        assetOpsMenu.pack()
                    }

                    assetOpsMenu.showMenu(
                        UI, Gdx.input.x.toFloat(),
                        (Gdx.graphics.height - Gdx.input.y).toFloat()
                    )
                } else if (event.button == Input.Buttons.LEFT) {
                    setSelected()
                }
            }

        })
    }

    fun setSelected() {
//        this@AssetsDock.setSelected(this@AssetItem)
        Mundus.postEvent(AssetSelectedEvent(asset))
    }

    private fun loadBackground() {
        if (asset is TextureAsset) {
            nameTable.background = thumbnailOverlay
            stack.add(Image(asset.texture))
        }
    }

    fun toggleSelectOverlay(selected: Boolean) {
        if (selected) {
            // Remove the name table from stack, put the selected overlay on, then put the name table back on
            // the stack, over top of the select overlay
            stack.removeActor(nameTable)
            stack.add(selectedOverlay)
            stack.add(nameTable)
        } else {
            stack.removeActor(selectedOverlay)
        }
    }
}