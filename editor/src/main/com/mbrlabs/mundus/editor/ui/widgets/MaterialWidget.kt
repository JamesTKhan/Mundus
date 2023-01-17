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
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.MaterialAsset
import com.mbrlabs.mundus.commons.assets.TexCoordInfo
import com.mbrlabs.mundus.commons.assets.TextureAsset
import com.mbrlabs.mundus.commons.utils.ModelUtils
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException
import com.mbrlabs.mundus.editor.assets.AssetMaterialFilter
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.MaterialDuplicatedEvent
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog
import com.mbrlabs.mundus.editor.utils.Colors
import java.io.FileNotFoundException

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
    private val matDuplicatedBtn: VisTextButton = VisTextButton("duplicate")
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

    private val roughnessField = ImprovedSlider(0.0f, 1.0f, 0.01f)
    private val metallicField = ImprovedSlider(0.0f, 1.0f, 0.01f)
    private val opacityField = ImprovedSlider(0.0f, 1.0f, 0.01f)
    private val alphaTestField = ImprovedSlider(0.0f, 1.0f, 0.01f)
    private val normalScaleField = ImprovedSlider(0.5f, 5f, 0.01f)
    private val shadowBiasField = ImprovedSlider(0.1f, 2f, 0.01f)

    private val scaleUField = FloatFieldWithLabel("Scale U", -1, false)
    private val scaleVField = FloatFieldWithLabel("Scale V", -1, false)

    private val offsetUField = ImprovedSlider(0.0f, 1.0f, 0.01f)
    private val offsetVField = ImprovedSlider(0.0f, 1.0f, 0.01f)
    private val rotateUVField = ImprovedSlider(0.0f, 6.3f, 0.01f)

    private val cullFaceSelectBox: VisSelectBox<CullFace> = VisSelectBox()

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
                shadowBiasField.value = value.shadowBias

                val cullValues = Array<CullFace>()
                for (cullValue in CullFace.values())
                    cullValues.add(cullValue)
                cullFaceSelectBox.items = cullValues

                cullFaceSelectBox.selected = CullFace.getFromValue(value.cullFace)

                scaleUField.textField.text = value.diffuseTexCoord.scaleU.toString()
                scaleVField.textField.text = value.diffuseTexCoord.scaleV.toString()
                offsetUField.value = value.diffuseTexCoord.offsetU
                offsetVField.value = value.diffuseTexCoord.offsetV
                rotateUVField.value = value.diffuseTexCoord.rotationUV
            }
        }

    /**
     * An optional listener for changing the material. If the property is null
     * the user will not be able to change the material.
     */
    var matChangedListener: MaterialChangedListener? = null
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

    }

    fun setupWidgets() {
        defaults().padBottom(4f)
        val table = VisTable()
        table.add(matNameLabel).grow()
        matNameLabel.color = Colors.TEAL
        table.add<VisTextButton>(matDuplicatedBtn)
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

        add(ToolTipLabel("Emissive texture", "The emissive texture. It controls the color and intensity " +
                "of the light being emitted by the material.\n This texture contains RGB components encoded with the sRGB transfer function.")).left().row()
        add(emissiveAssetField).growX().row()

        add(ToolTipLabel("Metallic/Roughness Texture", "The textures for metalness and roughness properties are packed together in a single texture called\n"+
                "metallicRoughnessTexture. Its green channel contains roughness values and its blue channel contains metalness values")).left().row()
        add(metallicRoughnessAssetField).growX().row()

        add(ToolTipLabel("Occlusion Texture", "The occlusion texture. The occlusion values are linearly sampled from the R channel.")).left().row()
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

        sliderTable.add(ToolTipLabel("Alpha Test", "If the alpha value is greater than or equal to " +
                "this value then it is rendered as fully opaque, otherwise, it is rendered as fully transparent.\n" +
                "Useful for models like trees that have leaf textures with alpha values")).left()
        sliderTable.add(alphaTestField).growX().row()

        sliderTable.add(VisLabel("Normal Scale")).growX()
        sliderTable.add(normalScaleField).growX().row()

        sliderTable.add(ToolTipLabel("Shadow Bias", "Increase to reduce shadow acne. Increase wisely as " +
                "higher bias results in peter-panning effect.")).left()
        sliderTable.add(shadowBiasField).growX().row()

        val cullTip = buildString {
            append("NONE: No culling\n")
            append("DEFAULT: Use Mundus Default (GL_BACK)\n")
            append("GL_BACK: Back face culling, recommended for performance.\n")
            append("GL_FRONT: Front face culling.\n")
            append("GL_FRONT_AND_BACK: Entire model culled (front and back).")
        }
        sliderTable.add(ToolTipLabel("Cull Face", cullTip)).left()
        sliderTable.add(cullFaceSelectBox).left()

        add(sliderTable).growX().row()

        addSeparator().padTop(15f).padBottom(15f).growX().row()

        val texTable = VisTable()
        texTable.defaults().padBottom(10f)

        texTable.add(VisLabel("Offset U")).growX()
        texTable.add(offsetUField).growX().row()
        texTable.add(VisLabel("Offset V")).growX()
        texTable.add(offsetVField).growX().row()
        texTable.add(VisLabel("Rotate UV")).growX()
        texTable.add(rotateUVField).growX().row()

        add(texTable).growX().row()

        add(scaleUField).growX().row()
        add(scaleVField).growX().row()

        matDuplicatedBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Dialogs.showInputDialog(UI, "Name:", "", object : InputDialogAdapter() {
                    override fun finished(input: String?) {
                        if (input != null) {
                            try {
                                val newMaterial = projectManager.current().assetManager.createMaterialAsset(input)
                                if (material != null) {
                                    newMaterial.duplicateMaterialAsset(material)
                                }
                                material = newMaterial
                                matChangedListener?.materialChanged(material!!)
                                Mundus.postEvent(MaterialDuplicatedEvent())
                            } catch (e: AssetAlreadyExistsException) {
                                Dialogs.showErrorDialog(UI, "That material already exists. Try a different name.")
                            } catch (e: FileNotFoundException) {
                                Dialogs.showErrorDialog(UI, "Invalid material name.")
                            }
                        }
                    }
                })
            }
        })

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

        shadowBiasField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (material?.shadowBias == shadowBiasField.value) return
                material?.shadowBias = shadowBiasField.value
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        })

        cullFaceSelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (material?.cullFace == cullFaceSelectBox.selected.value) return
                material?.cullFace = cullFaceSelectBox.selected.value
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        })

        val texCoordListener = object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                applyAllTexCoords()
                applyMaterialToModelAssets()
                applyMaterialToModelComponents()
                projectManager.current().assetManager.addModifiedAsset(material!!)
            }
        }

        scaleUField.addListener(texCoordListener)
        scaleVField.addListener(texCoordListener)
        offsetUField.addListener(texCoordListener)
        offsetVField.addListener(texCoordListener)
        rotateUVField.addListener(texCoordListener)
    }

    private fun applyAllTexCoords() {
        applyTexCoord(material?.diffuseTexCoord)
        applyTexCoord(material?.normalTexCoord)
        applyTexCoord(material?.emissiveTexCoord)
        applyTexCoord(material?.metallicRoughnessTexCoord)
        applyTexCoord(material?.occlusionTexCoord)
    }

    private fun applyTexCoord(diffuseTexCoord: TexCoordInfo?) {
        diffuseTexCoord?.scaleU = scaleUField.float
        diffuseTexCoord?.scaleV = scaleVField.float
        diffuseTexCoord?.offsetU = offsetUField.value
        diffuseTexCoord?.offsetV = offsetVField.value
        diffuseTexCoord?.rotationUV = rotateUVField.value
    }

    // TODO find better solution than iterating through all components
    private fun applyMaterialToModelComponents() {
        ModelUtils.applyGameObjectMaterials(projectManager.current().currScene.sceneGraph.root)
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

    /**
     * Simple enum for GL Cull Face int values,
     * used for select box in UI
     */
    enum class CullFace(val value: Int) {
        NONE(0),
        DEFAULT(-1),
        GL_BACK(GL20.GL_BACK),
        GL_FRONT(GL20.GL_FRONT),
        GL_FRONT_AND_BACK(GL20.GL_FRONT_AND_BACK);

        companion object {
            fun getFromValue(value: Int): CullFace {
                when (value) {
                    NONE.value -> return NONE
                    DEFAULT.value -> return DEFAULT
                    GL_BACK.value -> return GL_BACK
                    GL_FRONT.value -> return GL_FRONT
                    GL_FRONT_AND_BACK.value -> return GL_FRONT_AND_BACK
                }
                return DEFAULT
            }
        }
    }
}
