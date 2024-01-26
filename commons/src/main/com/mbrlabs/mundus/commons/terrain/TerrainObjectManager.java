package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.assets.TerrainObjectLayerAsset;
import com.mbrlabs.mundus.commons.assets.TerrainObjectsAsset;

public interface TerrainObjectManager extends RenderableProvider {

    /**
     * Apply terrain object changes.
     * If there is removed terrain object in asset then removes it from rendering.
     * If there is new terrain object in asset then adds it to rendering.
     * Updates position of terrain objects.
     *
     * @param recreateAllObjects If true then removes all terrain objects and recreates again.
     */
    void apply(boolean recreateAllObjects, TerrainObjectsAsset terrainObjectsAsset, TerrainObjectLayerAsset terrainObjectLayerAsset, Matrix4 parentTransform);

}
