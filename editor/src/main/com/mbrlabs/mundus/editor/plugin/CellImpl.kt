package com.mbrlabs.mundus.editor.plugin

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

class CellImpl(vararg val cells: com.badlogic.gdx.scenes.scene2d.ui.Cell<out Actor>) : com.mbrlabs.mundus.pluginapi.ui.Cell {

    override fun setAlign(align: WidgetAlign): com.mbrlabs.mundus.pluginapi.ui.Cell {
        when (align) {
            WidgetAlign.LEFT -> cells.forEach { it.align(Align.left) }
            WidgetAlign.CENTER -> cells.forEach { it.align(Align.center) }
            WidgetAlign.RIGHT -> cells.forEach {  it.align(Align.right) }
        }

        return this
    }

    override fun setPad(top: Float, right: Float, bottom: Float, left: Float): com.mbrlabs.mundus.pluginapi.ui.Cell {
        cells.forEach { it.pad(top, left, bottom, right) }

        return this
    }
}
