package com.mbrlabs.mundus.runtime.converter;

import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.mbrlabs.mundus.commons.dto.LightComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;

/**
 * @author JamesTKhan
 * @version June 07, 2022
 */
public class LightComponentConverter {

    /**
     * Converts {@link LightComponentDTO} to {@link LightComponent}.
     */
    public static LightComponent convert(LightComponentDTO dto, GameObject go) {

        LightComponent component = new LightComponent(go, dto.getLightType());
        BaseLight light = component.getLight();

        if (light instanceof PointLightEx) {
            ((PointLightEx) light).set(dto.getColor(), dto.getPosition(), dto.getIntensity());
        } else if (light instanceof SpotLightEx) {
            ((SpotLightEx) light).set(dto.getColor(), dto.getPosition(), dto.getDirection(), dto.getIntensity(), dto.getCutoff(), dto.getExponential());
        }

        return component;
    }
}
