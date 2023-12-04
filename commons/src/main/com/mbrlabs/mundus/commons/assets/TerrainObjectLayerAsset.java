package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.meta.Meta;

import java.util.Map;

public class TerrainObjectLayerAsset extends Asset {

    /**
     * @param meta the meta file
     * @param assetFile the asset file
     */
    public TerrainObjectLayerAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {

    }

    @Override
    public void load(AssetManager assetManager) {
        // No async loading for terrain objects
        load();
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {

    }

    @Override
    public void applyDependencies() {
        // Nothing to do here
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        return false;
    }
}
