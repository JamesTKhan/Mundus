package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;

/**
 * Utilities class for lighting.
 *
 * @author JamesTKhan
 * @version June 06, 2022
 */
public class LightUtils {

    public static final int MAX_POINT_LIGHTS = 12;
    public static final int MAX_SPOT_LIGHTS = 12;

    public static final float DEFAULT_INTENSITY = 2.0f;
    public static final Color DEFAULT_COLOR = Color.WHITE.cpy();
    public static final Vector3 DEFAULT_DIRECTION = new Vector3(0.40f, -1, 0);

    /**
     * Return the first Directional Light. Currently only one Directional Light is supported.
     */
    public static MundusDirectionalShadowLight getDirectionalLight(Environment env) {
        DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
        if (dirLightAttribs == null) return null;

        Array<DirectionalLight> dirLights = dirLightAttribs.lights;
        if (dirLights != null && dirLights.size > 0) {
            return (MundusDirectionalShadowLight) dirLights.first();
        }
        return null;
    }

    public static Array<PointLight> getPointLights(Environment env) {
        PointLightsAttribute attr = env.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        return attr == null ? new Array<PointLight>() : attr.lights;
    }

    public static int getPointLightsCount(Environment env) {
        Array<PointLight> pointLights = getPointLights(env);
        return pointLights == null ? 0 : pointLights.size;
    }

    public static Array<SpotLight> getSpotLights(Environment env) {
        SpotLightsAttribute spotAttr = env.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        return spotAttr == null ? new Array<SpotLight>() : spotAttr.lights;
    }

    public static int getSpotLightsCount(Environment env) {
        Array<SpotLight> spotLights = getSpotLights(env);
        return spotLights == null ? 0 : spotLights.size;
    }

    /**
     * Checks whether the environment can support adding a light of the given lightType.
     *
     * @param env the environment to check
     * @param lightType the lightType to be added
     * @return true if it can be added, else false
     */
    public static boolean canCreateLight(Environment env, LightType lightType) {
        switch(lightType) {
            case DIRECTIONAL_LIGHT:
                return false;
            case POINT_LIGHT:
                return getPointLightsCount(env) < LightUtils.MAX_POINT_LIGHTS;
            case SPOT_LIGHT:
                return getSpotLightsCount(env) < LightUtils.MAX_SPOT_LIGHTS;
        }
        return false;
    }

    /**
     * Adds given PointLight to environment only if it's not already in the environment.
     *
     * @param env the environment to add the light to
     * @param light the light to be added
     */
    public static void addLightIfMissing(MundusEnvironment env, BaseLight light) {
        if (light instanceof PointLightEx && !getPointLights(env).contains((PointLightEx) light, true)) {
            env.add(light);
        } else if  (light instanceof SpotLightEx && !getSpotLights(env).contains((SpotLight) light, true)) {
            env.add(light);
        }
    }

    /**
     * Copy light settings from a light to another light
     *
     * @param from the light to copy from
     * @param to the light to copy to
     */
    public static void copyLightSettings(BaseLight from, BaseLight to) {
        to.color.set(from.color);

        if (from instanceof PointLightEx && to instanceof PointLightEx) {
            PointLightEx fromI = (PointLightEx) from;
            PointLightEx toI = (PointLightEx) to;
            toI.intensity = fromI.intensity;
            toI.position.set(fromI.position);
        }

        if (from instanceof SpotLightEx && to instanceof SpotLightEx) {
            SpotLightEx fromI = (SpotLightEx) from;
            SpotLightEx toI = (SpotLightEx) to;
            toI.intensity = fromI.intensity;
            toI.position.set(fromI.position);
            toI.direction.set(fromI.direction);
            toI.cutoffAngle = fromI.cutoffAngle;
            toI.exponent = fromI.exponent;
        }
    }

}
