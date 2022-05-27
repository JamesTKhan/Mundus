package com.mbrlabs.mundus.commons.mapper;

import com.badlogic.gdx.graphics.Color;
import com.mbrlabs.mundus.commons.dto.DirectionalLightDTO;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;

public class DirectionalLightConverter {

    /**
     * Converts {@link DirectionalLightDTO} to {@link DirectionalLight}.
     */
    public static DirectionalLight convert(DirectionalLightDTO dto) {
        if (dto == null) return null;
        DirectionalLight light = new DirectionalLight();
        light.intensity = dto.getIntensity();
        light.color.set(dto.getColor());
        light.direction.set(dto.getDirection());

        return light;
    }

    /**
     * Converts {@link DirectionalLight} to {@link DirectionalLightDTO}.
     */
    public static DirectionalLightDTO convert(DirectionalLight light) {
        if (light == null) return null;
        DirectionalLightDTO lightDescriptor = new DirectionalLightDTO();
        lightDescriptor.setIntensity(light.intensity);
        lightDescriptor.setColor(Color.rgba8888(light.color));
        lightDescriptor.setDirection(light.direction);

        return lightDescriptor;
    }
}
