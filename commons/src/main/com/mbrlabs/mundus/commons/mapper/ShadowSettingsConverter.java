package com.mbrlabs.mundus.commons.mapper;

import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.dto.ShadowSettingsDTO;
import com.mbrlabs.mundus.commons.shadows.ShadowMapper;

/**
 * @author JamesTKhan
 * @version July 18, 2022
 */
public class ShadowSettingsConverter {

    /**
     * Converts {@link ShadowSettingsDTO} to a ShadowMapper on the scene.
     */
    public static void convert(Scene scene, ShadowSettingsDTO shadowSettingsDTO) {
        if (shadowSettingsDTO == null) return;

        ShadowMapper shadowMapper = new ShadowMapper(shadowSettingsDTO.getShadowResolution(),
                shadowSettingsDTO.getViewportSize(),
                shadowSettingsDTO.getViewportSize(),
                shadowSettingsDTO.getCamNearPlane(),
                shadowSettingsDTO.getCamFarPlane());

        scene.setShadowMapper(shadowMapper);
    }

    /**
     * Converts ShadowMapper settings in Scene to {@link ShadowSettingsDTO}.
     */
    public static ShadowSettingsDTO convert(Scene scene) {
        if (scene == null || scene.getShadowMapper() == null) return null;

        ShadowMapper shadowMapper = scene.getShadowMapper();
        ShadowSettingsDTO shadowSettingsDTO = new ShadowSettingsDTO();

        shadowSettingsDTO.setShadowResolution(shadowMapper.getShadowResolution());
        shadowSettingsDTO.setViewportSize(shadowMapper.getViewportWidth());
        shadowSettingsDTO.setCamNearPlane(shadowMapper.getNearPlane());
        shadowSettingsDTO.setCamFarPlane(shadowMapper.getFarPlane());

        return shadowSettingsDTO;
    }
}
