package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

/**
 * @author James Pooley
 * @version July 01, 2022
 */
public class MundusPBRShader extends PBRShader {

    public MundusPBRShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);
    }
}
