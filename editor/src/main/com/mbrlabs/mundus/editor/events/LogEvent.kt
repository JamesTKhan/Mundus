package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.editorcommons.Subscribe

enum class LogType {
    INFO,
    WARN,
    ERROR
}

/**
 * An Event for posting new log entries in the log bar
 */
class LogEvent(val logType: LogType = LogType.INFO, val logMessage: String) {

    // Secondary constructor (to support Java classes) to use INFO as default
    constructor(logMessage: String) : this(LogType.INFO, logMessage)

    interface LogEventListener {
        @Subscribe
        fun onLogEvent(event: LogEvent)
    }

}