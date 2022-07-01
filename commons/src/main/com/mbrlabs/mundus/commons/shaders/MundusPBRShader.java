package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

/**
 * @author James Pooley
 * @version July 01, 2022
 */
public class MundusPBRShader extends PBRShader {

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

        if (!(renderable.environment instanceof MundusEnvironment)) return;
        MundusEnvironment env = (MundusEnvironment) renderable.environment;
        Color color = env.getAmbientLight().color;
        float intensity = env.getAmbientLight().intensity;
        program.setUniformf(u_ambientLight, color.r * intensity* 2, color.g * intensity* 2, color.b * intensity* 2);
    }
}
