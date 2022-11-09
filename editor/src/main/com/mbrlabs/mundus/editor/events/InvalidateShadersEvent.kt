package com.mbrlabs.mundus.editor.events

/**
 * Force Mundus shaders to be recompiled by invalidating current shaders.
 *
 * @author JamesTKhan
 * @version November 09, 2022
 */
class InvalidateShadersEvent {
    interface InvalidateShadersListener {
        @Subscribe
        fun onInvalidateShaders(event: InvalidateShadersEvent)
    }
}