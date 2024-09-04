package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

/**
 * @author JamesTKhan
 * @version July 01, 2022
 */
public class MundusPBRShader extends PBRShader {

    private final int u_clipPlane = register("u_clipPlane");

    protected final int UNIFORM_POINT_LIGHT_NUM_ACTIVE = register(new Uniform("u_activeNumPointLights"));

    protected final int UNIFORM_SPOT_LIGHT_NUM_ACTIVE = register(new Uniform("u_activeNumSpotLights"));

    public MundusPBRShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);
    }

    @Override
    public void init(ShaderProgram program, Renderable renderable) {
        super.init(program, renderable);
    }

    @Override
    protected void bindLights(Renderable renderable, Attributes attributes) {
        MundusEnvironment env = (MundusEnvironment) renderable.environment;

        // set shadows
        if (env.shadowMap == null) {
            // Clear the shadow texture
            set(u_shadowTexture, 0);
        }

        // Set clipping plane
        Vector3 clippingPlane = env.getClippingPlane();
        set(u_clipPlane, clippingPlane.x, clippingPlane.y, clippingPlane.z, env.getClippingHeight());

        //
        PointLightsAttribute attr = env.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> pointLights = attr == null ? null : attr.lights;
        if (pointLights != null && pointLights.size > 0) {
            set(UNIFORM_POINT_LIGHT_NUM_ACTIVE, pointLights.size);
        } else {
            set(UNIFORM_POINT_LIGHT_NUM_ACTIVE, 0);
        }

        SpotLightsAttribute spotAttr = env.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        final Array<SpotLight> spotLights = spotAttr == null ? null : spotAttr.lights;

        if (spotLights != null && spotLights.size > 0) {
            set(UNIFORM_SPOT_LIGHT_NUM_ACTIVE, spotLights.size);
        } else {
            set(UNIFORM_SPOT_LIGHT_NUM_ACTIVE, 0);
        }

        super.bindLights(renderable, attributes);
    }
}
