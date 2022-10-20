package com.mbrlabs.mundus.commons;

import com.mbrlabs.mundus.commons.water.WaterResolution;

/**
 * Settings specific to a Scene.
 * @author JamesTKhan
 * @version July 23, 2022
 */
public class SceneSettings {

    // Water
    public float waterHeight = 0f;
    public final float distortionEdgeCorrection = 1f;
    public WaterResolution waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION;
    public boolean enableWaterReflections = true;
    public boolean enableWaterRefractions = true;

    // Performance
    public boolean useFrustumCulling = true;
}
