package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.kotcrab.vis.ui.widget.VisScrollPane

/**
 * @author JamesTKhan
 * @version July 28, 2022
 */
class AutoFocusScrollPane(actor: Actor) : VisScrollPane(actor) {
    init {
        addListener(object : InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                stage.scrollFocus = this@AutoFocusScrollPane
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                stage.scrollFocus = null
            }
        })
    }
}