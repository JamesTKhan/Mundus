package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.PointLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLightsAttribute;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

/**
 * @author JamesTKhan
 * @version July 01, 2022
 */
public class MundusPBRShader extends PBRShader {

    private final int u_clipPlane = register("u_clipPlane");

    private int u_ambientLight;
    private int u_dirLights0color;
    private int u_dirLights0direction;

    public MundusPBRShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);
    }

    @Override
    public void init(ShaderProgram program, Renderable renderable) {
        super.init(program, renderable);

        u_ambientLight = program.fetchUniformLocation("u_ambientLight", false);
        u_dirLights0color = program.fetchUniformLocation("u_dirLights[0].color", true);
        u_dirLights0direction = program.fetchUniformLocation("u_dirLights[0].direction", true);
    }

    @Override
    protected void bindLights(Renderable renderable, Attributes attributes) {
        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = renderable.environment.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights;
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first();
            program.setUniformf(u_dirLights0color, light.color.r * light.intensity * 2, light.color.g * light.intensity * 2,
                    light.color.b * light.intensity * 2);
            program.setUniformf(u_dirLights0direction, light.direction.x,
                    light.direction.y, light.direction.z);
        }

        // Point Lights
        PointLightsAttribute attr = renderable.environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> pointLights = attr == null ? null : attr.lights;
        if (pointLightsLoc >= 0 && pointLights != null) {
            for (int i = 0; i < pointLights.size; i++) {

                float intensityModifier = 100f;
                PointLight light = pointLights.get(i);
                int idx = pointLightsLoc + i * pointLightsSize;
                program.setUniformf(idx + pointLightsColorOffset, light.color.r * light.intensity * intensityModifier,
                        light.color.g * light.intensity * intensityModifier, light.color.b * light.intensity * intensityModifier);
                program.setUniformf(idx + pointLightsPositionOffset, light.position.x, light.position.y,
                        light.position.z);
                if (pointLightsIntensityOffset >= 0) program.setUniformf(idx + pointLightsIntensityOffset, light.intensity);
                if (pointLightsSize <= 0) break;
            }
        }

        // Spot lights
        SpotLightsAttribute spotAttr = renderable.environment.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        final Array<SpotLight> spotLights = spotAttr == null ? null : spotAttr.lights;
        if (spotLightsLoc >= 0 && spotLights != null) {
            for (int i = 0; i < spotLights.size; i++) {

                SpotLight spotLight = spotLights.get(i);
                float intensityModifier = 100f * spotLight.intensity;

                int idx = spotLightsLoc + i * spotLightsSize;
                program.setUniformf(idx + spotLightsColorOffset, spotLight.color.r * spotLight.intensity * intensityModifier,
                        spotLight.color.g * spotLight.intensity * intensityModifier, spotLight.color.b * spotLight.intensity * intensityModifier);
                program.setUniformf(idx + spotLightsPositionOffset, spotLight.position);
                program.setUniformf(idx + spotLightsDirectionOffset, spotLight.direction);
                program.setUniformf(idx + spotLightsCutoffAngleOffset, spotLight.cutoffAngle);
                program.setUniformf(idx + spotLightsExponentOffset, spotLight.exponent);
                if (spotLightsIntensityOffset >= 0)
                    program.setUniformf(idx + spotLightsIntensityOffset, spotLight.intensity);
                if (spotLightsSize <= 0) break;
            }
        }

        if (attributes.has(ColorAttribute.Fog)) {
            set(u_fogColor, ((ColorAttribute)attributes.get(ColorAttribute.Fog)).color);
        }

        if (!(renderable.environment instanceof MundusEnvironment)) return;

        MundusEnvironment env = (MundusEnvironment) renderable.environment;

        // set shadows
        if (env.shadowMap != null) {
            set(u_shadowMapProjViewTrans, env.shadowMap.getProjViewTrans());
            set(u_shadowTexture, env.shadowMap.getDepthMap());
            set(u_shadowPCFOffset, 1.f / (2f * env.shadowMap.getDepthMap().texture.getWidth()));
        }

        // Set clipping plane
        Vector3 clippingPlane = env.getClippingPlane();
        set(u_clipPlane, clippingPlane.x, clippingPlane.y, clippingPlane.z, env.getClippingHeight());

        Color color = env.getAmbientLight().color;
        float intensity = env.getAmbientLight().intensity;
        program.setUniformf(u_ambientLight, color.r * intensity * 4, color.g * intensity * 4, color.b * intensity * 4);
    }
}
