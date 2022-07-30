package com.mbrlabs.mundus.editor.ui.modules.dock

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.ui.widgets.AutoFocusScrollPane
import java.text.SimpleDateFormat
import java.util.*

/**
 * Docked log bar for displaying LogEvents with timestamps
 */
class LogBar : Tab(false, false), LogEvent.LogEventListener {

    private val root = VisTable()
    private val logTable = VisTable()
    private val pane = AutoFocusScrollPane(logTable)

    private val logOpsMenu = PopupMenu()
    private val clearLogsButton = MenuItem("Clear Logs")

    private val maxLogSize = 75
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    private val logTextPadding = 4f
    private var errorColor = Color(222f / 255f, 67f / 255f,67f / 255f, 1f)
    private var warnColor = Color(255f, 155f / 255f,0f, 1f)

    // True when new entries are in the log and log is not the active tab
    var newEntries = false

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
        })

        clearLogsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                logTable.clearChildren()
            }
        })
    }

    override fun onShow() {
        super.onShow()
        newEntries = false
    }

    override fun getTabTitle(): String {
        if (newEntries)
            return "Log*"

        return "Log"
    }

    override fun getContentTable(): Table {
        return root
    }

    override fun onLogEvent(event: LogEvent) {
        addLogMessage(event)
    }

    /**
     * Appends new log message with a time stamp to the log table, then scrolls to most recent entry and
     * removes old entries.
     */
    private fun addLogMessage(event: LogEvent) {
        if (!isActiveTab)
            newEntries = true

        val timeStamp = dateFormat.format(Date())

        val logString = buildString {
            append("[")
            append(timeStamp)
            append("] ")
            append("[")
            append(event.logType)
            append("] ")
            append(event.logMessage)
        }

        val visLabel: VisLabel = when(event.logType) {
            LogType.INFO -> VisLabel(logString)
            LogType.WARN -> VisLabel(logString, warnColor)
            LogType.ERROR -> VisLabel(logString, errorColor)
        }

        logTable.add(visLabel).left().pad(logTextPadding).expand().row()

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