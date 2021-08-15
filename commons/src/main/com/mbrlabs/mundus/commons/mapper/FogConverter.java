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
import com.mbrlabs.mundus.commons.dto.FogDTO;
import com.mbrlabs.mundus.commons.env.Fog;

/**
 * Converter for fog.
 */
public class FogConverter {

    /**
     * Converts {@link FogDTO} to {@link Fog}.
     */
    public static Fog convert(FogDTO dto) {
        if (dto == null) return null;
        Fog fog = new Fog();
        fog.density = dto.getDensity();
        fog.gradient = dto.getGradient();
        fog.color = new Color(dto.getColor());

        return fog;
    }

    /**
     * Converts {@link Fog} to {@link FogDTO}.
     */
    public static FogDTO convert(Fog fog) {
        if (fog == null) return null;
        FogDTO fogDescriptor = new FogDTO();
        fogDescriptor.setDensity(fog.density);
        fogDescriptor.setGradient(fog.gradient);
        fogDescriptor.setColor(Color.rgba8888(fog.color));

        return fogDescriptor;
    }
}
