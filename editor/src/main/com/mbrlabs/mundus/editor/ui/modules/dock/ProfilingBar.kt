package com.mbrlabs.mundus.editor.ui.modules.dock

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.profiling.MundusGLProfiler
import com.mbrlabs.mundus.editor.ui.widgets.AutoFocusScrollPane

/**
 * @author JamesTKhan
 * @version June 30, 2022
 */
class ProfilingBar : Tab(false, false) {

    private val glProfile: MundusGLProfiler = Mundus.inject()

    private val root = VisTable()
    private val profileTable = VisTable()
    private val pane = AutoFocusScrollPane(profileTable)

    private val fpsLabel = VisLabel("FPS : ")
    private val fps = VisLabel()

    private val drawCountLabel = VisLabel("Draw calls: ")
    private val drawCount = VisLabel()

    private val vertexCountLabel = VisLabel("Vertex count: ")
    private val vertexCount = VisLabel()

    private val shaderSwitchesLabel = VisLabel("Shader switches: ")
    private val shaderSwitches = VisLabel()

    private val textureBindLabel = VisLabel("Texture bindings: ")
    private val textureBind = VisLabel()

    private val cellPadding = 4f

    init {
        Mundus.registerEventListener(this)
        initUi()
    }

    private fun initUi() {
        root.setBackground("window-bg")
        root.left().top()
        root.add(pane).top().fillX().expandX()

        pane.fadeScrollBars = false

        profileTable.add(fpsLabel).left().pad(cellPadding)
        profileTable.add(fps).left().pad(cellPadding).expand().row()

        profileTable.add(drawCountLabel).left().pad(cellPadding)
        profileTable.add(drawCount).left().pad(cellPadding).expand().row()

        profileTable.add(vertexCountLabel).left().pad(cellPadding)
        profileTable.add(vertexCount).left().pad(cellPadding).expand().row()

        profileTable.add(shaderSwitchesLabel).left().pad(cellPadding)
        profileTable.add(shaderSwitches).left().pad(cellPadding).expand().row()

        profileTable.add(textureBindLabel).left().pad(cellPadding)
        profileTable.add(textureBind).left().pad(cellPadding).expand().row()
    }

    override fun getTabTitle(): String {
        return "Profiler"
    }

    override fun getContentTable(): Table {
        return root
    }

    override fun onShow() {
        super.onShow()
        if (glProfile.isEnabled) return
        glProfile.enable()
    }

    override fun onHide() {
        super.onHide()
        if (!glProfile.isEnabled) return
        glProfile.disable()
    }

    fun update() {
        if (!glProfile.isEnabled) return
        fps.setText(Gdx.graphics.framesPerSecond)
        drawCount.setText(glProfile.drawCalls)
        vertexCount.setText(String.format("%,d", glProfile.vertexCount.total.toLong()))
        shaderSwitches.setText(glProfile.shaderSwitches)
        textureBind.setText(glProfile.textureBindings)
    }

}