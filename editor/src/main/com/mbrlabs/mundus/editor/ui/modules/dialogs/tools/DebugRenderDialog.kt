package com.mbrlabs.mundus.editor.ui.modules.dialogs.tools

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.commons.utils.DebugRenderer
import com.mbrlabs.mundus.editor.Mundus
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

    private val projectManager: ProjectManager = Mundus.inject()
    private val preferencesManager : MundusPreferencesManager = Mundus.inject()
    private val debugRenderer: DebugRenderer = Mundus.inject()

    init {
        setupUI()
        setupListeners()
    }

    override fun show(stage: Stage?): VisDialog {
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

    }

    private fun toggle(checkBox: VisCheckBox) {
        checkBox.setProgrammaticChangeEvents(false)
        checkBox.toggle()
        checkBox.setProgrammaticChangeEvents(true)
    }

}