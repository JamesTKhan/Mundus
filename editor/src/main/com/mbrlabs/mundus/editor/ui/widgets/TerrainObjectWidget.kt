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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.mbrlabs.mundus.editor.tools.terrain.ObjectTool

class TerrainObjectWidget : VisTable() {

    companion object {
        const val ADDING_ACTION_LEFT_RIGHT_PADDING = 20f
    }

    private val addingButton = VisRadioButton("Adding")
    private val removingButton = VisRadioButton("Removing")
    private val actionWidget = VisTable()

    private val addingActionWidget = VisTable()
    private val strengthSlider = ImprovedSlider(0.01f, 1f, 0.01f)
    private val randomBetweenVerticesSlider = ImprovedSlider(0f, 0.99f, 0.01f)
    private val minSpaceBetweenObjectsSpinner = Spinner("", SimpleFloatSpinnerModel(0.1f, 0.1f, Float.MAX_VALUE))

    private val removingActionWidget = VisTable()

    init {
        ObjectTool.reset()
        initUI()
        setupListeners()
    }

    private fun initUI() {
        val radioButtonTable = VisTable()
        radioButtonTable.add(addingButton).expand().align(Align.center)
        radioButtonTable.add(removingButton).expand().align(Align.center)
        add(radioButtonTable).expandX().fillX().row()
        addingButton.isChecked = true

        setupAddingActionWidget()
        actionWidget.add(addingActionWidget).expand().fillX()

        add(actionWidget).expand().fill().row()
    }

