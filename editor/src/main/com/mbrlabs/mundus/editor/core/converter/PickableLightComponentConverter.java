package com.mbrlabs.mundus.editor.core.converter;

import com.mbrlabs.mundus.commons.dto.LightComponentDTO;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent;
import com.mbrlabs.mundus.editor.scene3d.components.PickableLightComponent;

/**
 * @author James Pooley
 * @version June 07, 2022
 */
public class PickableLightComponentConverter {

    /**
     * Converts {@link LightComponentDTO} to {@link PickableLightComponent}.
     */
    public static PickableLightComponent convert(LightComponentDTO dto, GameObject go) {

        PickableLightComponent component = new PickableLightComponent(go, dto.getLightType());
        PointLight light = component.getLight();

        light.color.set(dto.getColor());
        light.intensity = dto.getIntensity();

        light.position.set(dto.getPosition());
        light.attenuation.constant = dto.getConstant();
        light.attenuation.linear = dto.getLinear();
        light.attenuation.exponential = dto.getExponential();

        if (dto.getLightType() == LightType.SPOT_LIGHT) {
            ((SpotLight) light).direction.set(dto.getDirection());
            ((SpotLight) light).cutoff = dto.getCutoff();
        }

        return component;
    }

    /**
     * Converts {@link LightComponent} to {@link LightComponentDTO}.
     */
    public static LightComponentDTO convert(LightComponent lightComponent) {
        LightComponentDTO descriptor = new LightComponentDTO();

        PointLight light = lightComponent.getLight();

        descriptor.setLightType(light.lightType);
        descriptor.setColor(light.color);
        descriptor.setIntensity(light.intensity);

        descriptor.setPosition(light.position);
        descriptor.setConstant(light.attenuation.constant);
        descriptor.setLinear(light.attenuation.linear);
        descriptor.setExponential(light.attenuation.exponential);

        if (lightComponent.getLight().lightType == LightType.SPOT_LIGHT) {
            descriptor.setCutoff(((SpotLight)light).cutoff);
            descriptor.setDirection(((SpotLight)light).direction);
        }

        return descriptor;
    }
}
