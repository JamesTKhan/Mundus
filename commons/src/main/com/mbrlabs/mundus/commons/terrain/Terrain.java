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

package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.utils.Disposable;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class Terrain extends BaseTerrain implements Disposable {
    public static final int DEFAULT_SIZE = 1200;
    public static final int DEFAULT_VERTEX_RESOLUTION = 180;
    public static final int DEFAULT_UV_SCALE = 60;

    public float[] heightData;

    public Terrain(int size, float[] heightData) {
        super((int) Math.sqrt(heightData.length), size, size);
        this.heightData = heightData;
    }

    @Override
    protected float getHeight(int index) {
        return heightData[index];
    }
}
