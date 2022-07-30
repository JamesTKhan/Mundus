package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.kotcrab.vis.ui.util.adapter.ListAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.mbrlabs.mundus.editor.ui.UI

/**
 * @author JamesTKhan
 * @version July 28, 2022
 */
class AutoFocusListView<ItemT>(adapter: ListAdapter<ItemT>) : ListView<ItemT>(adapter) {
    init {
        scrollPane.addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                UI.scrollFocus = scrollPane
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                UI.scrollFocus = null
            }
        })
    }
}