/*
 * Copyright (c) 2024. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.plugin

import com.badlogic.gdx.files.FileHandle
import com.mbrlabs.mundus.commons.assets.CustomAsset
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.assets.EditorAssetManager
import com.mbrlabs.mundus.pluginapi.manager.AssetManager

class AssetManagerImpl : AssetManager {

    private val assetManager: EditorAssetManager = Mundus.inject()

    override fun createNewAsset(file: FileHandle): CustomAsset {
        return assetManager.createCustomAsset(file)
    }
}
