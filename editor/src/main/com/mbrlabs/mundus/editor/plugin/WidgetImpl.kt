package com.mbrlabs.mundus.editor.plugin

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.utils.Align
import com.mbrlabs.mundus.pluginapi.ui.Widget

class WidgetImpl(vararg val cells: Cell<out Actor>) : Widget {

    override fun setAlign(align: Widget.WidgetAlign) {
        when (align) {
            Widget.WidgetAlign.LEFT -> cells.forEach { it.align(Align.left) }
            Widget.WidgetAlign.CENTER -> cells.forEach { it.align(Align.center) }
            Widget.WidgetAlign.RIGHT -> cells.forEach {  it.align(Align.right) }
        }
    }
}
