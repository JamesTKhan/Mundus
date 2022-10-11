package com.mbrlabs.mundus.commons.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.mbrlabs.mundus.commons.terrain.attributes.TerrainTextureAttribute;
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
    protected Shader createShader(Renderable renderable) {
        if (renderable.material.has(TerrainTextureAttribute.ATTRIBUTE_SPLAT0))
            return createTerrainShader(renderable);

        return super.createShader(renderable);
    }

    @Override
    protected PBRShader createShader(Renderable renderable, PBRShaderConfig config, String prefix){
        return new MundusPBRShader(renderable, config, prefix);
    }

    protected Shader createTerrainShader(Renderable renderable) {
        Shader shader = new TerrainUberShader(renderable, config);
        shaders.add(shader);
        Gdx.app.log(MundusPBRShader.class.getSimpleName(), "Terrain Shader Compiled");
        return shader;
    }
}
