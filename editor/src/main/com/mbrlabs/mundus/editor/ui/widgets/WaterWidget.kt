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
import com.mbrlabs.mundus.commons.water.WaterResolution
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType


class WaterWidget(val waterComponent: WaterComponent) : VisTable() {

    private val tilingField = VisTextField()
    private val waveStrength = VisTextField()
    private val waveSpeed = VisTextField()
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

        val table = VisTable()
        add(table).grow().row()
        tilingField.setSize(0f,50f)
        tilingField.layout()

        add(VisLabel("Tiling:")).growX().row()
        add(tilingField).growX().row()

        add(VisLabel("Wave Strength:")).growX().row()
        add(waveStrength).growX().row()

        add(VisLabel("Wave Speed:")).growX().row()
        add(waveSpeed).growX().row()

        add(VisLabel("Reflectivity:")).growX().row()
        add(reflectivity).growX().row()

        add(VisLabel("Shine Damper:")).growX().row()
        add(shineDamper).growX().row()

        val selectorsTable = VisTable(true)
        selectBox = VisSelectBox<String>()
        selectBox.setItems(
                WaterResolution._1024.value,
                WaterResolution._1280.value,
                WaterResolution._1600.value,
                WaterResolution._1920.value
        )
        selectorsTable.add(selectBox).left()

        add(VisLabel("Texture resolution (Global per scene):")).growX().row()
        add(selectorsTable).left().row()

        add(resetDefaults).padTop(10f).growX().row()

        // tiling
        tilingField.textFieldFilter = FloatDigitsOnlyFilter(false)
        tilingField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (tilingField.isInputValid && !tilingField.isEmpty) {
                    try {
                        waterComponent.waterAsset.water.tiling = tilingField.text.toFloat()
                        projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing water tiling"))
                    }
                }
            }
        })

        // wave strength
        waveStrength.textFieldFilter = FloatDigitsOnlyFilter(false)
        waveStrength.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (waveStrength.isInputValid && !waveStrength.isEmpty) {
                    try {
                        waterComponent.waterAsset.water.waveStrength = waveStrength.text.toFloat()
                        projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing water wave strength"))
                    }
                }
            }
        })

        // wave speed
        waveSpeed.textFieldFilter = FloatDigitsOnlyFilter(false)
        waveSpeed.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (waveSpeed.isInputValid && !waveSpeed.isEmpty) {
                    try {
                        waterComponent.waterAsset.water.waveSpeed = waveSpeed.text.toFloat()
                        projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing water wave strength"))
                    }
                }
            }
        })

        // reflectivity
        reflectivity.textFieldFilter = FloatDigitsOnlyFilter(false)
        reflectivity.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (reflectivity.isInputValid && !reflectivity.isEmpty) {
                    try {
                        waterComponent.waterAsset.water.reflectivity = reflectivity.text.toFloat()
                        projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing water reflectivity"))
                    }
                }
            }
        })

        // shine damper
        shineDamper.textFieldFilter = FloatDigitsOnlyFilter(false)
        shineDamper.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (shineDamper.isInputValid && !shineDamper.isEmpty) {
                    try {
                        waterComponent.waterAsset.water.shineDamper = shineDamper.text.toFloat()
                        projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing water shine damper"))
                    }
                }
            }
        })

        // resolution
        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val res = WaterResolution.valueFromString(selectBox.selected)
                projectManager.current().currScene.setWaterResolution(res)
            }
        })

        resetDefaults.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                waterComponent.waterAsset.water.tiling = Water.DEFAULT_TILING
                waterComponent.waterAsset.water.waveStrength = Water.DEFAULT_WAVE_STRENGTH
                waterComponent.waterAsset.water.waveSpeed = Water.DEFAULT_WAVE_SPEED
                waterComponent.waterAsset.water.reflectivity = Water.DEFAULT_REFLECTIVITY
                waterComponent.waterAsset.water.shineDamper = Water.DEFAULT_SHINE_DAMPER
                projectManager.current().currScene.waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION
                projectManager.current().assetManager.addDirtyAsset(waterComponent.waterAsset)

                setFieldsToCurrentValues()
            }
        })

        setFieldsToCurrentValues()

    }

    fun setFieldsToCurrentValues() {
        tilingField.text = waterComponent.waterAsset.water.tiling.toString()
        waveStrength.text = waterComponent.waterAsset.water.waveStrength.toString()
        waveSpeed.text = waterComponent.waterAsset.water.waveSpeed.toString()
        reflectivity.text = waterComponent.waterAsset.water.reflectivity.toString()
        shineDamper.text = waterComponent.waterAsset.water.shineDamper.toString()
        selectBox.selected = projectManager.current().currScene.waterResolution.value
    }
}