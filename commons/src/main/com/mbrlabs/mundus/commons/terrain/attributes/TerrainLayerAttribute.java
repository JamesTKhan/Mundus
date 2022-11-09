/*
 * Copyright (c) 2022. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.terrain.attributes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.MundusAttribute;
import com.mbrlabs.mundus.commons.terrain.layers.TerrainLayer;

/**
 * @author JamesTKhan
 * @version October 16, 2022
 */
public class TerrainLayerAttribute extends MundusAttribute {
    public final static String HeightLayerAlias = "heightLayer";
    public final static long HeightLayer = register(HeightLayerAlias);

    public final static String SlopeLayerAlias = "slopeLayer";
    public final static long SlopeLayer = register(SlopeLayerAlias);

    protected static long Mask = HeightLayer | SlopeLayer;

    public final static boolean is (final long mask) {
        return (mask & Mask) != 0;
    }

    public final Array<TerrainLayer> terrainLayers;

    public TerrainLayerAttribute(final long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        terrainLayers = new Array<>();
    }

    public <T extends Texture> TerrainLayerAttribute(final long type, Array<TerrainLayer> terrainLayers) {
        this(type);
        this.terrainLayers.addAll(terrainLayers);
    }

    public TerrainLayerAttribute(final TerrainLayerAttribute copyFrom) {
        this(copyFrom.type, copyFrom.terrainLayers);
    }


    @Override
    public MundusAttribute copy () {
        return new TerrainLayerAttribute(this);
    }

    @Override
    public int hashCode () {
        int result = super.hashCode();
        result = 991 * result + terrainLayers.hashCode();
        return result;
    }

    @Override
    public int compareTo (MundusAttribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        TerrainLayerAttribute other = (TerrainLayerAttribute)o;
        return terrainLayers.equals(other.terrainLayers) ? 0 : -1;
    }
}

