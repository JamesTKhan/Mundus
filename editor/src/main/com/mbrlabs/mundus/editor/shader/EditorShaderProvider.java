package com.mbrlabs.mundus.editor.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.mbrlabs.mundus.commons.shaders.MundusPBRShaderProvider;
import com.mbrlabs.mundus.commons.shaders.WaterUberShader;
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

    /**
     * Invalidates shaders, currently only invalidates the terrain shader.
     * When invalidated this will force all instances of shader to be recompiled.
     * Useful in the editor for development purposes when editing shaders.
     */
    public void invalidateShaders() {
        for (int i = 0; i < shaders.size; i++) {
            Shader shader = shaders.get(i);

            if (shader instanceof EditorTerrainUberShader) {
                ((EditorTerrainUberShader)shader).invalid = true;
                shaders.removeIndex(i);
                shader.dispose();
            }
        }
    }

    @Override
    protected Shader createTerrainShader(Renderable renderable) {
        Shader shader = new EditorTerrainUberShader(renderable, config);
        shaders.add(shader);
        Gdx.app.log(EditorShaderProvider.class.getSimpleName(), "Terrain Shader Compiled");
        return shader;
    }
}
