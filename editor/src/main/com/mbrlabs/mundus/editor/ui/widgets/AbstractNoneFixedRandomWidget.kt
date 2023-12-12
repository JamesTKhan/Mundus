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
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisTable

abstract class AbstractNoneFixedRandomWidget : VisTable() {

    enum class NoneFixedRandomType {
        NONE, FIXED, RANDOM
    }

    private val noneButton = VisRadioButton("None")
    private val fixedButton = VisRadioButton("Fixed")
    private val randomButton = VisRadioButton("Random")

    private val mainWidget = VisTable()

    private val fixedWidget by lazy { createFixedWidget() }
    private val randomWidget by lazy { createRandomWidget() }

    var listener: NoneFixedRandomListener? = null

    init {
        initUI()
        setupListeners()
    }

    abstract fun createFixedWidget(): Actor

    abstract fun createRandomWidget(): Actor

    protected open fun typeChanged(type: NoneFixedRandomType) {
        // NOOP
    }

    private fun initUI() {
        val radioButtonGroup = VisTable()
        radioButtonGroup.add(noneButton).expand().align(Align.center)
        radioButtonGroup.add(fixedButton).expand().align(Align.center)
        radioButtonGroup.add(randomButton).expand().align(Align.center)

        noneButton.isChecked = true

        add(radioButtonGroup).expandX().fillX().row()

        add(mainWidget).expandX().fillX()
    }

    private fun setupListeners() {
        noneButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (noneButton.isChecked) {
                    fixedButton.isChecked = false
                    randomButton.isChecked = false

                    mainWidget.clearChildren()
                    typeChanged(NoneFixedRandomType.NONE)
                } else {
                    noneButton.isChecked = true
                }
            }
        })
        fixedButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (fixedButton.isChecked) {
                    noneButton.isChecked = false
                    randomButton.isChecked = false

                    mainWidget.clearChildren()
                    mainWidget.add(fixedWidget).expandX().fillX()
                    typeChanged(NoneFixedRandomType.FIXED)
                } else {
                    fixedButton.isChecked = true
                }
            }
        })
        randomButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (randomButton.isChecked) {
                    noneButton.isChecked = false
                    fixedButton.isChecked = false

                    mainWidget.clearChildren()
                    mainWidget.add(randomWidget).expandX().fillX()
                    typeChanged(NoneFixedRandomType.RANDOM)
                } else {
                    randomButton.isChecked = true
                }
            }
        })
    }

    interface NoneFixedRandomListener {
        fun changed(min: Float, max: Float)
    }

}
