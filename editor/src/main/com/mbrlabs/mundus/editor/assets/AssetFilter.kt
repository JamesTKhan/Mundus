/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.assets

import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.assets.MaterialAsset
import com.mbrlabs.mundus.commons.assets.ModelAsset
import com.mbrlabs.mundus.commons.assets.TerrainLayerAsset
import com.mbrlabs.mundus.commons.assets.TerrainObjectLayerAsset
import com.mbrlabs.mundus.commons.assets.TextureAsset

/**
 * @author Marcus Brummer
 * @version 17-10-2016
 */
interface AssetFilter {
    fun ignore(asset: Asset): Boolean
}

/**
 * @author Marcus Brummer
 * @version 17-10-2016
 */
class AssetMaterialFilter : AssetFilter {
    override fun ignore(asset: Asset): Boolean {
        return asset !is MaterialAsset
    }
}

/**
 * @author Marcus Brummer
 * @version 07-10-2016
 */
class AssetTextureFilter : AssetFilter {
    override fun ignore(asset: Asset): Boolean {
        return asset !is TextureAsset
    }
}


class AssetTerrainLayerFilter : AssetFilter {
    override fun ignore(asset: Asset): Boolean {
        return asset !is TerrainLayerAsset
    }
}

class AssetModelFilter : AssetFilter {
    override fun ignore(asset: Asset): Boolean = asset !is ModelAsset
}

class AssetTerrainObjectLayerFilter : AssetFilter {
    override fun ignore(asset: Asset): Boolean = asset !is TerrainObjectLayerAsset
}
