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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.editor.utils.formatFloat

/**
 * Can be used inside a scroll pane & has the current value editable and displayed in the
 * text box.
 */
class UvSliderWithFloatLabel(labeltext: String, min: Float, max: Float, step: Float) : VisTable() {

    private val currentValue = VisLabel("0")
    private val slider = ScrollPaneSlider(min, max, step, false)
    private var oldValue = 0f
    private var textField: VisTextField = VisTextField()
    private var label: VisLabel = VisLabel()

    init {
        label.setText(labeltext)
        add(label).left().expandX()
        add(slider).expandX().fillX().right()
        textField.textFieldFilter = FloatDigitsOnlyFilter(false)
        add(textField).right().expandX().row()
        textField.text = String.format(formatFloat(value, 6))

        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                textField.text = String.format(formatFloat(value, 6))
                oldValue = slider.value
            }
        })

        textField.addListener(object : FocusListener() {
            override fun keyboardFocusChanged(event: FocusEvent, actor: Actor, focused: Boolean) {
                val floatString = java.lang.Float.parseFloat(textField.text)
                if (textField.text != "" && floatString in min..max) {
                    slider.value = floatString
                }
                else textField.text = oldValue.toString()
            }
        })
    }

    var value: Float
        get() = slider.value
        set(value) {
            slider.value = value
            currentValue.setText(formatFloat(value, 6))
        }

    fun setText(text: String?) {
        if (text != "")
        textField.text = text
    }
}
