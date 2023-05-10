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

package com.mbrlabs.mundus.commons.mapper;

import com.badlogic.gdx.utils.OrderedMap;
import com.mbrlabs.mundus.commons.dto.CustomPropertiesComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.CustomPropertiesComponent;

public class CustomPropertiesComponentConverter {

    public static CustomPropertiesComponent convert(final CustomPropertiesComponentDTO dto, final GameObject go) {
        final CustomPropertiesComponent component = new CustomPropertiesComponent(go);
        final OrderedMap<String, String> dtoMap = dto.getCustomProperties();

        for (final OrderedMap.Entry<String, String> entry : dtoMap.iterator()) {
            component.put(entry.key, entry.value);
        }

        return component;
    }

    public static CustomPropertiesComponentDTO convert(final CustomPropertiesComponent component) {
        final CustomPropertiesComponentDTO dto = new CustomPropertiesComponentDTO();
        dto.setCustomProperties(component.getMap());

        return dto;
    }
}
