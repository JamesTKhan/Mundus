package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.PointLightsAttribute;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * Extend this shader and call setLights method to apply lighting uniforms.
 *
 * @author James Pooley
 * @version June 02, 2022
 */
public abstract class LightShader extends ClippableShader {
    // ============================ LIGHTS ============================
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR = register(new Uniform("gDirectionalLight.Base.Color"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR_AMBIENT = register(new Uniform("gDirectionalLight.Base.AmbientColor"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_DIR = register(new Uniform("gDirectionalLight.Direction"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY = register(new Uniform("gDirectionalLight.Base.DiffuseIntensity"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY_AMBIENT = register(new Uniform("gDirectionalLight.Base.AmbientIntensity"));

    protected final int UNIFORM_POINT_LIGHT_NUM = register(new Uniform("gNumPointLights"));

    protected int[] UNIFORM_POINT_LIGHT_COLOR = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_INTENSITY = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_INTENSITY_AMBIENT = new int[ShaderUtils.MAX_POINT_LIGHTS];

    protected int[] UNIFORM_POINT_LIGHT_POS = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_ATT_CONSTANT = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_ATT_LINEAR = new int[ShaderUtils.MAX_POINT_LIGHTS];
    protected int[] UNIFORM_POINT_LIGHT_ATT_EXP = new int[ShaderUtils.MAX_POINT_LIGHTS];

    protected float ambientDirectionalFactor = 1f;

    @Override
    public void init(ShaderProgram program, Renderable renderable) {
        for (int i = 0; i < ShaderUtils.MAX_POINT_LIGHTS; i++) {
            UNIFORM_POINT_LIGHT_COLOR[i] = register(new Uniform("gPointLights["+ i +"].Base.Color"));
            UNIFORM_POINT_LIGHT_INTENSITY[i] = register(new Uniform("gPointLights["+ i +"].Base.DiffuseIntensity"));
            UNIFORM_POINT_LIGHT_INTENSITY_AMBIENT[i] = register(new Uniform("gPointLights["+ i +"].Base.AmbientIntensity"));

            UNIFORM_POINT_LIGHT_POS[i] = register(new Uniform("gPointLights["+ i +"].LocalPos"));
            UNIFORM_POINT_LIGHT_ATT_CONSTANT[i] = register(new Uniform("gPointLights["+ i +"].Atten.Constant"));
            UNIFORM_POINT_LIGHT_ATT_LINEAR[i] = register(new Uniform("gPointLights["+ i +"].Atten.Linear"));
            UNIFORM_POINT_LIGHT_ATT_EXP[i] = register(new Uniform("gPointLights["+ i +"].Atten.Exp"));
        }

        super.init(program, renderable);
    }

    protected void setLights(MundusEnvironment env) {
        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights;
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first();
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR, light.color.r, light.color.g, light.color.b);
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR_AMBIENT, env.getAmbientLight().color.r, env.getAmbientLight().color.g, env.getAmbientLight().color.b);
            set(UNIFORM_DIRECTIONAL_LIGHT_DIR, light.direction);
            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY, light.intensity);
            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY_AMBIENT, env.getAmbientLight().intensity * ambientDirectionalFactor);
        }

        // point lights
        PointLightsAttribute attr = env.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> pointLights = attr == null ? null : attr.lights;
        if (pointLights != null && pointLights.size > 0) {
            set(UNIFORM_POINT_LIGHT_NUM, pointLights.size);

            for (int i = 0; i < pointLights.size; i++) {
                PointLight light = pointLights.get(i);

                set(UNIFORM_POINT_LIGHT_COLOR[i], light.color.r, light.color.g, light.color.b);
                set(UNIFORM_POINT_LIGHT_POS[i], light.position);
                set(UNIFORM_POINT_LIGHT_INTENSITY[i], light.intensity);

                set(UNIFORM_POINT_LIGHT_ATT_CONSTANT[i], light.attenuation.constant);
                set(UNIFORM_POINT_LIGHT_ATT_LINEAR[i], light.attenuation.linear);
                set(UNIFORM_POINT_LIGHT_ATT_EXP[i] , light.attenuation.exponential);
            }
        } else {
            set(UNIFORM_POINT_LIGHT_NUM, 0);
        }

    }
}
