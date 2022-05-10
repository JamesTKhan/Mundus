package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.meta.Meta;

import java.util.Map;

public class SkyboxAsset extends Asset {

    public TextureAsset positiveX;
    public TextureAsset negativeX;
    public TextureAsset positiveY;
    public TextureAsset negativeY;
    public TextureAsset positiveZ;
    public TextureAsset negativeZ;

    public SkyboxAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {
        // nothing to load for now
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        if (assets.containsKey(meta.getMetaSkybox().getPositiveX())) {
            positiveX = (TextureAsset) assets.get(meta.getMetaSkybox().getPositiveX());
        }
        if (assets.containsKey(meta.getMetaSkybox().getNegativeX())) {
            negativeX = (TextureAsset) assets.get(meta.getMetaSkybox().getNegativeX());
        }
        if (assets.containsKey(meta.getMetaSkybox().getPositiveY())) {
            positiveY = (TextureAsset) assets.get(meta.getMetaSkybox().getPositiveY());
        }
        if (assets.containsKey(meta.getMetaSkybox().getNegativeY())) {
            negativeY = (TextureAsset) assets.get(meta.getMetaSkybox().getNegativeY());
        }
        if (assets.containsKey(meta.getMetaSkybox().getPositiveZ())) {
            positiveZ = (TextureAsset) assets.get(meta.getMetaSkybox().getPositiveZ());
        }
        if (assets.containsKey(meta.getMetaSkybox().getNegativeZ())) {
            negativeZ = (TextureAsset) assets.get(meta.getMetaSkybox().getNegativeZ());
        }
    }

    @Override
    public void applyDependencies() {

    }

    @Override
    public void dispose() {
        positiveX.dispose();
        negativeX.dispose();
        positiveY.dispose();
        negativeY.dispose();
        positiveZ.dispose();
        negativeZ.dispose();
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (assetToCheck == positiveX || assetToCheck == negativeX ||
                assetToCheck == positiveY || assetToCheck == negativeY ||
                assetToCheck == positiveZ || assetToCheck == negativeZ) {
            return true;
        }

        return false;
    }
}
