/*
 * Copyright (c) 2021. See AUTHORS file.
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

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.mbrlabs.mundus.commons.utils.FileFormatUtils
import com.mbrlabs.mundus.editor.Mundus
import net.mgsx.gltf.data.GLTF
import net.mgsx.gltf.data.texture.GLTFTextureInfo
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute

class FileHandleWithDependencies(val file: FileHandle, val dependencies: ArrayList<FileHandle> = ArrayList()) {
    // Holds all the images parsed from the GLTF file,
    var images = Array<FileHandle>()

    // Links Materials to Textures(Images) <materialName, <PBRTextureAttribute, imagesArrayIndex>>
    var materialImageMap : HashMap<String, HashMap<Long, Int>> = HashMap()

    init {
        if (FileFormatUtils.isGLTF(file)) {
            loadGLTFDependencies()
        }
    }

    fun exists(): Boolean = file.exists()

    fun name(): String = file.name()

    fun copyTo(dest: FileHandle) {
        file.copyTo(dest)

        dependencies.forEach { it.copyTo(dest) }
    }

    private fun loadGLTFDependencies() {
        val json = Mundus.inject<Json>()

        val dto = json.fromJson(GLTF::class.java, file)

        dto.materials?.forEach {
            val attributeImageIndexMap = HashMap<Long, Int>()

            linkTextureToImage(dto, attributeImageIndexMap, it.pbrMetallicRoughness.baseColorTexture, PBRTextureAttribute.BaseColorTexture)
            linkTextureToImage(dto, attributeImageIndexMap, it.normalTexture, PBRTextureAttribute.NormalTexture)
            linkTextureToImage(dto, attributeImageIndexMap, it.emissiveTexture, PBRTextureAttribute.EmissiveTexture)
            linkTextureToImage(dto, attributeImageIndexMap, it.pbrMetallicRoughness.metallicRoughnessTexture, PBRTextureAttribute.MetallicRoughnessTexture)
            linkTextureToImage(dto, attributeImageIndexMap, it.occlusionTexture, PBRTextureAttribute.OcclusionTexture)

            materialImageMap[it.name] = attributeImageIndexMap
        }

        dto.images?.forEach {
            val depFile = FileHandle(file.parent().path() + '/' + it.uri)
            if (depFile.exists()) {
                images.add(depFile)
                dependencies.add(depFile)
            }
        }

        dto.buffers.forEach {
            val depFile = FileHandle(file.parent().path() + '/' + it.uri)
            if (depFile.exists()) {
                dependencies.add(depFile)
            }
        }
    }

    /**
     * Puts into a Hashmap the corresponding Image array index for the given attribute.
     * GLTF Materials with textures hold reference to a Texture index. The Texture holds reference
     * to an image index (source). The purpose is to allow auto-importing of textures as texture assets
     */
    private fun linkTextureToImage(
        dto: GLTF,
        map: HashMap<Long, Int>,
        textureInfo: GLTFTextureInfo?,
        attribute: Long
    ) {
        if (textureInfo == null) return

        // Get the image index based on the texture source value
        val imageIndex = dto.textures.get(textureInfo.index).source

        // Assign the given attribute with the image index
        map[attribute] = imageIndex
    }
}
