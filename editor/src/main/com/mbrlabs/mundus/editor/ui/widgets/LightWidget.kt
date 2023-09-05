package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.env.lights.LightType
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.commons.utils.LightUtils
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import net.mgsx.gltf.scene3d.lights.PointLightEx
import net.mgsx.gltf.scene3d.lights.SpotLightEx

class LightWidget(val lightComponent: LightComponent) : BaseWidget() {

    private val spotLightWrapper = VisTable()
    private val spotlightCheckbox = VisCheckBox(null)
    private val colorPickerField = ColorPickerField()
    private val diffuseIntensityField = VisTextField()

    private val coneSlider = ImprovedSlider(1f, 90f, 1.0f)

    private val linearField = VisTextField()
    private val exponentialField = VisTextField()

    init {
        spotlightCheckbox.isChecked = lightComponent.lightType == LightType.SPOT_LIGHT

        align(Align.topLeft)
        setupWidgets()
        setFieldsToCurrentValues()

        // For display info only, distance is set using selectBox
        linearField.isDisabled = true
        exponentialField.isDisabled = true
    }

    private fun setupWidgets() {
        defaults().padBottom(5f)

        addBaseSettings()
        addSpotLightSection()

        spotlightCheckbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                lightComponent.toggleSpotLight(spotlightCheckbox.isChecked)
                setFieldsToCurrentValues()
            }
        })

        diffuseIntensityField.textFieldFilter = FloatDigitsOnlyFilter(false)
        diffuseIntensityField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (diffuseIntensityField.isInputValid && !diffuseIntensityField.isEmpty) {
                    try {
                        if (lightComponent.light is SpotLightEx)
                            (lightComponent.light as SpotLightEx).intensity = diffuseIntensityField.text.toFloat()
                        else if (lightComponent.light is PointLightEx)
                            (lightComponent.light as PointLightEx).intensity = diffuseIntensityField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + diffuseIntensityField.name))
                    }
                }
            }
        })

        // color field listener
        colorPickerField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                lightComponent.light.color.set(newColor)
            }

            override fun changed(newColor: Color?) {
                lightComponent.light.color.set(newColor)
            }

            override fun canceled(oldColor: Color?) {
                lightComponent.light.color.set(oldColor)
            }
        }

        coneSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (lightComponent.light is SpotLightEx) {
                    //TODO new parameters needed inner vs outer angle
                    (lightComponent.light as SpotLightEx).setConeDeg(coneSlider.value, 10f)
                }
            }
        })

    }

    private fun addBaseSettings() {
        addSectionHeader("Base Settings")
        val baseSection = getSectionTable()

        val table = VisTable()
        table.add(VisLabel("Spot Light: ")).left()
        table.add(spotlightCheckbox).left()
        baseSection.add(table).left().colspan(2).row()

        baseSection.add(VisLabel("Color:")).growX().row()
        baseSection.add(colorPickerField).growX().row()

        baseSection.add(VisLabel("Intensity:")).growX().row()
        baseSection.add(diffuseIntensityField).growX().row()

        add(baseSection).grow().row()
    }

    private fun addSpotLightSection() {
        addSectionHeader("SpotLight Settings", spotLightWrapper)
        val spotlightSection = getSectionTable()

        // Light sliders
        val directionTable = VisTable()
        directionTable.defaults().padBottom(5f).padRight(6f)
        directionTable.add(VisLabel("Cone")).left().padRight(2f)
        directionTable.add(coneSlider).row()
        spotlightSection.add(directionTable).colspan(2).left().row()

        spotLightWrapper.add(spotlightSection)
        add(spotLightWrapper).growX().row()
    }

    private fun setFieldsToCurrentValues() {
        colorPickerField.selectedColor = lightComponent.light.color
        if (lightComponent.light is SpotLightEx) {
            val spotLight = lightComponent.light as SpotLightEx
            exponentialField.text = spotLight.exponent.toString()
            diffuseIntensityField.text = spotLight.intensity.toString()
        } else if (lightComponent.light is PointLightEx) {
            val pointLight = lightComponent.light as PointLightEx
            diffuseIntensityField.text = pointLight.intensity.toString()
        }

        // Disable spotlight check box if the corresponding light counts are maxed out.
        val env: Environment = lightComponent.gameObject.sceneGraph.scene.environment
        if (!spotlightCheckbox.isChecked && !LightUtils.canCreateLight(env, LightType.SPOT_LIGHT)) {
            spotlightCheckbox.isDisabled = true
        } else if (spotlightCheckbox.isChecked && !LightUtils.canCreateLight(env, LightType.POINT_LIGHT)) {
            spotlightCheckbox.isDisabled = true
        }

        if (lightComponent.light is SpotLightEx) {
            coneSlider.value = (lightComponent.light as SpotLightEx).cutoffAngle
            spotLightWrapper.isVisible = true
        } else {
            spotLightWrapper.isVisible = false
        }
    }
}