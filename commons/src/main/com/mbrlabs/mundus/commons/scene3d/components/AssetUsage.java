package com.mbrlabs.mundus.commons.scene3d.components;

import com.mbrlabs.mundus.commons.assets.Asset;

public interface AssetUsage {
    /**
     * Returns true if the implementation utilizes the given asset.
     * Used for safely deleting assets by checking for usages of the asset first.
     *
     * Ex. should return true if material asset is passed to a model that is using that asset.
     * @param assetToCheck the asset to check usages on
     * @return true if the asset is used in the implementation, false if not
     */
    boolean usesAsset(Asset assetToCheck);
}
