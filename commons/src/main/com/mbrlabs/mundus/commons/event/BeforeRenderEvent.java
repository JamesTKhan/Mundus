package com.mbrlabs.mundus.commons.event;

/**
 * Calls this event before render.
 */
public abstract class BeforeRenderEvent implements Event {

    @Override
    public EventType getType() {
        return EventType.BEFORE_RENDER;
    }
}
