package com.mbrlabs.mundus.commons.dto;

import com.mbrlabs.mundus.commons.shadows.ShadowResolution;

/**
 * @author JamesTKhan
 * @version July 18, 2022
 */
public class ShadowSettingsDTO {
    private int viewportSize;
    private float camNearPlane;
    private float camFarPlane;
    private ShadowResolution shadowResolution;

    public int getViewportSize() {
        return viewportSize;
    }

    public void setViewportSize(int viewportSize) {
        this.viewportSize = viewportSize;
    }

    public float getCamNearPlane() {
        return camNearPlane;
    }

    public void setCamNearPlane(float camNearPlane) {
        this.camNearPlane = camNearPlane;
    }

    public float getCamFarPlane() {
        return camFarPlane;
    }

    public void setCamFarPlane(float camFarPlane) {
        this.camFarPlane = camFarPlane;
    }

    public ShadowResolution getShadowResolution() {
        return shadowResolution;
    }

    public void setShadowResolution(ShadowResolution shadowResolution) {
        this.shadowResolution = shadowResolution;
    }
}
