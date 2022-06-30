package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.widgets.ColorPickerField
import com.mbrlabs.mundus.editor.ui.widgets.ImprovedSlider
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx

/**
 * @author James Pooley
 * *
 * @version 25-05-2022
 */
class DirectionalLightsDialog : BaseDialog("Directional Light"), ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener {

    private val intensity = VisTextField("0")
    private val colorPickerField = ColorPickerField()

    private val dirXSlider = ImprovedSlider(-1.0f, 1.0f, .1f)
    private val dirYSlider = ImprovedSlider(-1.0f, 1.0f, .1f)
    private val dirZSlider = ImprovedSlider(-1.0f, 1.0f, .1f)

    private var defaultBtn = VisTextButton("Reset Defaults")

    private val projectManager: ProjectManager = Mundus.inject()

    init {
        Mundus.registerEventListener(this)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val root = VisTable()
        root.padTop(6f).padRight(6f).padBottom(22f)
        root.defaults().padBottom(5f).padTop(5f)
        add(root)

        root.add(VisLabel("Light Settings")).colspan(2).left().row()
        root.addSeparator().colspan(2).row()

        // Settings
        val settingsTable = VisTable()
        settingsTable.defaults().padBottom(5f).padLeft(6f).padRight(6f)
        settingsTable.add(VisLabel("Intensity: ")).left().padBottom(10f)
        settingsTable.add(intensity).left().padBottom(10f).row()
        settingsTable.add(VisLabel("Color")).growX()
        settingsTable.add(colorPickerField).left().fillX().expandX().colspan(2).row()

        settingsTable.add(VisLabel("Direction")).left()

        // Light direction sliders
        val directionTable = VisTable()
        directionTable.defaults().padBottom(5f).padLeft(6f).padRight(6f)
        directionTable.add(VisLabel("X:")).left().padRight(2f)
        directionTable.add(dirXSlider).row()
        directionTable.add(VisLabel("Y:")).left().padRight(2f)
        directionTable.add(dirYSlider).row()
        directionTable.add(VisLabel("Z:")).left().padRight(2f)
        directionTable.add(dirZSlider).row()
        settingsTable.add(directionTable).colspan(2).left().growX().row()

        root.add(settingsTable).row()

        // Options
        root.add(VisLabel("Options")).colspan(2).left().padTop(10f).row()
        root.addSeparator().colspan(2).row()

        val tab = VisTable()
        tab.defaults().padTop(15f).padLeft(6f).padRight(6f).padBottom(15f)
        tab.add(defaultBtn).expandX().fillX()
        root.add(tab).fillX().expandX().row()

        resetValues()
    }

    private fun setupListeners() {

        // intensity
        intensity.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val d = convert(intensity.text)
                if (d != null) {
                    val light = getDirectionalLight()
                    light?.intensity = d
                    getDirectionalPBRLight()?.intensity = d
                }
            }
        })

        // color
        colorPickerField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                val light = getDirectionalLight()
                light?.color?.set(newColor)
                getDirectionalPBRLight()?.setColor(newColor)
            }

            override fun changed(newColor: Color?) {
                val light = getDirectionalLight()
                light?.color?.set(newColor)
                getDirectionalPBRLight()?.setColor(newColor)
            }

            override fun canceled(oldColor: Color?) {
                val light = getDirectionalLight()
                light?.color?.set(oldColor)
                getDirectionalPBRLight()?.setColor(oldColor)
            }
        }

        dirXSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                getDirectionalLight()?.direction?.x = dirXSlider.value
                getDirectionalPBRLight()?.direction?.x = dirXSlider.value
            }
        })

        dirYSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                getDirectionalLight()?.direction?.y = dirYSlider.value
                getDirectionalPBRLight()?.direction?.y = dirYSlider.value

            }
        })

        dirZSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                getDirectionalLight()?.direction?.z = dirZSlider.value
                getDirectionalPBRLight()?.direction?.z = dirZSlider.value
            }
        })

        defaultBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val light = getDirectionalLight()
                light?.color?.set(DirectionalLight.DEFAULT_COLOR)
                light?.intensity = DirectionalLight.DEFAULT_INTENSITY
                light?.direction?.set(DirectionalLight.DEFAULT_DIRECTION)

                resetValues()
            }
        })

    }

    private fun resetValues() {
        val light = getDirectionalLight()
        intensity.text = light!!.intensity.toString()
        colorPickerField.selectedColor = light.color

        dirXSlider.value = light.direction!!.x
        dirYSlider.value = light.direction!!.y
        dirZSlider.value = light.direction!!.z
    }

    private fun convert(input: String): Float? {
        try {
            if (input.isEmpty()) return null
            return java.lang.Float.valueOf(input)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Return the first Directional Light. Currently only one Directional Light is supported.
     */
    private fun getDirectionalLight() : DirectionalLight? {
        val dirLightAttribs: DirectionalLightsAttribute = projectManager.current().currScene.environment.get(DirectionalLightsAttribute::class.java,
                DirectionalLightsAttribute.Type)
        val dirLights = dirLightAttribs.lights
        if (dirLights != null && dirLights.size > 0) {
            return dirLights.first()
        }
        return null
    }

    private fun getDirectionalPBRLight() : DirectionalLightEx? {
        var dla = projectManager.current().currScene.environmentpbr.get(com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute::class.java,
            com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute.Type)

        if (dla != null) {
            for (light in dla.lights) {
                if (light is DirectionalLightEx) {
                    return light
                }
            }
        }
        return null
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        resetValues()
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        resetValues()
    }

}