package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.env.lights.AttenuationPreset
import com.mbrlabs.mundus.commons.env.lights.LightType
import com.mbrlabs.mundus.commons.env.lights.SpotLight
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.commons.utils.LightUtils
import com.mbrlabs.mundus.commons.utils.MathUtils
import com.mbrlabs.mundus.commons.utils.ShaderUtils
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import kotlin.math.roundToInt

class LightWidget(val lightComponent: LightComponent) : BaseWidget() {

    private val spotLightWrapper = VisTable()
    private val spotlightCheckbox = VisCheckBox(null)
    private val colorPickerField = ColorPickerField()
    private val diffuseIntensityField = VisTextField()

    private val leftRightSlider = ImprovedSlider(0f, 359f, 1.0f)
    private val upDownSlider = ImprovedSlider(1f, 179f, 1.0f)
    private val coneSlider = ImprovedSlider(1f, 90f, 1.0f)

    private val linearField = VisTextField()
    private val exponentialField = VisTextField()

    private var currentLeftRightValue = 0f
    private var currentUpDownValue = 90f

    private lateinit var selectBox: VisSelectBox<String>

    init {
        upDownSlider.value = 90f
        spotlightCheckbox.isChecked = lightComponent.light.lightType == LightType.SPOT_LIGHT

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
        addAttenuationSection()
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
                        lightComponent.light.intensity = diffuseIntensityField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + diffuseIntensityField.name))
                    }
                }
            }
        })

        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val att = AttenuationPreset.valueFromString(selectBox.selected)
                lightComponent.light.attenuation = att.attenuation
                setFieldsToCurrentValues()
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

        leftRightSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (lightComponent.light is SpotLight) {
                    (lightComponent.light as SpotLight).direction.rotate(Vector3.Y, currentLeftRightValue - leftRightSlider.value)
                    currentLeftRightValue = leftRightSlider.value
                }
            }
        })

        upDownSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (lightComponent.light is SpotLight) {
                    MathUtils.rotateUpDown((lightComponent.light as SpotLight).direction, currentUpDownValue - upDownSlider.value)
                    currentUpDownValue = upDownSlider.value
                }
            }
        })

        coneSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (lightComponent.light is SpotLight) {
                    (lightComponent.light as SpotLight).cutoff = coneSlider.value
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

    private fun addAttenuationSection() {
        addSectionHeader("Attenuation")
        val attenuationSection = getSectionTable()

        val selectorsTable = VisTable(true)
        selectBox = VisSelectBox<String>()

        // Build list for distance values
        val values = Array<String>()
        for (value in AttenuationPreset.values())
            values.add(value.value)

        selectBox.items = values
        selectorsTable.add(selectBox).left()

        attenuationSection.add(VisLabel("Distance:")).growX().row()
        attenuationSection.add(selectorsTable).left().row()

        attenuationSection.add(VisLabel("Linear:")).growX().row()
        attenuationSection.add(linearField).growX().row()

        attenuationSection.add(VisLabel("Exponential:")).growX().row()
        attenuationSection.add(exponentialField).growX()

        add(attenuationSection).grow().row()
    }

    private fun addSpotLightSection() {
        addSectionHeader("SpotLight Settings", spotLightWrapper)
        val spotlightSection = getSectionTable()

        // Light sliders
        val directionTable = VisTable()
        directionTable.defaults().padBottom(5f).padRight(6f)
        directionTable.add(VisLabel("Up/Down:")).left().padRight(2f)
        directionTable.add(upDownSlider).row()
        directionTable.add(VisLabel("Left/Right:")).left().padRight(2f)
        directionTable.add(leftRightSlider).row()
        directionTable.add(VisLabel("Cone")).left().padRight(2f)
        directionTable.add(coneSlider).row()
        spotlightSection.add(directionTable).colspan(2).left().row()

        spotLightWrapper.add(spotlightSection)
        add(spotLightWrapper).growX().row()
    }

    private fun setFieldsToCurrentValues() {
        colorPickerField.color = lightComponent.light.color
        diffuseIntensityField.text = lightComponent.light.intensity.toString()
        linearField.text = lightComponent.light.attenuation.linear.toString()
        exponentialField.text = lightComponent.light.attenuation.exponential.toString()
        selectBox.selected = AttenuationPreset.valueFromAttenuation(lightComponent.light.attenuation)

        // Disable spotlight check box if the corresponding light counts are maxed out.
        val env: Environment = lightComponent.gameObject.sceneGraph.scene.environment
        if (!spotlightCheckbox.isChecked && LightUtils.getSpotLightsCount(env) >= ShaderUtils.MAX_SPOT_LIGHTS) {
            spotlightCheckbox.isDisabled = true
        } else if (spotlightCheckbox.isChecked && LightUtils.getPointLightsCount(env) >= ShaderUtils.MAX_POINT_LIGHTS) {
            spotlightCheckbox.isDisabled = true
        }

        if (lightComponent.light is SpotLight) {
            // Convert direction to up/down angle
            var upDownAngle = MathUtils.getAngleBetween( (lightComponent.light as SpotLight).direction, Vector3.Y)
            upDownAngle = upDownAngle.roundToInt().toFloat()

            currentUpDownValue = upDownAngle
            upDownSlider.value = upDownAngle

            coneSlider.value = (lightComponent.light as SpotLight).cutoff
            spotLightWrapper.isVisible = true
        } else {
            spotLightWrapper.isVisible = false
        }
    }
}