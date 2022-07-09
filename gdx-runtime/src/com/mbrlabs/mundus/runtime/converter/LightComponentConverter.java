package com.mbrlabs.mundus.runtime.converter;

import com.mbrlabs.mundus.commons.dto.LightComponentDTO;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent;

/**
 * @author James Pooley
 * @version June 07, 2022
 */
public class LightComponentConverter {

    /**
     * Converts {@link LightComponentDTO} to {@link LightComponent}.
     */
    public static LightComponent convert(LightComponentDTO dto, GameObject go) {

        LightComponent component = new LightComponent(go, dto.getLightType());
        PointLight light = component.getLight();

        light.color.set(dto.getColor());
        light.intensity = dto.getIntensity();

        light.position.set(dto.getPosition());
        light.attenuation.constant = dto.getConstant();
        light.attenuation.linear = dto.getLinear();
        light.attenuation.exponential = dto.getExponential();

        if (dto.getLightType() == LightType.SPOT_LIGHT) {
            ((SpotLight) light).direction.set(dto.getDirection());
            ((SpotLight) light).setCutoff(dto.getCutoff());
        }

        return component;
    }
}
