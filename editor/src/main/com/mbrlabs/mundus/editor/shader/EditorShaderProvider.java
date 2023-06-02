package com.mbrlabs.mundus.editor.shader;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.mbrlabs.mundus.commons.shaders.MundusPBRShaderProvider;
import com.mbrlabs.mundus.commons.terrain.TerrainMaterial;
import com.mbrlabs.mundus.commons.terrain.attributes.TerrainMaterialAttribute;
import net.mgsx.gltf.scene3d.shaders.PBRShader;
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
    protected PBRShader createPBRTerrainShader(Renderable renderable, PBRShaderConfig config, String prefix) {
        TerrainMaterialAttribute terrainMaterialA = (TerrainMaterialAttribute) renderable.material.get(TerrainMaterialAttribute.TerrainMaterial);
        TerrainMaterial terrainMaterial = terrainMaterialA.terrainMaterial;

        prefix += "#define PICKER\n";
        prefix += getTerrainPrefix(terrainMaterial);

        return new EditorPBRTerrainShader(renderable, config, prefix);
    }
}
