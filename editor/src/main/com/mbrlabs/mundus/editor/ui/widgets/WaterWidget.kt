package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent
import com.mbrlabs.mundus.commons.water.Water
import com.mbrlabs.mundus.commons.water.WaterResolution
import com.mbrlabs.mundus.commons.water.attributes.WaterFloatAttribute
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType


class WaterWidget(val waterComponent: WaterComponent) : VisTable() {

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

    private lateinit var selectBox: VisSelectBox<String>

    private val resetDefaults = VisTextButton("Reset Defaults")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        align(Align.topLeft)
        setupWidgets()
    }

    private fun setupWidgets() {
        defaults().padBottom(10f)

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
                projectManager.current().currScene.settings.waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION
                projectManager.current().assetManager.addModifiedAsset(waterComponent.waterAsset)

                setFieldsToCurrentValues()
            }
        })

        setFieldsToCurrentValues()

    }

    private fun registerFloatFieldListener(floatField: VisTextField, attributeType: Long) {
        floatField.textFieldFilter = FloatDigitsOnlyFilter(false)
        floatField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (floatField.isInputValid && !floatField.isEmpty) {
                    try {
                        waterComponent.waterAsset.water.setFloatAttribute(attributeType, floatField.text.toFloat())
                        projectManager.current().assetManager.addModifiedAsset(waterComponent.waterAsset)
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + floatField.name))
                    }
                }
            }
        })
    }

    fun setFieldsToCurrentValues() {
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

        if (!selectBox.items.contains(projectManager.current().currScene.settings.waterResolution.value)) {
            selectBox.selected = WaterResolution.DEFAULT_WATER_RESOLUTION.value
        } else {
            selectBox.selected = projectManager.current().currScene.settings.waterResolution.value
        }

    }

    private fun getSectionTable(): VisTable {
        val table = VisTable()
        table.defaults().padLeft(0f).padBottom(5f)
        return table
    }
}