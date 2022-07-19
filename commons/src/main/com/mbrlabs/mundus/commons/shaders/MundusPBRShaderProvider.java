package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import net.mgsx.gltf.scene3d.shaders.PBRShader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

/**
 * @author JamesTKhan
 * @version July 01, 2022
 */
public class MundusPBRShaderProvider extends PBRShaderProvider {

    public MundusPBRShaderProvider(PBRShaderConfig config) {
        super(config);
    }

    @Override
    protected PBRShader createShader(Renderable renderable, PBRShaderConfig config, String prefix){
        return new MundusPBRShader(renderable, config, prefix);
    }
}
