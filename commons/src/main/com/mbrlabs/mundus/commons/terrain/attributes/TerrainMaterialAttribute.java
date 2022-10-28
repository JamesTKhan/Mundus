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

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.mbrlabs.mundus.commons.terrain.TerrainMaterial;

/**
 * @author JamesTKhan
 * @version October 24, 2022
 */
public class TerrainMaterialAttribute extends Attribute {
    public final static String TerrainMaterialAlias = "terrainData";
    public final static long TerrainMaterial = register(TerrainMaterialAlias);

    protected static long Mask = TerrainMaterial;
    
    public TerrainMaterial terrainMaterial;

    /**
     * Method to check whether the specified type is a valid DoubleAttribute
     * type
     */
    public static Boolean is(final long type) {
        return (type & Mask) != 0;
    }

    protected TerrainMaterialAttribute(long type) {
        super(type);
    }

    public TerrainMaterialAttribute(long type, TerrainMaterial terrainMaterial) {
        super(type);
        this.terrainMaterial = terrainMaterial;
    }

    public static TerrainMaterialAttribute createTerrainMaterialAttribute(TerrainMaterial terrainMaterial) {
        return new TerrainMaterialAttribute(TerrainMaterial, terrainMaterial);
    }
    @Override
    public Attribute copy() {
        return new TerrainMaterialAttribute(this.type, terrainMaterial);
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        TerrainMaterialAttribute otherValue = ((TerrainMaterialAttribute) o);
        return terrainMaterial.equals(otherValue.terrainMaterial) ? 0 : -1;
    }
}