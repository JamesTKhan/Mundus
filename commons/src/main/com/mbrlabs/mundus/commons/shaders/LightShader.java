package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import com.mbrlabs.mundus.commons.utils.LightUtils;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;

/**
 * Extend this shader and call setLights method to apply lighting uniforms.
 *
 * @author JamesTKhan
 * @version June 02, 2022
 */
public abstract class LightShader extends BaseShader {
    // ============================ LIGHTS ============================
    protected final int UNIFORM_SHADOW_BIAS = register(new Uniform("u_shadowBias"));
    protected final int UNIFORM_USE_SHADOWS = register(new Uniform("u_useShadows"));
    protected final int UNIFORM_SHADOW_TEXTURE = register(new Uniform("u_shadowTexture"));
    protected final int UNIFORM_SHADOW_VIEW = register(new Uniform("u_shadowMapProjViewTrans"));
    protected final int UNIFORM_SHADOW_PCF_OFFSET = register(new Uniform("u_shadowPCFOffset"));

    // Specular
    protected final int UNIFORM_USE_SPECULAR = register(new Uniform("u_useSpecular"));
    protected final int UNIFORM_MATERIAL_SHININESS = register(new Uniform("u_shininess"));

    // Directional Light
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR = register(new Uniform("u_directionalLight.Base.Color"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR_AMBIENT = register(new Uniform("u_directionalLight.Base.AmbientColor"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_DIR = register(new Uniform("u_directionalLight.Direction"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY = register(new Uniform("u_directionalLight.Base.DiffuseIntensity"));

    // Point Lights
    protected final int UNIFORM_POINT_LIGHT_NUM_ACTIVE = register(new Uniform("u_activeNumPointLights"));

    protected int[] UNIFORM_POINT_LIGHT_COLOR = new int[LightUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_INTENSITY = new int[LightUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_INTENSITY_AMBIENT = new int[LightUtils.MAX_POINT_LIGHTS];

    protected int[] UNIFORM_POINT_LIGHT_POS = new int[LightUtils.MAX_POINT_LIGHTS];

    // SpotLights
    protected final int UNIFORM_SPOT_LIGHT_NUM_ACTIVE = register(new Uniform("u_activeNumSpotLights"));

    protected int[] UNIFORM_SPOT_LIGHT_COLOR = new int[LightUtils.MAX_SPOT_LIGHTS];
    protected int[] UNIFORM_SPOT_LIGHT_INTENSITY = new int[LightUtils.MAX_SPOT_LIGHTS];
    protected int[] UNIFORM_SPOT_LIGHT_INTENSITY_AMBIENT = new int[LightUtils.MAX_SPOT_LIGHTS];

    protected int[] UNIFORM_SPOT_LIGHT_POS = new int[LightUtils.MAX_SPOT_LIGHTS];
    protected int[] UNIFORM_SPOT_LIGHT_DIRECTION = new int[LightUtils.MAX_SPOT_LIGHTS];
    protected int[] UNIFORM_SPOT_LIGHT_CUT_OFF = new int[LightUtils.MAX_SPOT_LIGHTS];
    protected int[] UNIFORM_SPOT_LIGHT_ATT_EXP = new int[LightUtils.MAX_SPOT_LIGHTS];

    private float shadowBias = 1f/255f;

    @Override
    public void init(ShaderProgram program, Renderable renderable) {

        // Register point light uniform array
        for (int i = 0; i < LightUtils.MAX_POINT_LIGHTS; i++) {
            UNIFORM_POINT_LIGHT_COLOR[i] = register(new Uniform("u_pointLights["+ i +"].Base.Color"));
            UNIFORM_POINT_LIGHT_INTENSITY[i] = register(new Uniform("u_pointLights["+ i +"].Base.DiffuseIntensity"));
            UNIFORM_POINT_LIGHT_INTENSITY_AMBIENT[i] = register(new Uniform("u_pointLights["+ i +"].Base.AmbientIntensity"));

            UNIFORM_POINT_LIGHT_POS[i] = register(new Uniform("u_pointLights["+ i +"].LocalPos"));
        }

        // Register spotlight uniform array
        for (int i = 0; i < LightUtils.MAX_SPOT_LIGHTS; i++) {
            UNIFORM_SPOT_LIGHT_COLOR[i] = register(new Uniform("u_spotLights["+ i +"].Base.Base.Color"));
            UNIFORM_SPOT_LIGHT_INTENSITY[i] = register(new Uniform("u_spotLights["+ i +"].Base.Base.DiffuseIntensity"));
            UNIFORM_SPOT_LIGHT_INTENSITY_AMBIENT[i] = register(new Uniform("u_spotLights["+ i +"].Base.Base.AmbientIntensity"));

            UNIFORM_SPOT_LIGHT_POS[i] = register(new Uniform("u_spotLights["+ i +"].Base.LocalPos"));
            UNIFORM_SPOT_LIGHT_DIRECTION[i] = register(new Uniform("u_spotLights["+ i +"].Direction"));
            UNIFORM_SPOT_LIGHT_CUT_OFF[i] = register(new Uniform("u_spotLights["+ i +"].Cutoff"));
            UNIFORM_SPOT_LIGHT_ATT_EXP[i] = register(new Uniform("u_spotLights["+ i +"].Exponent"));
        }

        super.init(program, renderable);
    }

    protected void setLights(MundusEnvironment env) {
        DirectionalLightEx dirLight = LightUtils.getDirectionalLight(env);
        ColorAttribute ambientLight = (ColorAttribute) env.get(ColorAttribute.AmbientLight);
        if (dirLight != null) {
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR, dirLight.baseColor.r, dirLight.baseColor.g, dirLight.baseColor.b);
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR_AMBIENT, ambientLight.color.r, ambientLight.color.g, ambientLight.color.b);
            set(UNIFORM_DIRECTIONAL_LIGHT_DIR, dirLight.direction);
            // A bit of a hack, water does not use PBR lighting and thus intensity is scaled down
            // as PBR shader tends to require higher intensity then the water light calcs
            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY, dirLight.intensity * 0.1f);
        }

        // point lights
        PointLightsAttribute attr = env.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> pointLights = attr == null ? null : attr.lights;
        if (pointLights != null && pointLights.size > 0) {
            set(UNIFORM_POINT_LIGHT_NUM_ACTIVE, pointLights.size);

            if (pointLights.size > LightUtils.MAX_POINT_LIGHTS) {
                throw new GdxRuntimeException("Too many point lights. Current: " + pointLights.size + " Max: " + LightUtils.MAX_POINT_LIGHTS);
            }

            for (int i = 0; i < pointLights.size; i++) {
                PointLight light = pointLights.get(i);

                set(UNIFORM_POINT_LIGHT_COLOR[i], light.color.r, light.color.g, light.color.b);
                set(UNIFORM_POINT_LIGHT_POS[i], light.position);
                set(UNIFORM_POINT_LIGHT_INTENSITY[i], light.intensity);
            }
        } else {
            set(UNIFORM_POINT_LIGHT_NUM_ACTIVE, 0);
        }

        // spotlights
        SpotLightsAttribute spotAttr = env.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        final Array<SpotLight> spotLights = spotAttr == null ? null : spotAttr.lights;
        if (spotLights != null && spotLights.size > 0) {
            set(UNIFORM_SPOT_LIGHT_NUM_ACTIVE, spotLights.size);

            if (spotLights.size > LightUtils.MAX_SPOT_LIGHTS) {
                throw new GdxRuntimeException("Too many spotlights. Current: " + spotLights.size + " Max: " + LightUtils.MAX_SPOT_LIGHTS);
            }

            for (int i = 0; i < spotLights.size; i++) {
                SpotLight light = spotLights.get(i);

                set(UNIFORM_SPOT_LIGHT_COLOR[i], light.color.r, light.color.g, light.color.b);
                set(UNIFORM_SPOT_LIGHT_POS[i], light.position);
                set(UNIFORM_SPOT_LIGHT_DIRECTION[i], light.direction);
                set(UNIFORM_SPOT_LIGHT_CUT_OFF[i], light.cutoffAngle);
                set(UNIFORM_SPOT_LIGHT_ATT_EXP[i], light.exponent);
                set(UNIFORM_SPOT_LIGHT_INTENSITY[i], light.intensity);
            }
        } else {
            set(UNIFORM_SPOT_LIGHT_NUM_ACTIVE, 0);
        }

    }

    protected void setShadows(MundusEnvironment env) {
        MundusDirectionalShadowLight dirLight = LightUtils.getDirectionalLight(env);
        if (dirLight == null) {
            set(UNIFORM_USE_SHADOWS, 0);
            return;
        }

        if (env.shadowMap != null) {
            set(UNIFORM_SHADOW_BIAS, shadowBias);
            set(UNIFORM_USE_SHADOWS, 1);
            set(UNIFORM_SHADOW_TEXTURE, env.shadowMap.getDepthMap());
            set(UNIFORM_SHADOW_VIEW, env.shadowMap.getProjViewTrans());
            set(UNIFORM_SHADOW_PCF_OFFSET,  1.f / (2f * env.shadowMap.getDepthMap().texture.getWidth()));
        }
    }
}
