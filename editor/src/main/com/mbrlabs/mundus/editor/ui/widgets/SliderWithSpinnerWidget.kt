/*
 * Copyright (c) 2023. See AUTHORS file.
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

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisSlider
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner

class SliderWithSpinnerWidget(min: Float, max: Float, stepSize: Float, precision: Int) : VisTable() {

    companion object {
        const val PAD = 5f
    }

    private val slider = VisSlider(min, max, stepSize, false)
    private val spinnerModel = SimpleFloatSpinnerModel(min, min, max, stepSize, precision)
    private val spinner = Spinner("", spinnerModel)

    var listener: SliderWithSpinnerListener? = null

    init {
        slider.setProgrammaticChangeEvents(false)
        spinner.isProgrammaticChangeEvents = false

        add(slider).expandX().fillX().padRight(PAD)
        add(spinner)

        setupListeners()
    }

    fun setValue(value: Float) {
        slider.value = value
        spinnerModel.value = value
    }

    private fun setupListeners() {
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val newValue = slider.value
                spinnerModel.value = newValue

                listener?.changed(newValue)
            }
        })

        spinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val newValue = spinnerModel.value
                slider.value = newValue

                listener?.changed(newValue)
            }
        })
    }


    interface SliderWithSpinnerListener {
        fun changed(value: Float)
    }
}
