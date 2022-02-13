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
import com.badlogic.gdx.utils.Json
import com.mbrlabs.mundus.commons.utils.FileFormatUtils
import com.mbrlabs.mundus.editor.Mundus
import net.mgsx.gltf.data.GLTF

class FileHandleWithDependencies(val file: FileHandle, val dependencies: ArrayList<FileHandle> = ArrayList()) {

    init {
        if (FileFormatUtils.isGLTF(file)) {
            loadGLTFDependencies()
        }
    }

    fun exists(): Boolean = file.exists()

    fun name(): String = file.name()

    fun copyTo(dest: FileHandle) {
        file.copyTo(dest);

        dependencies.forEach { it.copyTo(dest) }
    }

    private fun loadGLTFDependencies() {
        val json = Mundus.inject<Json>()

        val dto = json.fromJson(GLTF::class.java, file)
        dto.images?.forEach {
            val depFile = FileHandle(file.parent().path() + '/' + it.uri)
            if (depFile.exists()) {
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
}
