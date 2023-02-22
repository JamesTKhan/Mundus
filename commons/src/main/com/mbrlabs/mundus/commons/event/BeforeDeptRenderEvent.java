package com.mbrlabs.mundus.commons.event;

/**
 * Calls this event before dept render.
 */
public abstract class BeforeDeptRenderEvent implements Event {

    @Override
    public EventType getType() {
        return EventType.BEFORE_DEPT_RENDER;
    }
}
