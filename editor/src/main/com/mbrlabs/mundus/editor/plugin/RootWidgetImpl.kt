package com.mbrlabs.mundus.editor.plugin

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.mbrlabs.mundus.pluginapi.ui.CheckboxListener
import com.mbrlabs.mundus.pluginapi.ui.RadioButtonListener
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.SpinnerListener

class RootWidgetImpl : VisTable(), RootWidget {

    override fun addRadioButtons(button1Text: String, button2Text: String, listener: RadioButtonListener) {
        var selectedButtonText = button1Text

        val radioButton1 = VisRadioButton(button1Text)
        radioButton1.isChecked = true

        val radioButton2 = VisRadioButton(button2Text)

        radioButton1.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (selectedButtonText != button1Text) {
                    listener.selected(button1Text)
                    selectedButtonText = button1Text
                    radioButton2.isChecked = false
                } else {
                    radioButton1.isChecked = true
                }
            }
        })

        radioButton2.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (selectedButtonText != button2Text) {
                    listener.selected(button2Text)
                    selectedButtonText = button2Text
                    radioButton1.isChecked = false
                } else {
                    radioButton2.isChecked = true
                }
            }
        })

        add(radioButton1)
        add(radioButton2)
    }

    override fun addSpinner(text: String, min: Int, max: Int, initValue: Int, listener: SpinnerListener) {
        val spinnerModel = IntSpinnerModel(initValue, min, max)
        val spinner = Spinner(text, spinnerModel)
        spinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                listener.changed(spinnerModel.value)
            }
        })

        add(spinner)
    }

    override fun addCheckbox(text: String, listener: CheckboxListener) {
        val checkbox = VisCheckBox(text)
        checkbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                listener.changed(checkbox.isChecked)
            }
        })

        add(checkbox)
    }

    override fun addRow() {
        row()
    }
}
