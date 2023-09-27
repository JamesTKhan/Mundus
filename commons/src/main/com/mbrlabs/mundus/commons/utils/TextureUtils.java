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

package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.Texture;

/**
 * @author Marcus Brummer
 * @version 05-12-2015
 */
public class TextureUtils {

    /**
     * Checks if the given filter is a mip map filter.
     * @param filter the filter
     * @return true if the filter is a mip map filter
     */
    public static boolean isMipMapFilter(Texture.TextureFilter filter) {
        return isMipMapFilter(filter.getGLEnum());
    }

    /**
     * Checks if the given filter is a mip map filter.
     * @param filter the OpenGL enum of the filter
     * @return true if the filter is a mip map filter
     */
    public static boolean isMipMapFilter(int filter) {
        return filter == Texture.TextureFilter.MipMapLinearLinear.getGLEnum() ||
                filter == Texture.TextureFilter.MipMapLinearNearest.getGLEnum() ||
                filter == Texture.TextureFilter.MipMapNearestLinear.getGLEnum() ||
                filter == Texture.TextureFilter.MipMapNearestNearest.getGLEnum();
    }

}
