package com.mbrlabs.mundus.commons.dto;

import com.mbrlabs.mundus.commons.assets.Asset;

import java.util.Map;

/**
 * Returns True if the DTO object uses the assetToCheck.
 *
 * @author JamesTKhan
 * @version September 25, 2023
 */
public interface AssetUsageDTO {
    boolean usesAsset(Asset assetToCheck, Map<String, Asset> assetMap);
}
