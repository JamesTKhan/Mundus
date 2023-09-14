package com.mbrlabs.mundus.editor.ui.modules.dialogs.tools

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.mbrlabs.mundus.commons.utils.DebugRenderer
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.helperlines.HelperLineType
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.preferences.MundusPreferencesManager
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel

class DebugRenderDialog : BaseDialog(TITLE) {

    companion object {
        private const val TITLE = "Debug Render Options"
    }

    private val showBoundingBoxes = VisCheckBox(null)
    private val showBoundingBoxesOnTop = VisCheckBox(null)
    private val showFacingArrow = VisCheckBox(null)
    private val wireFrameMode = VisCheckBox(null)
    private val helperLines = VisCheckBox(null)
    private val rectangleRadio = VisRadioButton("Rectangle")
    private val hexagonRadio = VisRadioButton("Hexagon")
    private val columnSpinnerModel = IntSpinnerModel(2, 2, 100)
    private val columnSpinner = Spinner("Column:", columnSpinnerModel)
    private val counterOffsetXSpinnerModel = IntSpinnerModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE)
    private val counterOffsetXSpinner = Spinner("Counter offset X:", counterOffsetXSpinnerModel)
    private val counterOffsetYSpinnerModel = IntSpinnerModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE)
    private val counterOffsetYSpinner = Spinner("Counter offset Y:", counterOffsetYSpinnerModel)

    private val projectManager: ProjectManager = Mundus.inject()
    private val preferencesManager : MundusPreferencesManager = Mundus.inject()
    private val debugRenderer: DebugRenderer = Mundus.inject()

    private lateinit var helperLineSettingsTable: Table

    init {
        setupUI()
        setupListeners()
    }

    override fun show(stage: Stage?): VisDialog {
        val hasHelperLines = projectManager.current().helperLines.hasHelperLines()
        if ((hasHelperLines && !helperLines.isChecked) || (!hasHelperLines && helperLines.isChecked)) {
            toggle(helperLines)
        }

        showBoundingBoxesOnTop.isChecked = debugRenderer.isAppearOnTop
        showFacingArrow.isChecked = debugRenderer.isShowFacingArrow

        return super.show(stage)
    }

    override fun act(delta: Float) {
        super.act(delta)

        if (projectManager.current().renderWireframe != wireFrameMode.isChecked) {
            toggle(wireFrameMode)
        }

        if (debugRenderer.isEnabled != showBoundingBoxes.isChecked) {
            toggle(showBoundingBoxes)
        }
    }

    private fun setupUI() {
        val table = VisTable()
        table.add(ToolTipLabel("Show Bounding Boxes", "Renders boxes around model objects. Useful for debugging frustum culling as" +
                "\nthe bounding boxes reflect what frustum culling will use when determining to cull an object. Hotkey: CTRL+F2")).left()
        table.add(showBoundingBoxes).left().padBottom(10f).row()

        table.add(ToolTipLabel("Render Debug On Top", "Whether to render debug lines with depth or not.")).left()
        table.add(showBoundingBoxesOnTop).left().padBottom(10f).row()

        table.add(ToolTipLabel("Show Facing Arrow", "Renders a forward facing arrow with origin (0,0,1) form the selected game object.")).left()
        table.add(showFacingArrow).left().padBottom(10f).row()

        table.add(ToolTipLabel("Wireframe Mode", "Uses OpenGL glPolygonMode with GL_LINE to show wireframe.  Hotkey: CTRL+F3")).left()
        table.add(wireFrameMode).left().padBottom(10f).row()

        table.add(ToolTipLabel("Helper lines", "Render helper lines on the terrains.")).left()
        table.add(helperLines)
        table.row()

        helperLineSettingsTable = createHelperLinesTable()
        helperLineSettingsTable.isVisible = false
        table.add(helperLineSettingsTable).left()

        add(table)
    }

    private fun setupListeners() {
        showBoundingBoxes.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                debugRenderer.isEnabled = showBoundingBoxes.isChecked
                preferencesManager.set(MundusPreferencesManager.GLOB_BOOL_DEBUG_RENDERER_ON, showBoundingBoxes.isChecked)
            }
        })

        showBoundingBoxesOnTop.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                debugRenderer.isAppearOnTop = showBoundingBoxesOnTop.isChecked
                preferencesManager.set(MundusPreferencesManager.GLOB_BOOL_DEBUG_RENDERER_DEPTH_OFF, showBoundingBoxesOnTop.isChecked)
            }
        })

        showFacingArrow.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                debugRenderer.isShowFacingArrow = showFacingArrow.isChecked
                preferencesManager.set(MundusPreferencesManager.GLOB_BOOL_DEBUG_FACING_ARROW, showFacingArrow.isChecked)
            }
        })

        wireFrameMode.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                projectManager.current().renderWireframe = !projectManager.current().renderWireframe
            }
        })

        helperLines.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                helperLineSettingsTable.isVisible = helperLines.isChecked

                if (helperLines.isChecked) {
                    createHelperLines()
                } else {
                    clearHelperLines()
                }
            }
        })

        rectangleRadio.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                rectangleRadio.setProgrammaticChangeEvents(false)
                hexagonRadio.setProgrammaticChangeEvents(false)
                if (rectangleRadio.isChecked) {
                    hexagonRadio.isChecked = false
                } else {
                    rectangleRadio.isChecked = true
                }
                rectangleRadio.setProgrammaticChangeEvents(true)
                hexagonRadio.setProgrammaticChangeEvents(true)

                clearHelperLines()
                createHelperLines()
            }
        })

        hexagonRadio.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                rectangleRadio.setProgrammaticChangeEvents(false)
                hexagonRadio.setProgrammaticChangeEvents(false)
                if (hexagonRadio.isChecked) {
                    rectangleRadio.isChecked = false
                } else {
                    hexagonRadio.isChecked = true
                }
                rectangleRadio.setProgrammaticChangeEvents(true)
                hexagonRadio.setProgrammaticChangeEvents(true)

                clearHelperLines()
                createHelperLines()
            }
        })

        columnSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                clearHelperLines()
                createHelperLines()
            }
        })

        counterOffsetXSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                clearHelperLines()
                createHelperLines()
            }
        })

        counterOffsetYSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                clearHelperLines()
                createHelperLines()
            }
        })
    }

    private fun createHelperLinesTable(): VisTable {
        rectangleRadio.isChecked = true

        val helperLinesTable = VisTable()
        helperLinesTable.defaults().left()
        helperLinesTable.padLeft(20f)
        helperLinesTable.add(rectangleRadio).left()
        helperLinesTable.add(hexagonRadio).right()
        helperLinesTable.row()
        helperLinesTable.add(columnSpinner)
        helperLinesTable.row()
        helperLinesTable.add(counterOffsetXSpinner)
        helperLinesTable.row()
        helperLinesTable.add(counterOffsetYSpinner).padBottom(5f)

        return helperLinesTable
    }

    private fun toggle(checkBox: VisCheckBox) {
        checkBox.setProgrammaticChangeEvents(false)
        checkBox.toggle()
        checkBox.setProgrammaticChangeEvents(true)
    }

    private fun getHelperLineType(): HelperLineType {
        return if (rectangleRadio.isChecked) HelperLineType.RECTANGLE else HelperLineType.HEXAGON
    }

    private fun clearHelperLines() = projectManager.current().helperLines.dispose()

    private fun createHelperLines() = projectManager.current().helperLines.build(getHelperLineType(), columnSpinnerModel.value, counterOffsetXSpinnerModel.value, counterOffsetYSpinnerModel.value, projectManager.current().currScene.terrains)
}