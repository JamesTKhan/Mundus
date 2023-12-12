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
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner

class SpinnerNoneFixedRandomWidget : AbstractNoneFixedRandomWidget() {

    companion object {
        private const val STEP = 0.1f
        private const val MIN = 0f + STEP
        private const val INIT_VALUE = MIN
        private const val MAX = Int.MAX_VALUE.toFloat()
        private const val PRECISION = 1
    }

    private var fixedValue = MIN
    private var randomMinValue = MIN
    private var randomMaxValue = MIN

    override fun createFixedWidget(): Actor {
        val spinnerModel = SimpleFloatSpinnerModel(INIT_VALUE, MIN, MAX, STEP, PRECISION)
        val spinner = Spinner("", spinnerModel)
        spinner.isProgrammaticChangeEvents = false

        val main = VisTable()
        main.add(spinner).expandX().left()

        setupFixedListener(spinner)

        return main
    }

    override fun createRandomWidget(): Actor {
        val minSpinner = Spinner("Min:", SimpleFloatSpinnerModel(INIT_VALUE, MIN, MAX, STEP, PRECISION))
        val maxSpinner = Spinner("Max:", SimpleFloatSpinnerModel(INIT_VALUE + STEP, MIN, MAX, STEP, PRECISION))
        minSpinner.isProgrammaticChangeEvents = false
        maxSpinner.isProgrammaticChangeEvents = false

        val main = VisTable()
        main.add(minSpinner).expandX().left().row()
        main.add(maxSpinner).expandX().left()

        setupRandomListeners(minSpinner, maxSpinner)

        return main
    }

    override fun typeChanged(type: NoneFixedRandomType) {
        when (type) {
            NoneFixedRandomType.NONE -> listener?.changed(NoneFixedRandomType.NONE, -1f, -1f)
            NoneFixedRandomType.FIXED -> listener?.changed(NoneFixedRandomType.FIXED, fixedValue, fixedValue)
            NoneFixedRandomType.RANDOM -> listener?.changed(NoneFixedRandomType.RANDOM, randomMinValue, randomMaxValue)
        }
    }

    private fun setupFixedListener(spinner: Spinner) {
        val model = spinner.model as SimpleFloatSpinnerModel

        spinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                fixedValue = model.value
                listener?.changed(NoneFixedRandomType.FIXED, fixedValue, fixedValue)
            }
        })
    }

    private fun setupRandomListeners(minSpinner: Spinner, maxSpinner: Spinner) {
        val minModel = minSpinner.model as SimpleFloatSpinnerModel
        val maxModel = maxSpinner.model as SimpleFloatSpinnerModel

        minSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                randomMinValue = minModel.value

                if (randomMaxValue <= randomMinValue) {
                    randomMinValue = randomMaxValue - STEP
                    minModel.value = randomMinValue
                }

                listener?.changed(NoneFixedRandomType.RANDOM, randomMinValue, randomMaxValue)
            }
        })

        maxSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                randomMaxValue = maxModel.value

                if (randomMaxValue <= randomMinValue) {
                    randomMaxValue = randomMinValue + STEP
                    maxModel.value = randomMaxValue
                }

                listener?.changed(NoneFixedRandomType.RANDOM, randomMinValue, randomMaxValue)
            }
        })
    }

}
