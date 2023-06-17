/*
 * Copyright (c) 2023. See AUTHORS file.
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

import java.util.HashMap;
import java.util.Map;

/**
 * Preprocesses shader files and replaces all include directives with the
 * content of the included file.
 * gdx-gltf moved over to something a bit similar (ShaderParser) but it is not
 * in a released version yet. May be worth using the gdx-gltf ShaderParser instead
 * once it is available in a release
 *
 * @author JamesTKhan
 * @version May 08, 2023
 */
public class ShaderPreprocessor {
    public static boolean cacheEnabled = true;
    private static final String includeDirective = "#include";

    // Cache for already parsed files
    private static final Map<String, String> cache = new HashMap<>();

    /**
     * Parses the shader file and replaces all include directives with the
     * content of the included file.
     *
     * @param fileHandle The shader file
     * @return The complete shader file as string
     */
    public static String readShaderFile(FileHandle fileHandle) {
        String filePath = fileHandle.path();

        // Check if the file content is already in the cache
        if (cacheEnabled && cache.containsKey(filePath)) {
            return cache.get(filePath);
        }

        String fileContent = fileHandle.readString();

        StringBuilder stringBuffer = new StringBuilder();
        int cursor = 0; // Cursor to keep track of the current position in the file
        int includeIndex; // Index of the include directive

        // Iterate the file and find the include directives
        while ((includeIndex = fileContent.indexOf(includeDirective, cursor)) != -1) {

            // Append the content before the include directive
            stringBuffer.append(fileContent, cursor, includeIndex);

            int startQuote = fileContent.indexOf("\"", includeIndex);
            int endQuote = fileContent.indexOf("\"", startQuote + 1);

            // Get the included file path
            String includedFilePath = fileContent.substring(startQuote + 1, endQuote);

            // Recursive include
            String includedFileContent = readShaderFile(fileHandle.sibling(includedFilePath));

            // Append the included file content
            stringBuffer.append(includedFileContent);

            cursor = endQuote + 1;
        }

        // Appends the remaining content after the last include directive
        stringBuffer.append(fileContent.substring(cursor));

        String processedFileContent = stringBuffer.toString();
        if (cacheEnabled) {
            cache.put(filePath, processedFileContent);
        }
        return processedFileContent;
    }
}
