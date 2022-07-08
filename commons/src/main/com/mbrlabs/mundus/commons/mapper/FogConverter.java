/*
 * Copyright (c) 2021. See AUTHORS file.
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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.mbrlabs.mundus.commons.dto.FogDTO;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;

/**
 * Converter for fog.
 */
public class FogConverter {

    /**
     * Converts {@link FogDTO} to Environment attributes.
     */
    public static void convert(FogDTO dto, Environment environment) {
        if (dto == null) return;

        // Backward compat, near and far plane are new, if not set, give them some
        // default values otherwise entire scene will be foggy on load
        if (dto.getNearPlane() == 0f) { dto.setNearPlane(500f); }
        if (dto.getFarPlane() == 0f) { dto.setFarPlane(1000f); }

        environment.set(new FogAttribute(FogAttribute.FogEquation).set(dto.getNearPlane(), dto.getFarPlane(), dto.getGradient()));

        Color color = new Color();
        Color.rgba8888ToColor(color, dto.getColor());
        environment.set(ColorAttribute.createFog(color));
    }

    /**
     * Converts Fog attributes to {@link FogDTO}.
     */
    public static FogDTO convert(Environment environment) {
        FogAttribute fogAttribute = environment.get(FogAttribute.class, FogAttribute.FogEquation);
        ColorAttribute colorAttribute = environment.get(ColorAttribute.class, ColorAttribute.Fog);

        if (fogAttribute == null || colorAttribute == null) return null;

        FogDTO fogDescriptor = new FogDTO();

        fogDescriptor.setNearPlane(fogAttribute.value.x);
        fogDescriptor.setFarPlane(fogAttribute.value.y);
        fogDescriptor.setGradient(fogAttribute.value.z);
        fogDescriptor.setColor(Color.rgba8888(colorAttribute.color));

        return fogDescriptor;
    }
}
