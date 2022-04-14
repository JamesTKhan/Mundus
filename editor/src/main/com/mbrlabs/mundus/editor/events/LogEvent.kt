package com.mbrlabs.mundus.editor.events

/**
 * An Event for posting new log entries in the log bar
 */
class LogEvent(val logMessage: String) {

    interface LogEventListener {
        @Subscribe
        fun onLogEvent(event: LogEvent)
    }

}