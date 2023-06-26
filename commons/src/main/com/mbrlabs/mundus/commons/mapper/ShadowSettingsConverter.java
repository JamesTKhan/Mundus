package com.mbrlabs.mundus.commons.mapper;

import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.dto.ShadowSettingsDTO;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import com.mbrlabs.mundus.commons.utils.LightUtils;

/**
 * @author JamesTKhan
 * @version July 18, 2022
 */
public class ShadowSettingsConverter {

    /**
     * Converts ShadowMapper settings in Scene to {@link ShadowSettingsDTO}.
     */
    public static ShadowSettingsDTO convert(Scene scene) {
        if (scene == null) return null;

        MundusDirectionalShadowLight directionalLightEx = LightUtils.getDirectionalLight(scene.environment);
        ShadowSettingsDTO shadowSettingsDTO = new ShadowSettingsDTO();

        shadowSettingsDTO.setShadowResolution(directionalLightEx.getShadowResolution());
        shadowSettingsDTO.setViewportSize((int) directionalLightEx.getCamera().viewportWidth);
        shadowSettingsDTO.setCamNearPlane(directionalLightEx.getCamera().near);
        shadowSettingsDTO.setCamFarPlane(directionalLightEx.getCamera().far);

        return shadowSettingsDTO;
    }
}
