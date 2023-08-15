package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

/**
 * @author JamesTKhan
 * @version July 01, 2022
 */
public class MundusPBRShader extends PBRShader {

    private final int u_clipPlane = register("u_clipPlane");

    public MundusPBRShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);
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

        super.bindLights(renderable, attributes);
    }
}
