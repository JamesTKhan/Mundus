package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.PointLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLightsAttribute;

/**
 * Utilities class for lighting.
 *
 * @author James Pooley
 * @version June 06, 2022
 */
public class LightUtils {

    public static Array<PointLight> getPointLights(Environment env) {
        PointLightsAttribute attr = env.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        return attr == null ? null : attr.lights;
    }

    public static int getPointLightsCount(Environment env) {
        Array<PointLight> pointLights = getPointLights(env);
        return pointLights == null ? 0 : pointLights.size;
    }

    public static Array<SpotLight> getSpotLights(Environment env) {
        SpotLightsAttribute spotAttr = env.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        return spotAttr == null ? null : spotAttr.lights;
    }

    public static int getSpotLightsCount(Environment env) {
        Array<SpotLight> spotLights = getSpotLights(env);
        return spotLights == null ? 0 : spotLights.size;
    }

}
