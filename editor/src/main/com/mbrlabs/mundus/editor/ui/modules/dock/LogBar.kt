package com.mbrlabs.mundus.editor.ui.modules.dock

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.ui.UI
import java.text.SimpleDateFormat
import java.util.*

/**
 * Docked log bar for displaying LogEvents with timestamps
 */
class LogBar : Tab(false, false), LogEvent.LogEventListener {

    private val root = VisTable()
    private val logTable = VisTable()
    private val pane = VisScrollPane(logTable)

    private val logOpsMenu = PopupMenu()
    private val clearLogsButton = MenuItem("Clear Logs")

    private val maxLogSize = 75
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    init {
        Mundus.registerEventListener(this)
        initUi()
    }

    private fun initUi() {
        root.setBackground("window-bg")
        root.left().top()
        root.add(pane).top().fillX().expandX()

        pane.fadeScrollBars = false

        logOpsMenu.addItem(clearLogsButton)
        registerListeners()
    }

    private fun registerListeners() {
        // Pop up menu on right click
        root.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (event!!.button == Input.Buttons.RIGHT) {
                    logOpsMenu.showMenu(UI, Gdx.input.x.toFloat(),
                            (Gdx.graphics.height - Gdx.input.y).toFloat())
                }
            }

            override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                // Give scroll focus to pane automatically when mouse enters
                UI.scrollFocus = pane
            }

            override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                // Only clear focus if the exit to another actor is NOT an actor within the LogBars root
                if (toActor?.isDescendantOf(root) != true)
                    UI.scrollFocus = null
            }
        })

        clearLogsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                logTable.clearChildren()
            }
        })
    }

    override fun getTabTitle(): String {
        return "Log"
    }

    override fun getContentTable(): Table {
        return root
    }

    override fun onLogEvent(event: LogEvent) {
        addLogMessage(event.logMessage)
    }

    /**
     * Appends new log message with a time stamp to the log table, then scrolls to most recent entry and
     * removes old entries.
     */
    private fun addLogMessage(message : String) {
        val timeStamp = dateFormat.format(Date())

        val logString = buildString {
            append("[")
            append(timeStamp)
            append("] ")
            append(message)
        }

        logTable.add(VisLabel(logString)).left().pad(4f).expand().row()

        // Remove oldest entry
        if (logTable.cells.size > maxLogSize)
            logTable.removeActorAt(0, true)

        scrollToBottom()
    }

    private fun scrollToBottom() {
        // Update layout and scroll to the latest (bottom) log message
        pane.layout()
        pane.scrollTo(0f, 0f, 0f, 0f)
    }
}