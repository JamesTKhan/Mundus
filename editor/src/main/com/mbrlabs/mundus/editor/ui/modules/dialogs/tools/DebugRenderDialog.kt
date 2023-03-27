package com.mbrlabs.mundus.editor.ui.modules.dialogs.tools

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisRadioButton
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel

class DebugRenderDialog : BaseDialog(TITLE) {

    companion object {
        private const val TITLE = "Debug Render Options"
    }

    private val showBoundingBoxes = VisCheckBox(null)
    private val wireFrameMode = VisCheckBox(null)
    private val helperLines = VisCheckBox(null)
    private val rectangleRadio = VisRadioButton("Rectangle")
    private val hexagonRadio = VisRadioButton("Hexagon")
    private val columnSpinner = Spinner("Column:", IntSpinnerModel(2, 2, 10))
    private val projectManager: ProjectManager = Mundus.inject()

    init {
        setupUI()
        setupListeners()
    }

    override fun act(delta: Float) {
        super.act(delta)

        if (projectManager.current().renderWireframe != wireFrameMode.isChecked) {
            wireFrameMode.setProgrammaticChangeEvents(false)
            wireFrameMode.toggle()
            wireFrameMode.setProgrammaticChangeEvents(true)
        }

        if (projectManager.current().renderDebug != showBoundingBoxes.isChecked) {
            showBoundingBoxes.setProgrammaticChangeEvents(false)
            showBoundingBoxes.toggle()
            showBoundingBoxes.setProgrammaticChangeEvents(true)
        }
    }

    private fun setupUI() {
        val table = VisTable()
        table.add(ToolTipLabel("Show Bounding Boxes", "Renders boxes around model objects. Useful for debugging frustum culling as" +
                "\nthe bounding boxes reflect what frustum culling will use when determining to cull an object. Hotkey: CTRL+F2")).left()
        table.add(showBoundingBoxes).left().padBottom(10f).row()

        table.add(ToolTipLabel("Wireframe Mode", "Uses OpenGL glPolygonMode with GL_LINE to show wireframe.  Hotkey: CTRL+F3")).left()
        table.add(wireFrameMode).left().padBottom(10f).row()

        table.add(ToolTipLabel("Helper lines", "TODO")).left()
        table.add(helperLines)
        table.row()
        table.add(createHelperLinesTable()).left()


        add(table)
    }

    private fun setupListeners() {
        showBoundingBoxes.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                projectManager.current().renderDebug = !projectManager.current().renderDebug
            }
        })

        wireFrameMode.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                projectManager.current().renderWireframe = !projectManager.current().renderWireframe
            }
        })

        helperLines.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val touchable = if (helperLines.isChecked) Touchable.enabled else Touchable.disabled

                rectangleRadio.touchable = touchable
                hexagonRadio.touchable = touchable
                columnSpinner.touchable = touchable
            }
        })

        rectangleRadio.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                hexagonRadio.isChecked = !rectangleRadio.isChecked
            }
        })

        hexagonRadio.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                rectangleRadio.isChecked = !hexagonRadio.isChecked
            }
        })
    }

    private fun createHelperLinesTable(): VisTable {
        rectangleRadio.touchable = Touchable.disabled
        hexagonRadio.touchable = Touchable.disabled
        columnSpinner.touchable = Touchable.disabled

        rectangleRadio.isChecked = true

        val helperLinesTable = VisTable()
        helperLinesTable.padLeft(20f)
        helperLinesTable.add(rectangleRadio).left()
        helperLinesTable.add(hexagonRadio).right()
        helperLinesTable.row()
        helperLinesTable.add(columnSpinner)

        return helperLinesTable
    }
}