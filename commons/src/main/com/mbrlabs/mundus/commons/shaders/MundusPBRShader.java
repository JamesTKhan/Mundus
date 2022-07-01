package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

/**
 * @author James Pooley
 * @version July 01, 2022
 */
public class MundusPBRShader extends PBRShader {

    public MundusPBRShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);
    }

    @Override
    protected void bindLights(Renderable renderable, Attributes attributes) {
        //super.bindLights(renderable, attributes);

        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = renderable.environment.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights;
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first();
            int idx = dirLightsLoc + 0 * dirLightsSize;
            program.setUniformf(idx + dirLightsColorOffset, light.color.r * light.intensity, light.color.g * light.intensity,
                    light.color.b * light.intensity);
            program.setUniformf(idx + dirLightsDirectionOffset, light.direction.x,
                    light.direction.y, light.direction.z);
        }
    }
}
