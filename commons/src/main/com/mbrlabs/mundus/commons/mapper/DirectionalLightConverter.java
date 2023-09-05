package com.mbrlabs.mundus.commons.mapper;

import com.badlogic.gdx.graphics.Color;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.dto.DirectionalLightDTO;
import com.mbrlabs.mundus.commons.dto.ShadowSettingsDTO;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;

public class DirectionalLightConverter {

    /**
     * Converts {@link DirectionalLightDTO} to {@link MundusDirectionalShadowLight}.
     */
    public static MundusDirectionalShadowLight convert(Scene scene, DirectionalLightDTO dto) {
        if (dto == null) return null;
        MundusDirectionalShadowLight light;

        ShadowSettingsDTO shadowSettingsDTO = dto.getShadowSettingsDTO();
        light = new MundusDirectionalShadowLight(
                shadowSettingsDTO.getShadowResolution(),
                shadowSettingsDTO.getViewportSize(),
                shadowSettingsDTO.getViewportSize(),
                shadowSettingsDTO.getCamNearPlane(),
                shadowSettingsDTO.getCamFarPlane());
        light.setCastsShadows(dto.isCastsShadows());

        light.direction.set(dto.getDirection()).nor();
        light.color.set(dto.getColor());
        light.intensity = dto.getIntensity();

        if (light.direction.x == 0.0 && light.direction.z == 0.0) {
            // avoid zero direction as it will cause issues with shadows if X and Z are both zero
            // Not a Mundus issue, present in libgdx as well
            light.direction.x = 0.1f;
        }

        return light;
    }

    /**
     * Converts {@link DirectionalLightEx} to {@link DirectionalLightDTO}.
     */
    public static DirectionalLightDTO convert(Scene scene, MundusDirectionalShadowLight light) {
        if (light == null) return null;
        DirectionalLightDTO lightDescriptor = new DirectionalLightDTO();
        lightDescriptor.setIntensity(light.intensity);
        lightDescriptor.setColor(Color.rgba8888(light.color));
        lightDescriptor.setDirection(light.direction);
        lightDescriptor.setCastsShadows(light.isCastsShadows());

        lightDescriptor.setShadowSettingsDTO(ShadowSettingsConverter.convert(scene));

        return lightDescriptor;
    }
}
