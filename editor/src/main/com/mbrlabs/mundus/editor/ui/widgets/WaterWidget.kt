package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent
import com.mbrlabs.mundus.commons.water.Water
import com.mbrlabs.mundus.commons.water.WaterResolution
import com.mbrlabs.mundus.commons.water.attributes.WaterColorAttribute
import com.mbrlabs.mundus.commons.water.attributes.WaterFloatAttribute
import com.mbrlabs.mundus.commons.water.attributes.WaterIntAttribute
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType


class WaterWidget(val waterComponent: WaterComponent) : VisTable() {

    private val visibleDepthField = VisTextField()
    private val tilingField = VisTextField()
    private val waveStrength = VisTextField()
    private val waveSpeed = VisTextField()
    private val foamPatternScale = VisTextField()
    private val foamEdgeBias = VisTextField()
    private val foamScrollSpeedFactor = VisTextField()
    private val foamFallOffDistance = VisTextField()
    private val foamEdgeDistance = VisTextField()
    private val reflectivity = VisTextField()
    private val shineDamper = VisTextField()

    private val enableReflections = VisCheckBox(null)
    private val enableRefractions = VisCheckBox(null)

    private val colorPickerField = ColorPickerField()
    private val cullFaceSelectBox: VisSelectBox<MaterialWidget.CullFace> = VisSelectBox()

    private lateinit var selectBox: VisSelectBox<String>

