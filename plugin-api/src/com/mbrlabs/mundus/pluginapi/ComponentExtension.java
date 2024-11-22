/*
 * Copyright (c) 2024. See AUTHORS file.
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

package com.mbrlabs.mundus.pluginapi;

import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.pluginapi.ui.RootWidget;
import org.pf4j.ExtensionPoint;

public interface ComponentExtension extends ExtensionPoint {

    /**
     * @return The component type what the plugin use.
     */
    Component.Type getComponentType();

    /**
     * @return The component name.
     */
    String getComponentName();

    /**
     * @param gameObject The game object.
     * @return The created component for given game object.
     */
    Component createComponent(GameObject gameObject);

    /**
     * Setups widget for custom component for Inspector.
     *
     * @param component The component.
     * @param rootWidget The root widget.
     */
    void setupComponentInspectorWidget(Component component, RootWidget rootWidget);

    /**
     * @return The converter for load and save properties for custom component.
     */
    CustomComponentConverter getConverter();
}
