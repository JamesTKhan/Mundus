/*
 * Copyright (c) 2023. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.utils.OrderedMap;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class CustomPropertiesComponent extends AbstractComponent {

    private final OrderedMap<String, String> customProperties;

    public CustomPropertiesComponent(final GameObject go) {
        super(go);
        this.customProperties = new OrderedMap<>(5);
        type = Type.CUSTOM_PROPERTIES;
    }

    @Override
    public void update(final float delta) {
        // NOOP
    }

    @Override
    public Component clone(final GameObject go) {
        final CustomPropertiesComponent component = new CustomPropertiesComponent(go);

        for (final OrderedMap.Entry<String, String> entry : this.customProperties.iterator()) {
            component.put(entry.key, entry.value);
        }

        return component;
    }

    /**
     * See {@link OrderedMap#containsKey(Object)}.
     */
    public boolean containsKey(String key) {
        return customProperties.containsKey(key);
    }

    /**
     * See {@link OrderedMap#get(Object)}.
     */
    public String get(String key) {
        return customProperties.get(key);
    }

    /**
     * See {@link OrderedMap#put(Object, Object)}.
     */
    public String put(String key, String value) {
        return customProperties.put(key, value);
    }

    /**
     * See {@link OrderedMap#remove(Object)}.
     */
    public String remove(String key) {
        return customProperties.remove(key);
    }

    /**
     * @return Size of custom properties map.
     */
    public int getSize() {
        return customProperties.size;
    }

    /**
     * @return The whole custom properties map.
     */
    public OrderedMap<String, String> getMap() {
        return customProperties;
    }
}
