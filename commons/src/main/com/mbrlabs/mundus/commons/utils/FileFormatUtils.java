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

package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.files.FileHandle;

public class FileFormatUtils {

    public static final String FORMAT_3D_G3DB = "g3db";
    public static final String FORMAT_3D_GLTF = "gltf";
    public static final String FORMAT_3D_GLB = "glb";

    public static boolean isG3DB(String filename) {
        return filename.toLowerCase().endsWith(FORMAT_3D_G3DB);
    }

    public static boolean isG3DB(FileHandle file) {
        return isG3DB(file.name());
    }

    public static boolean isGLTF(String filename) {
        return filename.toLowerCase().endsWith(FORMAT_3D_GLTF);
    }

    public static boolean isGLB(String filename) {
        return filename.toLowerCase().endsWith(FORMAT_3D_GLB);
    }

    public static boolean isGLTF(FileHandle file) {
        return isGLTF(file.name());
    }

    public static boolean isGLB(final FileHandle file) {
        return isGLB(file.name());
    }
}
