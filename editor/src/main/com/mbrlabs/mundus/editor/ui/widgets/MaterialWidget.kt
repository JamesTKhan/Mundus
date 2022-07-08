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

package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.MaterialAsset
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetMaterialFilter
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog

/**
 * Displays all properties of a material.
 *
 * You can also edit materials and replace them with another materials by.
 *
 * @author Marcus Brummer
 * @version 13-10-2016
 */
class MaterialWidget : VisTable() {

    private val matFilter: AssetMaterialFilter = AssetMaterialFilter()
    private val matChangedBtn: VisTextButton = VisTextButton("change")
    private val matPickerListener: AssetPickerDialog.AssetPickerListener

    private val matNameLabel: VisLabel = VisLabel()
    private val diffuseColorField: ColorPickerField = ColorPickerField()
    private val emissiveColorField: ColorPickerField = ColorPickerField()
    private val diffuseAssetField: AssetSelectionField = AssetSelectionField()
    private val normalMapField: AssetSelectionField = AssetSelectionField()
    private val emissiveAssetField: AssetSelectionField = AssetSelectionField()
    private val metallicRoughnessAssetField: AssetSelectionField = AssetSelectionField()
    private val occlusionAssetField: AssetSelectionField = AssetSelectionField()

    private val roughnessField = ImprovedSlider(0.0f, 1.0f, 0.05f)
    private val metallicField = ImprovedSlider(0.0f, 1.0f, 0.05f)
    private val opacityField = ImprovedSlider(0.0f, 1.0f, 0.05f)
    private val alphaTestField = ImprovedSlider(0.0f, 1.0f, 0.05f)
    private val normalScaleField = ImprovedSlider(0.5f, 5f, 0.5f)

    private val projectManager: ProjectManager = Mundus.inject()

    /**
     * The currently active material of the widget.
     */
    var material: MaterialAsset? = null
        set(value) {
            if (value != null) {
                field = value
                diffuseColorField.selectedColor = value.diffuseColor
                emissiveColorField.selectedColor = value.emissiveColor
                diffuseAssetField.setAsset(value.diffuseTexture)
                normalMapField.setAsset(value.normalMap)
                emissiveAssetField.setAsset(value.emissiveTexture)
                metallicRoughnessAssetField.setAsset(value.metallicRoughnessTexture)
                occlusionAssetField.setAsset(value.occlusionTexture)
                matNameLabel.setText(value.name)
                roughnessField.value = value.roughness
                metallicField.value = value.metallic
                opacityField.value = value.opacity
                alphaTestField.value = value.alphaTest
                normalScaleField.value = value.normalScale
            }
        }

    /**
     * An optional listener for changing the material. If the property is null
     * the user will not be able to change the material.
     */
    var matChangedListener: MaterialWidget.MaterialChangedListener? = null
        set(value) {
            field = value
            matChangedBtn.touchable = if(value == null) Touchable.disabled else Touchable.enabled
        }

    init {
        align(Align.topLeft)
        matNameLabel.setWrap(true)

        matPickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material = asset as? MaterialAsset
                matChangedListener?.materialChanged(material!!)
            }
        }

        setupWidgets()
    }

    private fun setupWidgets() {
        defaults().padBottom(4f)
        val table = VisTable()
        table.add(matNameLabel).grow()
        table.add<VisTextButton>(matChangedBtn).padLeft(4f).right().row()
        add(table).grow().row()

        addSeparator().padTop(15f).padBottom(15f).growX().row()

        add(VisLabel("Diffuse color")).grow().row()
        add(diffuseColorField).growX().row()
        add(VisLabel("Emissive color")).grow().row()
        add(emissiveColorField).growX().row()

        add(VisLabel("Diffuse texture")).grow().row()
        add(diffuseAssetField).growX().row()
        add(VisLabel("Normal map")).grow().row()
        add(normalMapField).growX().row()
        add(VisLabel("Emissive texture")).grow().row()
        add(emissiveAssetField).growX().row()
        add(VisLabel("Metallic/Roughness Texture")).grow().row()
        add(metallicRoughnessAssetField).growX().row()
        add(VisLabel("Occlusion Texture")).grow().row()
        add(occlusionAssetField).growX().row()

        addSeparator().padTop(15f).padBottom(15f).growX().row()

        val sliderTable = VisTable()
        sliderTable.defaults().padBottom(10f)
        sliderTable.add(VisLabel("Roughness")).growX()
        sliderTable.add(roughnessField).growX().row()

        sliderTable.add(VisLabel("Metallic")).growX()
        sliderTable.add(metallicField).growX().row()

        sliderTable.add(VisLabel("Opacity")).growX()
        sliderTable.add(opacityField).growX().row()

        sliderTable.add(VisLabel("Alpha Test")).growX()
        sliderTable.add(alphaTestField).growX().row()

        sliderTable.add(VisLabel("Normal Scale")).growX()
        sliderTable.add(normalScaleField).growX().row()

        add(sliderTable).growX()

        matChangedBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                UI.assetSelectionDialog.show(false, matFilter, matPickerListener)
            }
        })

        // diffuse texture
        diffuseAssetField.assetFilter = AssetTextureFilter()
        diffuseAssetField.pickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material?.diffuseTexture = asset as? TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        // normal texture
        normalMapField.assetFilter = AssetTextureFilter()
        normalMapField.pickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material?.normalMap = asset as? TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        // emissive texture
        emissiveAssetField.assetFilter = AssetTextureFilter()
        emissiveAssetField.pickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material?.emissiveTexture = asset as? TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        // metallic/roughness texture
        metallicRoughnessAssetField.assetFilter = AssetTextureFilter()
        metallicRoughnessAssetField.pickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material?.metallicRoughnessTexture = asset as? TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        // occlusion texture
        occlusionAssetField.assetFilter = AssetTextureFilter()
        occlusionAssetField.pickerListener = object: AssetPickerDialog.AssetPickerListener {
            override fun onSelected(asset: Asset?) {
                material?.occlusionTexture = asset as? TextureAsset
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        // diffuse color
        diffuseColorField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                material?.diffuseColor?.set(newColor)
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        // emissive color
        emissiveColorField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                material?.emissiveColor?.set(newColor)
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        // roughness
        roughnessField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (material?.roughness == roughnessField.value) return
                material?.roughness = roughnessField.value
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        })

        // metallic
        metallicField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (material?.metallic == metallicField.value) return
                material?.metallic = metallicField.value
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        })

        // opacity
        opacityField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (material?.opacity == opacityField.value) return
                material?.opacity = opacityField.value
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        })

        // alpha test
        alphaTestField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (material?.alphaTest == alphaTestField.value) return
                material?.alphaTest = alphaTestField.value
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        })


        // normal scale
        normalScaleField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (material?.normalScale == normalScaleField.value) return
                material?.normalScale = normalScaleField.value
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        })

    }

    // TODO find better solution than iterating through all components
    private fun applyMaterialToModelComponents() {
        val sceneGraph = projectManager.current().currScene.sceneGraph
        for (go in sceneGraph.gameObjects) {
            val mc = go.findComponentByType(Component.Type.MODEL)
            if (mc != null && mc is ModelComponent) {
                mc.applyMaterials()
            }
        }
    }

    // TODO find better solution than iterating through all assets
    private fun applyMaterialToModelAssets() {
        val assetManager = projectManager.current().assetManager
        for (modelAsset in assetManager.modelAssets) {
            modelAsset.applyDependencies()
        }
    }

    /**
     *
     */
    interface MaterialChangedListener {
        fun materialChanged(materialAsset: MaterialAsset)
    }


}