    private val resetDefaults = VisTextButton("Reset Defaults")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        align(Align.topLeft)
        setupWidgets()
    }

    private fun setupWidgets() {
        defaults().padBottom(10f)

        /* General section */
        add(VisLabel("General")).left().row()
        addSeparator().padBottom(5f).row()

        val generalSettings = getSectionTable()

        generalSettings.add(ToolTipLabel("Color:", "Color tint of the water. When using reflections/refractions, the alpha value\ncontrols the blending of the color")).growX()
        generalSettings.add(colorPickerField).left().row()
        // color
        colorPickerField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                waterComponent.waterAsset.water.setColorAttribute(WaterColorAttribute.Diffuse, newColor)
                projectManager.current().assetManager.addModifiedAsset(waterComponent.waterAsset)
            }

            override fun changed(newColor: Color?) {
                waterComponent.waterAsset.water.setColorAttribute(WaterColorAttribute.Diffuse, newColor)
            }

            override fun canceled(oldColor: Color?) {
                waterComponent.waterAsset.water.setColorAttribute(WaterColorAttribute.Diffuse, oldColor)
            }
        }

        generalSettings.add(ToolTipLabel("Max Visible Depth:", "Maximum depth where objects underwater are still visible.\nOnly applicable when refractions are enabled.")).growX()
        generalSettings.add(visibleDepthField).growX().row()

        // Cull Face select
        val cullTip = buildString {
            append("NONE: No culling\n")
            append("DEFAULT: Use Mundus Default (GL_BACK)\n")
            append("GL_BACK: Back face culling, recommended for performance.\n")
            append("GL_FRONT: Front face culling.\n")
            append("GL_FRONT_AND_BACK: Entire model culled (front and back).")
        }
        generalSettings.add(ToolTipLabel("Cull Face", cullTip)).left()
        generalSettings.add(cullFaceSelectBox).left().row()
        val cullValues = Array<MaterialWidget.CullFace>()
        for (cullValue in MaterialWidget.CullFace.values())
            cullValues.add(cullValue)
        cullFaceSelectBox.items = cullValues

        cullFaceSelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val value = cullFaceSelectBox.selected.value
                waterComponent.waterAsset.water.setIntAttribute(WaterIntAttribute.CullFace, value)
                projectManager.current().assetManager.addModifiedAsset(waterComponent.waterAsset)
            }
        })

        add(generalSettings).grow().row()

        /* Waves section */
        add(VisLabel("Waves")).left().row()
        addSeparator().padBottom(5f).row()

        val waveSettings = getSectionTable()

        waveSettings.add(ToolTipLabel("Tiling:", "Tiling of the ripples. The smaller the value, the more spaced out the ripples will be.")).growX()
        waveSettings.add(tilingField).growX().row()

        waveSettings.add(ToolTipLabel("Wave Strength:", "Affects how distorted reflections and refractions will be.\nHigher means more distortion.")).growX()
        waveSettings.add(waveStrength).growX().row()

        waveSettings.add(ToolTipLabel("Wave Speed:", "Affects how fast the ripples move on the water.")).growX()
        waveSettings.add(waveSpeed).growX().row()

        add(waveSettings).grow().row()

        /* Foam settings */
        add(VisLabel("Foam")).left().row()
        addSeparator().padBottom(5f).row()

        val foamSettings = getSectionTable()

        foamSettings.add(ToolTipLabel("Scale:", "Scales the foam texture. Higher value results in smaller texture.")).growX()
        foamSettings.add(foamPatternScale).growX().row()

        foamSettings.add(ToolTipLabel("Edge Bias:", "Affects the strength and fading of the foam coming from the shoreline out.")).growX()
        foamSettings.add(foamEdgeBias).growX().row()

        foamSettings.add(ToolTipLabel("Scroll Speed:", "Affects how fast the foam moves (scrolls).")).growX()
        foamSettings.add(foamScrollSpeedFactor).growX().row()

        foamSettings.add(ToolTipLabel("Fall Off Distance:", "Affects how far out the foam will travel.")).growX()
        foamSettings.add(foamFallOffDistance).growX().row()

        foamSettings.add(ToolTipLabel("Edge Fall Off Distance:", "Affects how far out the stronger solid edge foam will travel.")).growX()
        foamSettings.add(foamEdgeDistance).growX().row()

        add(foamSettings).grow().row()

        /* Lighting section */
        add(VisLabel("Lighting")).left().row()
        addSeparator().padBottom(5f).row()

        val lightingSettings = getSectionTable()

        lightingSettings.add(ToolTipLabel("Reflectivity:", "The strength of the specular highlights.")).growX()
        lightingSettings.add(reflectivity).growX().row()

        lightingSettings.add(ToolTipLabel("Shine Damper:", "Lowering this will increase how far the specular highlights will spread.")).growX()
        lightingSettings.add(shineDamper).growX().row()

        add(lightingSettings).grow().row()

        /* Quality section */
        add(VisLabel("Quality")).left().row()
        addSeparator().padBottom(5f).row()

        val qualitySettings = getSectionTable()

        val checkboxTable = VisTable()
        checkboxTable.defaults().padBottom(5f)
        checkboxTable.add(ToolTipLabel("Enable Reflections", "Toggles water reflections. Disabling them improves performance.")).padRight(2f)
        checkboxTable.add(enableReflections).left().row()
        enableReflections.isChecked = projectManager.current().currScene.settings.enableWaterReflections
        enableReflections.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                projectManager.current().currScene.settings.enableWaterReflections = enableReflections.isChecked
            }
        })

        checkboxTable.add(ToolTipLabel("Enable Refractions", "Toggles water refractions. Disabling them improves performance.")).padRight(2f)
        checkboxTable.add(enableRefractions).left().row()
        enableRefractions.isChecked = projectManager.current().currScene.settings.enableWaterRefractions
        enableRefractions.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                projectManager.current().currScene.settings.enableWaterRefractions = enableRefractions.isChecked
            }
        })

        qualitySettings.add(checkboxTable).left().padBottom(5f).row()

        val selectorsTable = VisTable(true)
        selectBox = VisSelectBox<String>()
        selectBox.setItems(
                WaterResolution._256.value,
                WaterResolution._512.value,
                WaterResolution._1024.value,
                WaterResolution._2048.value
        )
        selectorsTable.add(selectBox).left()

        qualitySettings.add(ToolTipLabel("Texture resolution:", "This resolution (Global per scene) is used for " +
                "multiple render passes\n to generate reflections and refractions in Frame Buffers.\nFor low end devices, mobile, and GWT use 256 or 512.")).growX().left()
        qualitySettings.add(selectorsTable).left().row()

        add(qualitySettings).grow().row()

        addSeparator().padBottom(5f).row()
        add(resetDefaults).padTop(10f).growX().row()

        // Register listeners for the float fields
        registerFloatFieldListener(visibleDepthField, WaterFloatAttribute.MaxVisibleDepth, 0.01f)
        registerFloatFieldListener(tilingField, WaterFloatAttribute.Tiling)
        registerFloatFieldListener(waveStrength, WaterFloatAttribute.WaveStrength)
        registerFloatFieldListener(waveSpeed, WaterFloatAttribute.WaveSpeed)
        registerFloatFieldListener(foamPatternScale, WaterFloatAttribute.FoamPatternScale)
        registerFloatFieldListener(foamEdgeBias, WaterFloatAttribute.FoamEdgeBias)
        registerFloatFieldListener(foamScrollSpeedFactor, WaterFloatAttribute.FoamScrollSpeed)
        registerFloatFieldListener(foamEdgeDistance, WaterFloatAttribute.FoamEdgeDistance)
        registerFloatFieldListener(foamFallOffDistance, WaterFloatAttribute.FoamFallOffDistance)
        registerFloatFieldListener(reflectivity, WaterFloatAttribute.Reflectivity)
        registerFloatFieldListener(shineDamper, WaterFloatAttribute.ShineDamper)

        // resolution
        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val res = WaterResolution.valueFromString(selectBox.selected)
                projectManager.current().currScene.setWaterResolution(res)
            }
        })

        resetDefaults.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.Tiling, Water.DEFAULT_TILING)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.WaveStrength, Water.DEFAULT_WAVE_STRENGTH)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.WaveSpeed, Water.DEFAULT_WAVE_SPEED)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.Reflectivity, Water.DEFAULT_REFLECTIVITY)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.ShineDamper, Water.DEFAULT_SHINE_DAMPER)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.FoamPatternScale, Water.DEFAULT_FOAM_SCALE)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.FoamScrollSpeed, Water.DEFAULT_FOAM_SCROLL_SPEED)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.FoamEdgeDistance, Water.DEFAULT_FOAM_EDGE_DISTANCE)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.FoamEdgeBias, Water.DEFAULT_FOAM_EDGE_BIAS)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.FoamFallOffDistance, Water.DEFAULT_FOAM_FALL_OFF_DISTANCE)
                waterComponent.waterAsset.water.setFloatAttribute(WaterFloatAttribute.MaxVisibleDepth, Water.DEFAULT_MAX_VISIBLE_DEPTH)

                waterComponent.waterAsset.water.setIntAttribute(WaterIntAttribute.CullFace, Water.DEFAULT_CULL_FACE)
                waterComponent.waterAsset.water.setColorAttribute(WaterColorAttribute.Diffuse, Water.DEFAULT_COLOR)

                projectManager.current().currScene.settings.waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION
                projectManager.current().currScene.settings.enableWaterReflections = true
                projectManager.current().currScene.settings.enableWaterRefractions = true
                projectManager.current().assetManager.addModifiedAsset(waterComponent.waterAsset)

                setFieldsToCurrentValues()
            }
        })

        setFieldsToCurrentValues()

    }

    private fun registerFloatFieldListener(floatField: VisTextField, attributeType: Long, min: Float? = null) {
        floatField.textFieldFilter = FloatDigitsOnlyFilter(false)
        floatField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (floatField.isInputValid && !floatField.isEmpty) {
                    try {
                        var value = floatField.text.toFloat()

                        // If min value specified we won't allow it go below the minimum
                        if (min != null && value < min) {
                            value = min
                        }

                        waterComponent.waterAsset.water.setFloatAttribute(attributeType, value)
                        projectManager.current().assetManager.addModifiedAsset(waterComponent.waterAsset)
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + floatField.name))
                    }
                }
            }
        })
    }

    fun setFieldsToCurrentValues() {
        visibleDepthField.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.MaxVisibleDepth).toString()
        tilingField.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.Tiling).toString()
        waveStrength.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.WaveStrength).toString()
        waveSpeed.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.WaveSpeed).toString()
        reflectivity.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.Reflectivity).toString()
        shineDamper.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.ShineDamper).toString()
        foamPatternScale.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.FoamPatternScale).toString()
        foamEdgeBias.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.FoamEdgeBias).toString()
        foamScrollSpeedFactor.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.FoamScrollSpeed).toString()
        foamFallOffDistance.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.FoamFallOffDistance).toString()
        foamEdgeDistance.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.FoamEdgeDistance).toString()

        colorPickerField.selectedColor = waterComponent.waterAsset.water.getColorAttribute(WaterColorAttribute.Diffuse)

        enableRefractions.isChecked = projectManager.current().currScene.settings.enableWaterRefractions
        enableReflections.isChecked = projectManager.current().currScene.settings.enableWaterReflections

        if (!selectBox.items.contains(projectManager.current().currScene.settings.waterResolution.value)) {
            selectBox.selected = WaterResolution.DEFAULT_WATER_RESOLUTION.value
        } else {
            selectBox.selected = projectManager.current().currScene.settings.waterResolution.value
        }

        val cullFace = waterComponent.waterAsset.water.getIntAttribute(WaterIntAttribute.CullFace)
        cullFaceSelectBox.selected = MaterialWidget.CullFace.getFromValue(cullFace)
    }

    private fun getSectionTable(): VisTable {
        val table = VisTable()
        table.defaults().padLeft(0f).padBottom(5f)
        return table
    }
}