    private fun setupListeners() {
        addingButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (addingButton.isChecked) {
                    removingButton.isChecked = false
                    actionWidget.clear()
                    actionWidget.add(addingActionWidget).expandX().fillX()
                    ObjectTool.action = ObjectTool.Action.ADDING
                } else {
                    addingButton.isChecked = true
                }
            }
        })

        removingButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (removingButton.isChecked) {
                    addingButton.isChecked = false
                    actionWidget.clear()
                    actionWidget.add(removingActionWidget).expandX().fillX()
                    ObjectTool.action = ObjectTool.Action.REMOVING
                } else {
                    removingButton.isChecked = true
                }
            }
        })

        strengthSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                ObjectTool.strength = strengthSlider.value
            }
        })

        randomBetweenVerticesSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                ObjectTool.randomBetweenVertices = randomBetweenVerticesSlider.value
            }
        })

        minSpaceBetweenObjectsSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                ObjectTool.minSpaceBetweenObjects = (minSpaceBetweenObjectsSpinner.model as SimpleFloatSpinnerModel).value
            }
        })
    }

    private fun setupAddingActionWidget() {
        // Strength slider
        addingActionWidget.add(VisLabel("Strength")).left().row()
        strengthSlider.value = ObjectTool.strength
        addingActionWidget.add(strengthSlider).expandX().fillX().row()

        // Random between vertices
        val randomBetweenVerticesDescription = "If the value is 0 then the objects will be on the vertices\notherwise the objects will be randomized between vertices with this rate."
        val randomBetweenVerticesLabel = ToolTipLabel("Random between vertices", randomBetweenVerticesDescription)
        addingActionWidget.add(randomBetweenVerticesLabel).left().row()
        randomBetweenVerticesSlider.value = ObjectTool.randomBetweenVertices
        addingActionWidget.add(randomBetweenVerticesSlider).expandX().fill().row()

        // Space between objects
        val minSpaceBetweenObjectsDescription = "The minimum space between center of two terrain objects."
        val minSpaceBetweenObjectsLabel = ToolTipLabel("Minimum space between objects", minSpaceBetweenObjectsDescription)
        addingActionWidget.add(minSpaceBetweenObjectsLabel).left().row()
        (minSpaceBetweenObjectsSpinner.model as SimpleFloatSpinnerModel).value = ObjectTool.minSpaceBetweenObjects
        addingActionWidget.add(minSpaceBetweenObjectsSpinner).expandX().left().row()

        // Rotation
        addingActionWidget.add(VisLabel("Rotation")).expandX().fillX().left().row()

        // Rotation X
        val rotationX = VisTable()
        rotationX.add(VisLabel("X")).expandX().fillX().left().row()
        val xSliderAndSpinnerWidget = SliderAndSpinnerNoneFixedRandomWidget()
        xSliderAndSpinnerWidget.listener = NoneFixedRandomListenerImpl { min: Float, max: Float -> ObjectTool.xRotationMin = min; ObjectTool.xRotationMax = max }
        rotationX.add(xSliderAndSpinnerWidget).expandX().fillX().row()
        addingActionWidget.add(rotationX).padLeft(ADDING_ACTION_LEFT_RIGHT_PADDING).padRight(ADDING_ACTION_LEFT_RIGHT_PADDING).expandX().fillX().row()

        // Rotation Y
        val rotationY = VisTable()
        rotationY.add(VisLabel("Y")).expandX().fillX().left().row()
        val ySliderAndSpinnerWidget = SliderAndSpinnerNoneFixedRandomWidget()
        ySliderAndSpinnerWidget.listener = NoneFixedRandomListenerImpl { min: Float, max: Float -> ObjectTool.yRotationMin = min; ObjectTool.yRotationMax = max }
        rotationY.add(ySliderAndSpinnerWidget).expandX().fillX().row()
        addingActionWidget.add(rotationY).padLeft(ADDING_ACTION_LEFT_RIGHT_PADDING).padRight(ADDING_ACTION_LEFT_RIGHT_PADDING).expandX().fillX().row()


        // Rotation Z
        val rotationZ = VisTable()
        rotationZ.add(VisLabel("Z")).expandX().fillX().left().row()
        val zSliderAndSpinnerWidget = SliderAndSpinnerNoneFixedRandomWidget()
        zSliderAndSpinnerWidget.listener = NoneFixedRandomListenerImpl { min: Float, max: Float -> ObjectTool.zRotationMin = min; ObjectTool.zRotationMax = max }
        rotationZ.add(zSliderAndSpinnerWidget).expandX().fillX().row()
        addingActionWidget.add(rotationZ).padLeft(ADDING_ACTION_LEFT_RIGHT_PADDING).padRight(ADDING_ACTION_LEFT_RIGHT_PADDING).expandX().fillX().row()

        // Scaling
        addingActionWidget.add(VisLabel("Scaling")).expandX().fillX().left().row()

        // Scaling X
        val scalingX = VisTable()
        scalingX.add(VisLabel("X")).expandX().fillX().left().row()
        val xSpinnerWidget = SpinnerNoneFixedRandomWidget()
        xSpinnerWidget.listener = NoneFixedRandomListenerImpl { min: Float, max: Float -> ObjectTool.xScaleMin = min; ObjectTool.xScaleMax = max }
        scalingX.add(xSpinnerWidget).expandX().fillX().row()
        addingActionWidget.add(scalingX).padLeft(ADDING_ACTION_LEFT_RIGHT_PADDING).padRight(ADDING_ACTION_LEFT_RIGHT_PADDING).expandX().fillX().row()

        // Scaling Y
        val scalingY = VisTable()
        scalingY.add(VisLabel("Y")).expandX().fillX().left().row()
        val ySpinnerWidget = SpinnerNoneFixedRandomWidget()
        ySpinnerWidget.listener = NoneFixedRandomListenerImpl { min: Float, max: Float -> ObjectTool.yScaleMin = min; ObjectTool.yScaleMax = max }
        scalingY.add(ySpinnerWidget).expandX().fillX().row()
        addingActionWidget.add(scalingY).padLeft(ADDING_ACTION_LEFT_RIGHT_PADDING).padRight(ADDING_ACTION_LEFT_RIGHT_PADDING).expandX().fillX().row()

        // Scaling Z
        val scalingZ = VisTable()
        scalingZ.add(VisLabel("Z")).expandX().fillX().left().row()
        val zSpinnerWidget = SpinnerNoneFixedRandomWidget()
        zSpinnerWidget.listener = NoneFixedRandomListenerImpl { min: Float, max: Float -> ObjectTool.zScaleMin = min; ObjectTool.zScaleMax = max }
        scalingZ.add(zSpinnerWidget).expandX().fillX().row()
        addingActionWidget.add(scalingZ).padLeft(ADDING_ACTION_LEFT_RIGHT_PADDING).padRight(ADDING_ACTION_LEFT_RIGHT_PADDING).expandX().fillX().row()

    }

    inner class NoneFixedRandomListenerImpl(private val setter: (min: Float, max: Float) -> Unit) : AbstractNoneFixedRandomWidget.NoneFixedRandomListener {
        override fun changed(min: Float, max: Float) {
            setter(min, max)
        }
    }
}
