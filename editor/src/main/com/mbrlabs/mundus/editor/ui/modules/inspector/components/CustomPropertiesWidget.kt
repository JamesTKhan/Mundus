package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget

class CustomPropertiesWidget : BaseInspectorWidget("Custom Properties") {

    init {
        isDeletable = false
    }
    override fun onDelete() {
        // The custom properties component can't be deleted.
    }

    override fun setValues(go: GameObject) {
        // TODO
    }
}