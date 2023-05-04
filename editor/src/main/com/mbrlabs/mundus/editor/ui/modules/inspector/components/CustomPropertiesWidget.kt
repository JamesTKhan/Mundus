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
        val c = go.findComponentByType(Component.Type.CUSTOM_PROPERTIES)
        if (c != null) {
            component = c as CustomPropertiesComponent
        }

        customProperties.clearChildren()

        if (component.customProperties.notEmpty()) {
            addHeader()
        }

        for (entry in component.customProperties) {
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

                if (!component.customProperties.containsKey(key)) {
                    component.customProperties.put(key, value)

                    if (component.customProperties.size == 1) {
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

                val customProperties = component.customProperties

                if (previousKey == currentKey) {
                    keyTextField.isInputValid = true
                } else {
                    if (customProperties.containsKey(currentKey)) {
                        keyTextField.isInputValid = false
                    } else {
                        keyTextField.isInputValid = true

                        customProperties.remove(previousKey)
                        customProperties.put(currentKey, valueTextField.text)

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

                val customProperties = component.customProperties

                customProperties.put(currentKey, currentValue)
            }
        })

        deleteButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val currentKey = keyTextField.text

                component.customProperties.remove(currentKey)
                setValues(component.gameObject)
            }
        })
    }
}
