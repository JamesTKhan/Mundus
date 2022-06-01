package com.mbrlabs.mundus.editor.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType

class LightWidget(val lightComponent: LightComponent) : VisTable() {

    private val colorPickerField = ColorPickerField()
    private val diffuseIntensityField = VisTextField()

    private val linearField = VisTextField()
    private val exponentialField = VisTextField()

    init {
        align(Align.topLeft)
        setFieldsToCurrentValues()
        setupWidgets()
    }

    private fun setupWidgets() {
        defaults().padBottom(5f)

        add(VisLabel("Color:")).growX().row()
        add(colorPickerField).growX().row()

        add(VisLabel("Diffuse Intensity:")).growX().row()
        add(diffuseIntensityField).growX().row()

        add(VisLabel("Linear:")).growX().row()
        add(linearField).growX().row()

        add(VisLabel("Exponential:")).growX().row()
        add(exponentialField).growX().row()

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

        linearField.textFieldFilter = FloatDigitsOnlyFilter(false)
        linearField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (linearField.isInputValid && !linearField.isEmpty) {
                    try {
                        lightComponent.pointLight.attenuation.linear = linearField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + linearField.name))
                    }
                }
            }
        })

        exponentialField.textFieldFilter = FloatDigitsOnlyFilter(false)
        exponentialField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if (exponentialField.isInputValid && !exponentialField.isEmpty) {
                    try {
                        lightComponent.pointLight.attenuation.exponential = exponentialField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + exponentialField.name))
                    }
                }
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

    private fun setFieldsToCurrentValues() {
        diffuseIntensityField.text = lightComponent.pointLight.intensity.toString()
        linearField.text = lightComponent.pointLight.attenuation.linear.toString()
        exponentialField.text = lightComponent.pointLight.attenuation.exponential.toString()
    }
}