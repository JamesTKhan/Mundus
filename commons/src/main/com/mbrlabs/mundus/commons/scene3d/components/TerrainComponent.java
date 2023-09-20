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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.TerrainLayerAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 18-01-2016
 */
public class TerrainComponent extends CullableComponent implements AssetUsage, RenderableComponent {

    private static final String TAG = TerrainComponent.class.getSimpleName();

    // Array of lod models per terrain
    protected ModelInstance currentInstance;
    private ModelInstance[] modelInstances;
    protected TerrainAsset terrainAsset;
    protected Terrain terrain;

    // Neighbor terrain components
    private TerrainComponent topNeighbor;
    private TerrainComponent rightNeighbor;
    private TerrainComponent bottomNeighbor;
    private TerrainComponent leftNeighbor;

    // Bounding box to determine what LOD to render
    private static final BoundingBox bb = new BoundingBox();

    private int previousLod = 0;

    public TerrainComponent(GameObject go) {
        super(go);
        type = Component.Type.TERRAIN;
    }

    @Override
    public void update(float delta) {
        if (!terrainAsset.getTerrain().terraformed) {
            return;
        }

        super.update(delta);
        if (gameObject.isDirty()) {
            currentInstance.transform.set(gameObject.getTransform());
        }

        int newLod = calculateLodLevel();
        if (newLod != terrain.currentLod){
            previousLod = terrain.currentLod;
            terrain.currentLod = newLod;
            if (modelInstances[newLod] == null){
                modelInstances[newLod] = new ModelInstance(terrain.getModel(newLod));
                modelInstances[newLod].transform.set(gameObject.getTransform());
            }
            currentInstance = modelInstances[newLod];
            applyMaterial();
        }
    }
    private int calculateLodLevel(){

        int camWidth = Gdx.graphics.getWidth();
        int camHeight = Gdx.graphics.getHeight();
        int lodLevels = terrainAsset.getTerrain().lodLevels;
        int lodLevel = previousLod;

        //we need to use the bounding box of the previous lod level to prevent stuttering at LOD thresholds
        modelInstances[previousLod].calculateBoundingBox(bb);

        //have to get all corners, cant use bb.min and max because of rotation
        Vector3[] corners = new Vector3[8];
        for (int i = 0; i < 8; i++) {
            corners[i] = new Vector3();
        }
        bb.getCorner000(corners[0]);
        bb.getCorner001(corners[1]);
        bb.getCorner010(corners[2]);
        bb.getCorner011(corners[3]);
        bb.getCorner100(corners[4]);
        bb.getCorner101(corners[5]);
        bb.getCorner110(corners[6]);
        bb.getCorner111(corners[7]);

        Matrix4 combinedMatrix = gameObject.sceneGraph.scene.cam.combined;

        Vector2 minScreenCoord = new Vector2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Vector2 maxScreenCoord = new Vector2(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        for (Vector3 corner : corners) {
            corner.prj(combinedMatrix);
            minScreenCoord.x = Math.min(minScreenCoord.x, corner.x);
            minScreenCoord.y = Math.min(minScreenCoord.y, corner.y);
            maxScreenCoord.x = Math.max(maxScreenCoord.x, corner.x);
            maxScreenCoord.y = Math.max(maxScreenCoord.y, corner.y);
        }

        float width = Math.min((maxScreenCoord.x - minScreenCoord.x) * 0.5f * camWidth, camWidth);
        float height =Math.min((maxScreenCoord.y - minScreenCoord.y) * 0.5f * camHeight, camHeight);

        float widthRatio = width / camWidth;
        float heightRatio = height / camHeight;

        for (int i = lodLevels - 1; i >= 0; i--) {
            //we need to check these separately in case we are close to the x or z horizon
            if (widthRatio > terrain.thresholds[i])
                lodLevel = i;
            else if (heightRatio > terrain.thresholds[i])
                lodLevel = i;
        }

        return lodLevel;
    }

    @Override
    public RenderableProvider getRenderableProvider() {
        return currentInstance;
    }

    public void updateUVs(Vector2 uvScale) {
        terrainAsset.updateUvScale(uvScale);
    }

    public void setTerrainAsset(TerrainAsset terrainAsset) {
        this.terrainAsset = terrainAsset;
        this.terrain = terrainAsset.getTerrain();
        modelInstances = new ModelInstance[terrain.lodLevels];
        //this is called before terraforming so the model is initially flat
        modelInstances[0] = new ModelInstance(terrain.getModel(0));
        modelInstances[0].transform = gameObject.getTransform();
        currentInstance = modelInstances[0];
        applyMaterial();
        setDimensions(modelInstances[0]);


    }

    public void applyMaterial() {
        if (terrainAsset.getMaterialAsset() == null) return;

        Material material = currentInstance.materials.first();

        // Apply base textures to this instances material because we use base color/normal for splat base
        final TerrainLayerAsset terrainLayerAsset = terrainAsset.getTerrainLayerAsset();
        material.set(PBRTextureAttribute.createBaseColorTexture(terrainLayerAsset.getSplatBase().getTexture()));
        if (terrainLayerAsset.getSplatBaseNormal() != null)
            material.set(PBRTextureAttribute.createNormalTexture(terrainLayerAsset.getSplatBaseNormal().getTexture()));
        else
            material.remove(PBRTextureAttribute.NormalTexture);

        terrainAsset.getMaterialAsset().applyToMaterial(material, true);
    }

    public TerrainAsset getTerrainAsset() {
        return terrainAsset;
    }

    public TerrainComponent getTopNeighbor() {
        return topNeighbor;
    }

    public void setTopNeighbor(final TerrainComponent topNeighbor) {
        this.topNeighbor = topNeighbor;
    }

    public TerrainComponent getRightNeighbor() {
        return rightNeighbor;
    }

    public void setRightNeighbor(final TerrainComponent rightNeighbor) {
        this.rightNeighbor = rightNeighbor;
    }

    public TerrainComponent getBottomNeighbor() {
        return bottomNeighbor;
    }

    public void setBottomNeighbor(final TerrainComponent bottomNeighbor) {
        this.bottomNeighbor = bottomNeighbor;
    }

    public TerrainComponent getLeftNeighbor() {
        return leftNeighbor;
    }

    public void setLeftNeighbor(final TerrainComponent leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    /**
     * Retrieves immediate neighbors of this terrain (TOP, RIGHT, BOTTOM, LEFT).
     * If a neighbor is null, it is not added to the array.
     *
     * @param out array to store neighbors in
     * @return array with neighbors
     */
    public Array<TerrainComponent> getNeighbors(Array<TerrainComponent> out) {
        if (topNeighbor != null) out.add(topNeighbor);
        if (rightNeighbor != null) out.add(rightNeighbor);
        if (bottomNeighbor != null) out.add(bottomNeighbor);
        if (leftNeighbor != null) out.add(leftNeighbor);
        return out;
    }

    @Override
    public Component clone(GameObject go) {
        TerrainComponent terrainComponent = new TerrainComponent(go);
        terrainComponent.setTerrainAsset(terrainAsset);
        return terrainComponent;
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
        return currentInstance;
    }

    /**
     * Returns the terrain height at the given world coordinates, in world coordinates.
     *
     * @param worldX X world position to get height
     * @param worldZ Z world position to get height
     * @return float height value
     */
    public float getHeightAtWorldCoord(float worldX, float worldZ) {
        return terrainAsset.getTerrain().getHeightAtWorldCoord(worldX, worldZ, currentInstance.transform);
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
        return terrainAsset.getTerrain().getNormalAtWordCoordinate(out, worldX, worldZ, currentInstance.transform);
    }

    /**
     * Casts the given ray to determine where it intersects on the terrain.
     *
     * @param out Vector3 to populate with intersect point with
     * @param ray the ray to cast
     * @return The out Vector3 which contains the intersect point.
     */
    public Vector3 getRayIntersection(Vector3 out, Ray ray) {
        terrainAsset.getTerrain().getRayIntersection(out, ray, currentInstance.transform);
        return out;
    }

    /**
     * Determines if the world coordinates are within the terrains X and Z boundaries, does not including height
     * @param worldX worldX to check
     * @param worldZ worldZ to check
     * @return boolean true if within the terrains boundary, else false
     */
    public boolean isOnTerrain(float worldX, float worldZ) {
        return terrainAsset.getTerrain().isOnTerrain(worldX, worldZ, currentInstance.transform);
    }
}
