package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.math.Vector3;

public class DirectionalLightDTO extends BaseLightDTO {
    private Vector3 direction;

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }
}
