/*
 * Copyright (c) 2023. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.ui.modules.inspector.assets

import com.mbrlabs.mundus.commons.assets.TerrainObjectsAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget
import com.mbrlabs.mundus.editor.ui.widgets.TerrainObjectLayerWidget

class TerrainObjectsAssetInspectorWidget() : BaseInspectorWidget(TITLE)  {
    companion object {
        private val TITLE = "Terrain Objects Asset"
    }

    private var terrainObjectLayerWidget : TerrainObjectLayerWidget? = null
    private var objectAsset: TerrainObjectsAsset? = null

    init {
        isDeletable = false
    }

    fun setTerrainLObjectsAsset(objectAsset: TerrainObjectsAsset) {
        this.objectAsset = objectAsset
        if (terrainObjectLayerWidget == null) {
            terrainObjectLayerWidget = TerrainObjectLayerWidget(objectAsset, false)
            collapsibleContent.add(terrainObjectLayerWidget).growX().row()
        } else {
            terrainObjectLayerWidget!!.setTerrainObjectsAsset(objectAsset)
        }
    }

    override fun onDelete() {

    }

    override fun setValues(go: GameObject) {

    }

}