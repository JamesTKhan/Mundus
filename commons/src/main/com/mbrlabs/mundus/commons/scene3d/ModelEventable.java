package com.mbrlabs.mundus.commons.scene3d;

import com.mbrlabs.mundus.commons.event.BeforeDepthRenderEvent;
import com.mbrlabs.mundus.commons.event.Event;

/**
 * Indicates the object that can be eventable.
 *
 * @author Dgzt
 */
public interface ModelEventable {

    /**
     * Adds event to object.
     */
    void addEvent(Event event);

    /**
     * Removes event from object.
     */
    void removeEvent(Event event);

    /**
     * Triggers all {@link BeforeDepthRenderEvent}.
     */
    void triggerBeforeDepthRenderEvent();

    /**
     * Triggers all {@link com.mbrlabs.mundus.commons.event.BeforeRenderEvent}.
     */
    void triggerBeforeRenderEvent();

}
