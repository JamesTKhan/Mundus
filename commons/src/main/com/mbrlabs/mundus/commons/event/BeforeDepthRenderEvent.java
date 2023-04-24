package com.mbrlabs.mundus.commons.event;

/**
 * Calls this event before depth render.
 */
public abstract class BeforeDepthRenderEvent implements Event {

    @Override
    public EventType getType() {
        return EventType.BEFORE_DEPTH_RENDER;
    }
}
