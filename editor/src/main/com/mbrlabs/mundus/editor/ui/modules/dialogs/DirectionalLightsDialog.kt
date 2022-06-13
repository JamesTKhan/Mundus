package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight
import com.mbrlabs.mundus.commons.shadows.ShadowResolution
import com.mbrlabs.mundus.commons.utils.LightUtils
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent
import com.mbrlabs.mundus.editor.events.SceneChangedEvent
import com.mbrlabs.mundus.editor.ui.widgets.ColorPickerField
import com.mbrlabs.mundus.editor.ui.widgets.ImprovedSlider

/**
 * @author James Pooley
 * *
 * @version 25-05-2022
 */
class  DirectionalLightsDialog : BaseDialog("Directional Light"), ProjectChangedEvent.ProjectChangedListener,
        SceneChangedEvent.SceneChangedListener {

    private val root = VisTable()

    private val intensity = VisTextField("0")
    private val colorPickerField = ColorPickerField()
    private val castShadows = VisCheckBox(null)
    private lateinit var shadowResSelectBox: VisSelectBox<String>

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

        addShadowSection()

        // Options
        root.add(VisLabel("Options")).colspan(2).left().padTop(10f).row()
        root.addSeparator().colspan(2).row()

        val tab = VisTable()
        tab.defaults().padTop(15f).padLeft(6f).padRight(6f).padBottom(15f)
        tab.add(defaultBtn).expandX().fillX()
        root.add(tab).fillX().expandX().row()

        resetValues()
    }

    private fun addShadowSection() {
        root.add(VisLabel("Shadow Settings")).colspan(2).left().row()
        root.addSeparator().colspan(2).row()

        // Settings
        val shadowSettingsTable = VisTable()
        val enableLabel = VisLabel("Cast Dynamic Shadows: ")
        shadowSettingsTable.defaults().padBottom(5f).padLeft(6f).padRight(6f)
        shadowSettingsTable.add(enableLabel).left().padBottom(10f)
        shadowSettingsTable.add(castShadows).left().padBottom(10f).left().row()

        val resolutionLabel = VisLabel("Shadow Resolution: ")
        val selectorsTable = VisTable(true)
        shadowResSelectBox = VisSelectBox<String>()
        shadowResSelectBox.setItems(
                ShadowResolution._512.value,
                ShadowResolution._1024.value,
                ShadowResolution._2048.value,
                ShadowResolution._4096.value
        )
        selectorsTable.add(shadowResSelectBox)

        shadowSettingsTable.add(resolutionLabel).left().padBottom(10f)
        shadowSettingsTable.add(selectorsTable).padBottom(10f)

        var tip = "Enables shadow mapping."
        Tooltip.Builder(tip).target(enableLabel).build()
        Tooltip.Builder(tip).target(castShadows).build()

        tip = "Sets shadow texture resolution. Higher resolutions look better but uses more resources."
        Tooltip.Builder(tip).target(resolutionLabel).build()
        Tooltip.Builder(tip).target(shadowResSelectBox).build()

        root.add(shadowSettingsTable).left().row()
    }

    private fun setupListeners() {

        // intensity
        intensity.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val d = convert(intensity.text)
                if (d != null) {
                    val light = LightUtils.getDirectionalLight(projectManager.current().currScene.environment)
                    light?.intensity = d
                }
            }
        })

        // color
        colorPickerField.colorAdapter = object: ColorPickerAdapter() {
            override fun finished(newColor: Color) {
                val light = LightUtils.getDirectionalLight(projectManager.current().currScene.environment)
                light?.color?.set(newColor)
            }

            override fun changed(newColor: Color?) {
                val light = LightUtils.getDirectionalLight(projectManager.current().currScene.environment)
                light?.color?.set(newColor)
            }

            override fun canceled(oldColor: Color?) {
                val light = LightUtils.getDirectionalLight(projectManager.current().currScene.environment)
                light?.color?.set(oldColor)
            }
        }

        dirXSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                LightUtils.getDirectionalLight(projectManager.current().currScene.environment)?.direction?.x = dirXSlider.value
            }
        })

        dirYSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                LightUtils.getDirectionalLight(projectManager.current().currScene.environment)?.direction?.y = dirYSlider.value
            }
        })

        dirZSlider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                LightUtils.getDirectionalLight(projectManager.current().currScene.environment).direction?.z = dirZSlider.value
            }
        })

        // Shadows

        castShadows.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val light = LightUtils.getDirectionalLight(projectManager.current().currScene.environment)
                light?.castsShadows = castShadows.isChecked
            }
        })

        // resolution
        shadowResSelectBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val res = ShadowResolution.valueFromString(shadowResSelectBox.selected)
                projectManager.current().currScene.setShadowQuality(res)
            }
        })

        defaultBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val light = LightUtils.getDirectionalLight(projectManager.current().currScene.environment)
                light?.color?.set(DirectionalLight.DEFAULT_COLOR)
                light?.intensity = DirectionalLight.DEFAULT_INTENSITY
                light?.direction?.set(DirectionalLight.DEFAULT_DIRECTION)

                resetValues()
            }
        })

    }

    private fun resetValues() {
        val light = LightUtils.getDirectionalLight(projectManager.current().currScene.environment)
        intensity.text = light!!.intensity.toString()
        colorPickerField.selectedColor = light.color
        castShadows.isChecked = light.castsShadows
        shadowResSelectBox.selected = projectManager.current().currScene.shadowMapper.shadowResolution.value

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

    override fun onProjectChanged(event: ProjectChangedEvent) {
        resetValues()
    }

    override fun onSceneChanged(event: SceneChangedEvent) {
        resetValues()
    }

}