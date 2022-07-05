/*
 * Copyright (c) 2022. See AUTHORS file.
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
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextField
import com.mbrlabs.mundus.commons.physics.enums.PhysicsBody
import com.mbrlabs.mundus.commons.physics.enums.PhysicsShape
import com.mbrlabs.mundus.commons.scene3d.components.RigidBodyPhysicsComponent
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType

/**
 * @author James Pooley
 * @version July 03, 2022
 */
class PhysicsWidget(val physicsComponent: RigidBodyPhysicsComponent) : BaseWidget() {

    private lateinit var shapeTable: VisTable
    private lateinit var selectorsTable: VisTable
    private lateinit var settingsTable: VisTable
    private lateinit var bodySelectBox: VisSelectBox<String>
    private lateinit var shapeSelectBox: VisSelectBox<String>

    // Dynamic Body properties
    private var shapeSelectorLabel = VisLabel("Collision Shape ")

    private var massFieldLabel = ToolTipLabel("Mass ", "Mass of the body, must be positive value greater than 0 for dynamic bodies.\n" +
            "Avoid large mass ratios. It is best to keep the mass around 1")
    private var massField = VisTextField()

    private var frictionLabel = ToolTipLabel("Friction ", "How strongly friction applies to this body.\n" +
            "Friction is calculated by multiplying the friction coefficients from the two colliding objects.\nFor friction to apply, both objects must have a positive friction value. \n" +
            "Zero friction will result in continuous sliding.")
    private var frictionField = VisTextField()

    private var restitutionLabel = ToolTipLabel("Restitution ", "How elastic or bouncy the body is.\n" +
            "Restitution is calculated by multiplying the restitution coefficients from two colliding bodies.\n" +
            "For restitution to apply, both objects must have a positive restitution value.")
    private var restitutionField = VisTextField()

    private var disableDeactivationLabel = ToolTipLabel("Disable Deactivation ", "Prevents body from auto deactivating shortly after it stops moving.")
    private var disableDeactivationCheckbox = VisCheckBox(null)

    init {
        align(Align.topLeft)
        setupWidgets()
    }

    private fun setupWidgets() {
        defaults().padBottom(4f).padRight(4f)
        selectorsTable = VisTable()
        bodySelectBox = VisSelectBox<String>()
        shapeSelectBox = VisSelectBox<String>()
        settingsTable = VisTable()

        // Build list for body type values
        val values = Array<String>()
        for (value in PhysicsBody.values())
            values.add(value.value)

        bodySelectBox.items = values
        selectorsTable.add(bodySelectBox).left()

        buildSettings()
        registerListeners()
        updateValues()
    }

    private fun buildSettings() {
        clearChildren()
        defaults().align(Align.left)

        // Body Selector
        add(ToolTipLabel("Body Type", "Static: Non-moving body. Use for terrains and other static objects.\n\n" +
                "Dynamic: Moving body driven by physics simulation\n\n" +
                "Kinematic: Manually moved, influences other objects but does not receive any physics influence"))
        add(selectorsTable).left().row()

        shapeTable = VisTable()
        // Build list for shape type values
        val shapes = Array<String>()
        shapes.add(PhysicsShape.BOX.value)
        shapes.add(PhysicsShape.SPHERE.value)
        shapes.add(PhysicsShape.CAPSULE.value)
        shapes.add(PhysicsShape.CYLINDER.value)
        shapes.add(PhysicsShape.CONE.value)
        shapes.add(PhysicsShape.CONVEX_HULL.value)
        shapes.add(PhysicsShape.G_IMPACT_TRIANGLE_MESH.value)

        shapeSelectBox.items = shapes
        shapeTable.add(shapeSelectBox).left()

        add(frictionLabel)
        add(frictionField).row()

        add(restitutionLabel)
        add(restitutionField).row()

        if (physicsComponent.physicsBodyType == PhysicsBody.DYNAMIC) {
            addDynamicFields()
        } else {
            removeDynamicFields()
        }
    }

    private fun removeDynamicFields() {
        shapeTable.remove()
        shapeSelectorLabel.remove()

        massField.remove()
        massFieldLabel.remove()

        disableDeactivationCheckbox.remove()
        disableDeactivationLabel.remove()
    }

    private fun addDynamicFields() {
        add(shapeSelectorLabel)
        add(shapeTable).left().row()

        add(massFieldLabel)
        add(massField).row()

        add(disableDeactivationLabel)
        add(disableDeactivationCheckbox).row()
    }

    private fun updateValues() {
        bodySelectBox.selected = physicsComponent.physicsBodyType.value
        shapeSelectBox.selected = physicsComponent.physicsShape.value

        massField.text = physicsComponent.mass.toString()
        frictionField.text = physicsComponent.friction.toString()
        restitutionField.text = physicsComponent.restitution.toString()
        disableDeactivationCheckbox.isChecked = physicsComponent.isDisableDeactivation
    }


    private fun registerListeners() {
        bodySelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val physType = PhysicsBody.valueFromString(bodySelectBox.selected)
                physicsComponent.physicsBodyType = physType
                buildSettings()
            }
        })

        shapeSelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val physType = PhysicsShape.valueFromString(shapeSelectBox.selected)
                physicsComponent.physicsShape = physType
            }
        })

        massField.textFieldFilter = FloatDigitsOnlyFilter(false)
        massField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if(massField.isInputValid && !massField.isEmpty) {
                    try {
                        physicsComponent.mass = massField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + massField.name))
                    }
                }
            }
        })

        frictionField.textFieldFilter = FloatDigitsOnlyFilter(false)
        frictionField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if(frictionField.isInputValid && !frictionField.isEmpty) {
                    try {
                        physicsComponent.friction = frictionField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + frictionField.name))
                    }
                }
            }
        })

        restitutionField.textFieldFilter = FloatDigitsOnlyFilter(false)
        restitutionField.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if(restitutionField.isInputValid && !restitutionField.isEmpty) {
                    try {
                        physicsComponent.restitution = restitutionField.text.toFloat()
                    } catch (ex : NumberFormatException) {
                        Mundus.postEvent(LogEvent(LogType.ERROR,"Error parsing field " + restitutionField.name))
                    }
                }
            }
        })

        disableDeactivationCheckbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                physicsComponent.isDisableDeactivation = disableDeactivationCheckbox.isChecked
            }
        })
    }
}