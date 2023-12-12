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
import com.kotcrab.vis.ui.widget.VisTable

class SliderAndSpinnerNoneFixedRandomWidget : AbstractNoneFixedRandomWidget() {

    companion object {
        private const val STEP = 0.1f
        private const val MIN = 0f + STEP
        private const val MAX = 360f - STEP
        private const val PRECISION = 1
    }

    private var fixedValue = MIN
    private var randomMinValue = MIN
    private var randomMaxValue = MIN

    override fun createFixedWidget(): Actor {
        val sliderWithSpinner = SliderWithSpinnerWidget(MIN, MAX, STEP, PRECISION)
        sliderWithSpinner.listener = SliderWithSpinnerListenerImpl(sliderWithSpinner, NoneFixedRandomType.FIXED)

        val main = VisTable()
        main.add(sliderWithSpinner).fillX().expandX()

        return main
    }

    override fun createRandomWidget(): Actor {
        val minSliderWithSpinner = SliderWithSpinnerWidget(MIN, MAX, STEP, PRECISION)
        minSliderWithSpinner.listener = SliderWithSpinnerListenerImpl(minSliderWithSpinner, NoneFixedRandomType.RANDOM, true)
        val maxSliderWithSpinner = SliderWithSpinnerWidget(MIN, MAX, STEP, PRECISION)
        maxSliderWithSpinner.listener = SliderWithSpinnerListenerImpl(maxSliderWithSpinner, NoneFixedRandomType.RANDOM, false)

        val main = VisTable()
        main.add(minSliderWithSpinner).expandX().fillX().row()
        main.add(maxSliderWithSpinner).expandX().fillX()

        return main
    }

    override fun typeChanged(type: NoneFixedRandomType) {
        when (type) {
            NoneFixedRandomType.NONE -> listener?.changed(NoneFixedRandomType.NONE, -1f, -1f)
            NoneFixedRandomType.FIXED -> listener?.changed(NoneFixedRandomType.FIXED, fixedValue, fixedValue)
            NoneFixedRandomType.RANDOM -> listener?.changed(NoneFixedRandomType.RANDOM, randomMinValue, randomMaxValue)
        }
    }

    inner class SliderWithSpinnerListenerImpl(private val sliderWithSpinner: SliderWithSpinnerWidget,
                                              private val type: NoneFixedRandomType,
                                              private val randomMin: Boolean = false) : SliderWithSpinnerWidget.SliderWithSpinnerListener {
        override fun changed(value: Float) {
            when (type) {
                NoneFixedRandomType.NONE -> {} //NOOP
                NoneFixedRandomType.FIXED -> {
                    fixedValue = value
                    listener?.changed(type, fixedValue, fixedValue)
                }
                NoneFixedRandomType.RANDOM -> {
                    if (randomMin) {
                        if (randomMaxValue <= value) {
                            sliderWithSpinner.setValue(randomMinValue)
                        } else {
                            randomMinValue = value
                        }
                    } else {
                        if (value <= randomMinValue) {
                            sliderWithSpinner.setValue(randomMaxValue)
                        } else {
                            randomMaxValue = value
                        }
                    }
                    listener?.changed(type, randomMinValue, randomMaxValue)
                }
            }
        }
    }

}
