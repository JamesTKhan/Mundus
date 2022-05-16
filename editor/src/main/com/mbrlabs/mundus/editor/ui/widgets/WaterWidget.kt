package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent
import com.mbrlabs.mundus.commons.water.Water
import com.mbrlabs.mundus.commons.water.WaterFloatAttribute
import com.mbrlabs.mundus.commons.water.WaterResolution
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

    private lateinit var selectBox: VisSelectBox<String>

    private val resetDefaults = VisTextButton("Reset Defaults")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        align(Align.topLeft)
        setupWidgets()
    }

    private fun setupWidgets() {
        defaults().padBottom(5f)

        /* Waves section */
        add(VisLabel("Waves")).left().row()
        addSeparator().padBottom(5f).row()

        val waveSettings = getSectionTable()

        waveSettings.add(VisLabel("Tiling:")).growX().row()
        waveSettings.add(tilingField).growX().row()

        waveSettings.add(VisLabel("Wave Strength:")).growX().row()
        waveSettings.add(waveStrength).growX().row()

        waveSettings.add(VisLabel("Wave Speed:")).growX().row()
        waveSettings.add(waveSpeed).growX().row()

        add(waveSettings).grow().row()

        /* Foam settings */
        add(VisLabel("Foam")).left().row()
        addSeparator().padBottom(5f).row()

        val foamSettings = getSectionTable()

        foamSettings.add(VisLabel("Foam Scale:")).growX().row()
        foamSettings.add(foamPatternScale).growX().row()

        foamSettings.add(VisLabel("Foam Edge Bias:")).growX().row()
        foamSettings.add(foamEdgeBias).growX().row()

        foamSettings.add(VisLabel("Foam Scroll Speed:")).growX().row()
        foamSettings.add(foamScrollSpeedFactor).growX().row()

        foamSettings.add(VisLabel("Foam Fall Off Distance:")).growX().row()
        foamSettings.add(foamFallOffDistance).growX().row()

        foamSettings.add(VisLabel("Foam Edge Fall Off Distance:")).growX().row()
        foamSettings.add(foamEdgeDistance).growX().row()

        add(foamSettings).grow().row()

        /* Lighting section */
        add(VisLabel("Lighting")).left().row()
        addSeparator().padBottom(5f).row()

        val lightingSettings = getSectionTable()

        lightingSettings.add(VisLabel("Reflectivity:")).growX().row()
        lightingSettings.add(reflectivity).growX().row()

        lightingSettings.add(VisLabel("Shine Damper:")).growX().row()
        lightingSettings.add(shineDamper).growX().row()

        add(lightingSettings).grow().row()

        /* Quality section */
        add(VisLabel("Quality")).left().row()
        addSeparator().padBottom(5f).row()

        val qualitySettings = getSectionTable()
        val selectorsTable = VisTable(true)
        selectBox = VisSelectBox<String>()
        selectBox.setItems(
                WaterResolution._1024.value,
                WaterResolution._1280.value,
                WaterResolution._1600.value,
                WaterResolution._1920.value
        )
        selectorsTable.add(selectBox).left()

        qualitySettings.add(VisLabel("Texture resolution (Global per scene):")).growX().row()
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
                projectManager.current().currScene.waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION
                projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)

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
                        projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)
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
        foamEdgeDistance.text = waterComponent.waterAsset.water.getFloatAttribute(WaterFloatAttribute.FoamEdgeBias).toString()
        selectBox.selected = projectManager.current().currScene.waterResolution.value
    }

    private fun getSectionTable(): VisTable {
        val table = VisTable()
        table.defaults().padLeft(10f).padBottom(5f)
        return table
    }
}