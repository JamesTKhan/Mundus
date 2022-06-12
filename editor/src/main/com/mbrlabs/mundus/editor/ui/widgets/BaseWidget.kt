package com.mbrlabs.mundus.editor.ui.widgets

import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable

abstract class BaseWidget : VisTable() {
    fun getSectionTable(): VisTable {
        val table = VisTable()
        table.defaults().padLeft(10f).padBottom(5f)
        return table
    }

    fun addSectionHeader(header: String) {
        addSectionHeader(header, this)
    }

    fun addSectionHeader(header: String, table: VisTable) {
        table.add(VisLabel(header)).left().row()
        table.addSeparator().padBottom(5f).row()
    }
}