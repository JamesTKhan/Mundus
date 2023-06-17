package com.mbrlabs.mundus.editor.core.converter;

import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.mbrlabs.mundus.commons.dto.LightComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent;
import com.mbrlabs.mundus.editor.scene3d.components.PickableLightComponent;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;

/**
 * @author JamesTKhan
 * @version June 07, 2022
 */
public class PickableLightComponentConverter {

    /**
     * Converts {@link LightComponentDTO} to {@link PickableLightComponent}.
     */
    public static PickableLightComponent convert(LightComponentDTO dto, GameObject go) {

        PickableLightComponent component = new PickableLightComponent(go, dto.getLightType());
        BaseLight light = component.getLight();

        if (light instanceof PointLightEx) {
            ((PointLightEx) light).set(dto.getColor(), dto.getPosition(), dto.getIntensity());
        } else if (light instanceof SpotLightEx) {
            ((SpotLightEx) light).set(dto.getColor(), dto.getPosition(), dto.getDirection(), dto.getIntensity(), dto.getCutoff(), dto.getExponential());
        }

        return component;
    }

    /**
     * Converts {@link LightComponent} to {@link LightComponentDTO}.
     */
    public static LightComponentDTO convert(LightComponent lightComponent) {
        LightComponentDTO descriptor = new LightComponentDTO();

        BaseLight light = lightComponent.getLight();

        descriptor.setLightType(lightComponent.getLightType());
        descriptor.setColor(light.color);

        if (light instanceof PointLightEx) {
            PointLightEx pointLight = (PointLightEx) light;
            descriptor.setPosition(pointLight.position);
            descriptor.setIntensity(pointLight.intensity);
        } else if (light instanceof SpotLightEx) {
            SpotLightEx spotLight = (SpotLightEx) light;
            descriptor.setPosition(spotLight.position);
            descriptor.setIntensity(spotLight.intensity);
            descriptor.setExponential(spotLight.exponent);
            descriptor.setCutoff(spotLight.cutoffAngle);
            descriptor.setDirection(spotLight.direction);
        }

        return descriptor;
    }
}
