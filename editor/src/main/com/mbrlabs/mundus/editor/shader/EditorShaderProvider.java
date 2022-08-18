package com.mbrlabs.mundus.editor.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.mbrlabs.mundus.commons.shaders.MundusPBRShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;

/**
 * Overrides the regular provider to create an Editor version of the Terrain shader.
 *
 * @author JamesTKhan
 * @version August 16, 2022
 */
public class EditorShaderProvider extends MundusPBRShaderProvider {
    public EditorShaderProvider(PBRShaderConfig config) {
        super(config);
    }

    @Override
    protected Shader createTerrainShader(Renderable renderable) {
        Shader shader = new EditorTerrainUberShader(renderable, config);
        shaders.add(shader);
        Gdx.app.log(EditorShaderProvider.class.getSimpleName(), "Terrain Shader Compiled");
        return shader;
    }
}
