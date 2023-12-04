package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.commons.assets.meta.Meta;

import java.util.Map;

public class TerrainObjectLayerAsset extends Asset {

    private final Json json;

    private final Array<String> idsToLoad;

    private final Array<ModelAsset> models;

    /**
     * @param meta the meta file
     * @param assetFile the asset file
     */
    public TerrainObjectLayerAsset(Meta meta, FileHandle assetFile, Json json) {
        super(meta, assetFile);
        this.json = json;
        idsToLoad = new Array<>(5);
        models = new Array<>(5);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() {
        final Array<String> ids = json.fromJson(Array.class, file);
        idsToLoad.addAll(ids);
    }

    @Override
    public void load(AssetManager assetManager) {
        // No async loading for terrain object layer
        load();
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        for (int i = 0; i < idsToLoad.size; ++i) {
            final String id = idsToLoad.get(i);
            models.add((ModelAsset) assets.get(id));
        }

        idsToLoad.clear();
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
        return idsToLoad.contains(assetToCheck.getID(), false);
    }

    public void addModel(final ModelAsset model) {
        models.add(model);
    }

    public Array<ModelAsset> getModels() {
        return models;
    }
}
