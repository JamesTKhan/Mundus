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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.TerrainObjectLayerAsset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.MetaSaver
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush
import com.mbrlabs.mundus.editor.ui.widgets.TerrainObjectLayerWidget
import com.mbrlabs.mundus.editor.ui.widgets.TerrainObjectWidget

class TerrainObjectTab(private val parentWidget: TerrainComponentWidget) : BaseBrushTab(parentWidget, TerrainBrush.BrushMode.TERRAIN_OBJECT) {

    private val root = VisTable()
    private val terrainObjectLayerWidget = TerrainObjectLayerWidget(parentWidget.component.terrainAsset.terrainObjectLayerAsset, parentWidget.component)

    private val projectManager: ProjectManager = Mundus.inject()
    private val metaSaver: MetaSaver = Mundus.inject()

    init {
        root.add(terrainObjectLayerWidget).expand().fill().padBottom(5f).row()
        root.add(TerrainObjectWidget()).expand().fill().row()

        terrainObjectLayerWidget.layerChangedListener = object : TerrainObjectLayerWidget.LayerChangedListener {
            override fun layerChanged(terrainObjectLayerAsset: TerrainObjectLayerAsset) {
                // Assign the new object layer asset to the terrain
                parentWidget.component.terrainAsset.terrainObjectLayerAsset = terrainObjectLayerAsset
                parentWidget.component.applyTerrainObjects(true)
                metaSaver.save(parentWidget.component.terrainAsset.meta)
                projectManager.current().assetManager.addModifiedAsset(parentWidget.component.terrainAsset)
            }

        }
    }

    override fun getTabTitle(): String = "Objets"

    override fun getContentTable(): Table = root

    override fun onShow() {
        super.onShow()

        terrainObjectLayerWidget.textureGrid.clearHighlight()
    }
}
