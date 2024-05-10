package com.mbrlabs.mundus.editor.plugin

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.RootWidgetCell

class RootWidgetCellImpl(private val rootWidget: Cell<RootWidgetImpl>) : CellImpl(rootWidget), RootWidgetCell {

    override fun getRootWidget(): RootWidget = rootWidget.actor
}
