package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.math.Vector3;

public class DirectionalLightDTO extends BaseLightDTO {
    private Vector3 direction;
    private boolean castsShadows;
    private ShadowSettingsDTO shadowSettingsDTO;

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }

    public ShadowSettingsDTO getShadowSettingsDTO() {
        return shadowSettingsDTO;
    }

    public void setShadowSettingsDTO(ShadowSettingsDTO shadowSettingsDTO) {
        this.shadowSettingsDTO = shadowSettingsDTO;
    }
}
