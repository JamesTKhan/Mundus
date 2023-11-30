package com.mbrlabs.mundus.editor.assets

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent

/**
 * Asset item in the grid.
 */
class AssetItem(
    val asset: Asset,
    val assetOpsMenu: PopupMenu?,
    val exportTerrainAsset: MenuItem?
) : VisTable() {

    private val nameLabel: VisLabel
    private var nameTable: VisTable
    private val stack = Stack()

    private val thumbnailOverlay: TextureRegionDrawable
    private var selectedOverlay: Image

    init {
        // Darkening overlay to darken texture thumbnails slightly so text is more visible
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0.0f,0.0f,0.0f,0.5f)
        pixmap.fill()
        thumbnailOverlay = TextureRegionDrawable(TextureRegion(Texture(pixmap)))

        selectedOverlay = Image(VisUI.getSkin().getDrawable("default-select-selection"))
        selectedOverlay.color.a = 0.6f

        setBackground("menu-bg")
        align(Align.center)
        nameLabel = VisLabel(asset.toString(), "tiny")
        nameLabel.wrap = true

        nameTable = VisTable()
        nameTable.add(nameLabel).grow().top().row()

        loadBackground()

        stack.add(nameTable)
        add(stack).grow().top().row()
    }

    fun setSelected() {
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