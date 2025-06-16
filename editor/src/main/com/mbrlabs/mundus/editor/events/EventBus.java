/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.events;

import com.mbrlabs.mundus.editor.utils.ReflectionUtils;
import com.mbrlabs.mundus.editorcommons.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Event bus via reflection.
 * <p>
 * Subscribers need to provide a public method, annotated with @Subscribe and 1
 * parameter as event type.
 * <p>
 * Inspired by the Otto Event Bus for Android.
 *
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class EventBus {

    private static class EventBusException extends RuntimeException {
        private EventBusException(String s) {
            super(s);
        }
    }

    /**
     * Tracks the subscriber methods for each event type.
     */
    private static class SubscriberMethod {
        final Object instance;
        final Method method;

        SubscriberMethod(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
        }
    }

    // Maps the event class to subscriber methods, cached for performance
    private final Map<Class<?>, List<SubscriberMethod>> subscribersMap;

    public EventBus() {
        subscribersMap = new HashMap<>();
    }

    /**
     * Registers all subscriber methods of the given object.
     * For performance reasons we cache the subscriber methods for each event on register.
     *
     * @param subscriber the object with subscriber methods
     */
    public void register(Object subscriber) {
        // Loop over each method in the subscriber
        for (Method method : subscriber.getClass().getDeclaredMethods()) {
            if (!isSubscriber(method)) continue;

            if (method.getParameterTypes().length != 1) {
                throw new EventBusException("Size of parameter list of method " + method.getName() + " in "
                        + subscriber.getClass().getName() + " must be 1");
            }

            Class<?> eventType = method.getParameterTypes()[0];

            // Get the list of subscribers for this event
            List<SubscriberMethod> subscribers = subscribersMap.computeIfAbsent(eventType, k -> new ArrayList<>());
            subscribers.add(new SubscriberMethod(subscriber, method));
        }
    }

    /**
     * Unregisters all subscriber methods of the given object.
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        // Iterate over each event type in the map, remove if matched
        for (List<SubscriberMethod> methods : subscribersMap.values()) {
            methods.removeIf(subscriberMethod -> subscriberMethod.instance.equals(subscriber));
        }
    }

    /**
     * Posts an event to all subscribers of this event type.
     * @param event the event to post
     */
    public void post(Object event) {
        // Get the list of subscribers for this event
        List<SubscriberMethod> subscribers = subscribersMap.get(event.getClass());

        if (subscribers == null) return;

        // Call each subscriber method
        for (SubscriberMethod subscriberMethod : subscribers) {
            try {
                subscriberMethod.method.invoke(subscriberMethod.instance, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the given method is a subscriber.
     * Slow with reflection, but we only check it on register.
     *
     * @param method the method to check
     * @return true if the method is a subscriber
     */
    private boolean isSubscriber(Method method) {
        // check if @Subscribe is directly used in class
        boolean isSub = ReflectionUtils.hasMethodAnnotation(method, Subscribe.class);
        if (isSub) return true;

        // check if implemented interfaces of this class have a @Subscribe
        // annotation
        Class[] interfaces = method.getDeclaringClass().getInterfaces();
        for (Class i : interfaces) {
            try {
                Method interfaceMethod = i.getMethod(method.getName(), method.getParameterTypes());
                if (interfaceMethod != null) {
                    isSub = ReflectionUtils.hasMethodAnnotation(interfaceMethod, Subscribe.class);
                    if (isSub) return true;
                }
            } catch (NoSuchMethodException e) {
                // silently ignore -> this interface simply does not declare
                // such a method
            }
        }

        return false;
    }

}
