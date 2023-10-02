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

package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 18-01-2016
 */
public class TerrainComponent extends CullableComponent implements AssetUsage, RenderableComponent {

    private static final String TAG = TerrainComponent.class.getSimpleName();

    protected ModelInstance modelInstance;
    protected TerrainAsset terrainAsset;

    public TerrainComponent(GameObject go) {
        super(go);
        type = Component.Type.TERRAIN;
    }

    @Override
    public RenderableProvider getRenderableProvider() {
        return modelInstance;
    }

    public void updateUVs(Vector2 uvScale) {
        terrainAsset.updateUvScale(uvScale);
    }

    public void setTerrainAsset(TerrainAsset terrainAsset) {
        this.terrainAsset = terrainAsset;
        modelInstance = new ModelInstance(terrainAsset.getTerrain().getModel());
        modelInstance.transform = gameObject.getTransform();
        applyMaterial();
        setDimensions(modelInstance);
    }

    public void applyMaterial() {
        if (terrainAsset.getMaterialAsset() == null) return;

        Material material = modelInstance.materials.first();

        // Apply base textures to this instances material because we use base color/normal for splat base
        material.set(PBRTextureAttribute.createBaseColorTexture(terrainAsset.getSplatBase().getTexture()));
        if (terrainAsset.getSplatBaseNormal() != null)
            material.set(PBRTextureAttribute.createNormalTexture(terrainAsset.getSplatBaseNormal().getTexture()));

        terrainAsset.getMaterialAsset().applyToMaterial(material, true);
    }

    public TerrainAsset getTerrainAsset() {
        return terrainAsset;
    }

    @Override
    public Component clone(GameObject go) {
        throw new GdxRuntimeException("Duplicating terrains is not supported.");
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (Objects.equals(terrainAsset.getID(), assetToCheck.getID()))
            return true;

        if (assetToCheck == terrainAsset.getMaterialAsset()) {
            return true;
        }

        return terrainAsset.usesAsset(assetToCheck);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    /**
     * Returns the terrain height at the given world coordinates, in world coordinates.
     *
     * @param worldX X world position to get height
     * @param worldZ Z world position to get height
     * @return float height value
     */
    public float getHeightAtWorldCoord(float worldX, float worldZ) {
        return terrainAsset.getTerrain().getHeightAtWorldCoord(worldX, worldZ, modelInstance.transform);
    }

    /**
     * Get normal at world coordinates. The methods calculates exact point
     * position in terrain coordinates and returns normal at that point. If
     * point doesn't belong to terrain -- it returns default
     * <code>Vector.Y<code> normal.
     *
     * @param worldX
     *            the x coord in world
     * @param worldZ
     *            the z coord in world
     * @return normal at that point. If point doesn't belong to terrain -- it
     *         returns default <code>Vector.Y<code> normal.
     */
    public Vector3 getNormalAtWordCoordinate(Vector3 out, float worldX, float worldZ) {
        return terrainAsset.getTerrain().getNormalAtWordCoordinate(out, worldX, worldZ, modelInstance.transform);
    }

    /**
     * Casts the given ray to determine where it intersects on the terrain.
     *
     * @param out Vector3 to populate with intersect point with
     * @param ray the ray to cast
     * @return The out Vector3 which contains the intersect point.
     */
    public Vector3 getRayIntersection(Vector3 out, Ray ray) {
        return terrainAsset.getTerrain().getRayIntersection(out, ray, modelInstance.transform);
    }

    /**
     * Determines if the world coordinates are within the terrains X and Z boundaries, does not including height
     * @param worldX worldX to check
     * @param worldZ worldZ to check
     * @return boolean true if within the terrains boundary, else false
     */
    public boolean isOnTerrain(float worldX, float worldZ) {
        return terrainAsset.getTerrain().isOnTerrain(worldX, worldZ, modelInstance.transform);
    }
}
