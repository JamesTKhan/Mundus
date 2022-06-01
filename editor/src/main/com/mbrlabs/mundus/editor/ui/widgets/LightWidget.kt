package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.env.lights.AttenuationPreset
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType

class LightWidget(val lightComponent: LightComponent) : BaseWidget() {

    private val colorPickerField = ColorPickerField()
    private val diffuseIntensityField = VisTextField()

    private val linearField = VisTextField()
    private val exponentialField = VisTextField()

    private lateinit var selectBox: VisSelectBox<String>

    init {
        align(Align.topLeft)
        setupWidgets()
        setFieldsToCurrentValues()

        // For display info only, distance is set using selectBox
        linearField.isDisabled = true
        exponentialField.isDisabled = true
    }

    private fun setupWidgets() {
        defaults().padBottom(5f)

        add(VisLabel("Color:")).growX().row()
        add(colorPickerField).growX().row()

        add(VisLabel("Diffuse Intensity:")).growX().row()
        add(diffuseIntensityField).growX().row()

        addAttenuationSection()

        diffuseIntensityField.textFieldFilter = FloatDigitsOnlyFilter(false)
        diffuseIntensityField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (diffuseIntensityField.isInputValid && !diffuseIntensityField.isEmpty) {
                    try {
                        lightComponent.pointLight.intensity = diffuseIntensityField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + diffuseIntensityField.name))
                    }
                }
            }
        })

        selectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val att = AttenuationPreset.valueFromString(selectBox.selected)
                lightComponent.pointLight.attenuation = att.attenuation
                setFieldsToCurrentValues()
            }
        })

        // color field listener
        colorPickerField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                lightComponent.pointLight.color.set(newColor)
            }

            override fun changed(newColor: Color?) {
                lightComponent.pointLight.color.set(newColor)
            }

            override fun canceled(oldColor: Color?) {
                lightComponent.pointLight.color.set(oldColor)
            }
        }

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

    private fun setFieldsToCurrentValues() {
        colorPickerField.color = lightComponent.pointLight.color
        diffuseIntensityField.text = lightComponent.pointLight.intensity.toString()
        linearField.text = lightComponent.pointLight.attenuation.linear.toString()
        exponentialField.text = lightComponent.pointLight.attenuation.exponential.toString()
        selectBox.selected = AttenuationPreset.valueFromAttenuation(lightComponent.pointLight.attenuation)
    }
}