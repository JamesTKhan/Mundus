package com.mbrlabs.mundus.commons.env;

/**
 * Class for holding default values for camera settings.
 *
 * @author JamesTKhan
 * @version July 11, 2022
 */
public class CameraSettings {
    private CameraSettings() {}

    public static float DEFAULT_NEAR_PLANE = 0.2f;
    public static float DEFAULT_FAR_PLANE = 10000f;
    public static float DEFAULT_FOV = 67f;
}
