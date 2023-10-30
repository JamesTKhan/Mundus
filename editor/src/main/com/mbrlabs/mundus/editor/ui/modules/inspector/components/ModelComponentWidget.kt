/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.assets.MaterialAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent
import com.mbrlabs.mundus.editor.ui.widgets.MaterialSelectWidget
import com.mbrlabs.mundus.editor.ui.widgets.MaterialWidget
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
class ModelComponentWidget(modelComponent: ModelComponent) : ComponentWidget<ModelComponent>("Model Component", modelComponent) {

    private val materialContainer = VisTable()
    private val useModelCache = VisCheckBox(null)

    init {
        this.component = modelComponent
        setupUI()
    }

    private fun setupUI() {
        // create Model select dropdown
        collapsibleContent.add(VisLabel("Model")).left().row()
        collapsibleContent.addSeparator().padBottom(5f).row()
        //collapsibleContent.add(selectBox).expandX().fillX().row();
        collapsibleContent.add(VisLabel("Model asset: " + component.modelAsset.name)).grow().padBottom(15f).row()

        val cacheTable = VisTable()
        cacheTable.add(ToolTipLabel("Use in Model Cache? ", "Merges meshes together to reduce draw counts.\nNote: Only applicable for static, non-moving, non-animated models!")).padRight(2f)
        cacheTable.add(useModelCache).left()
        collapsibleContent.add(cacheTable).left().padBottom(5f).row()

        // create materials for all model nodes
        collapsibleContent.add(VisLabel("Materials")).expandX().fillX().left().padBottom(3f).padTop(3f).row()
        collapsibleContent.addSeparator().row()

        val label = VisLabel()
        label.setWrap(true)
        label.setText("Here you change the materials of model components individually.\n"
                + "Modifying the material will update all components, that use that material.")
        collapsibleContent.add(label).grow().padBottom(10f).row()

        collapsibleContent.add(materialContainer).grow().row()
        buildMaterials()

        useModelCache.isChecked = component.shouldCache()

        useModelCache.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (component.shouldCache() == useModelCache.isChecked) return
                component.setUseModelCache(useModelCache.isChecked)
                component.gameObject.sceneGraph.scene.modelCacheManager.requestModelCacheRebuild()
            }
        })
    }

    private fun buildMaterials() {
        materialContainer.clear()
        for (g3dbMatID in component.materials.keys()) {

            val material = component.materials[g3dbMatID]
            val selectWidget = MaterialSelectWidget(material)
            materialContainer.add(selectWidget).grow().padBottom(20f).row()
            selectWidget.matChangedListener = object: MaterialWidget.MaterialChangedListener {
                override fun materialChanged(materialAsset: MaterialAsset) {
                    component.materials.put(g3dbMatID, materialAsset)
                    component.applyMaterials()
                }
            }
        }
    }

    override fun setValues(go: GameObject) {
        val c: ModelComponent? = go.findComponentByType(Component.Type.MODEL)
        if (c != null) {
            component = c
        }
    }

}
