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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.CustomPropertiesComponent
import com.mbrlabs.mundus.editor.ui.widgets.FaTextButton
import com.mbrlabs.mundus.editor.utils.Fa

class CustomPropertiesWidget(customPropertiesComponent: CustomPropertiesComponent)
    : ComponentWidget<CustomPropertiesComponent>("Custom Properties Component", customPropertiesComponent) {

    private val customProperties = VisTable()

    init {
        component = customPropertiesComponent

        setupUI()
    }

    override fun setValues(go: GameObject) {
        val c: CustomPropertiesComponent? = go.findComponentByType(Component.Type.CUSTOM_PROPERTIES)
        if (c != null) {
            component = c
        }

        customProperties.clearChildren()

        if (component.map.notEmpty()) {
            addHeader()
        }

        for (entry in component.map) {
            addCustomProperty(entry.key, entry.value)
        }
    }

    private fun setupUI() {
        collapsibleContent.add(customProperties).row()

        val addButton = VisTextButton("Add")
        collapsibleContent.add(addButton)

        addButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val key = ""
                val value = ""

                if (!component.containsKey(key)) {
                    component.put(key, value)

                    if (component.size == 1) {
                        addHeader()
                    }

                    addCustomProperty(key, value)
                }
            }
        })
    }

    private fun addHeader() {
        customProperties.add(VisLabel("Key")).expandX().align(Align.center).padBottom(3f).padRight(3f)
        customProperties.add(VisLabel("Value")).expandX().align(Align.center).padBottom(3f).padRight(3f).row()
    }

    private fun addCustomProperty(key: String, value: String) {
        var previousKey = key

        val keyTextField = VisTextField(key)
        val valueTextField = VisTextField(value)
        val deleteButton = FaTextButton(Fa.TIMES)

        customProperties.add(keyTextField).padBottom(3f).padRight(3f)
        customProperties.add(valueTextField).padBottom(3f).padRight(3f)
        customProperties.add(deleteButton).row()

        keyTextField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val currentKey = keyTextField.text

                if (previousKey == currentKey) {
                    keyTextField.isInputValid = true
                } else {
                    if (component.containsKey(currentKey)) {
                        keyTextField.isInputValid = false
                    } else {
                        keyTextField.isInputValid = true

                        component.remove(previousKey)
                        component.put(currentKey, valueTextField.text)

                        previousKey = currentKey
                    }
                }
            }
        })

        keyTextField.addListener(object : FocusListener() {
            override fun keyboardFocusChanged(event: FocusEvent?, actor: Actor?, focused: Boolean) {
                // If the key value is invalid then change text to the previous valid value
                if (!focused && !keyTextField.isInputValid) {
                    keyTextField.text = previousKey
                    keyTextField.isInputValid = true
                }
            }
        })

        valueTextField.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val currentKey = keyTextField.text
                val currentValue = valueTextField.text

                component.put(currentKey, currentValue)
            }
        })

        deleteButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val currentKey = keyTextField.text

                component.remove(currentKey)
                setValues(component.gameObject)
            }
        })
    }
}
