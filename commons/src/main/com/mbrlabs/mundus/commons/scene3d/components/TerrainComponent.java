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

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.shaders.ClippableShader;
import com.mbrlabs.mundus.commons.shaders.TerrainUberShader;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 18-01-2016
 */
public class TerrainComponent extends CullableComponent implements AssetUsage, ClippableComponent {

    private static final String TAG = TerrainComponent.class.getSimpleName();

    protected ModelInstance modelInstance;
    protected TerrainAsset terrainAsset;
    protected Shader shader;

    public TerrainComponent(GameObject go, Shader shader) {
        super(go);
        this.shader = shader;
        type = Component.Type.TERRAIN;
    }

    public void updateUVs(Vector2 uvScale) {
        terrainAsset.updateUvScale(uvScale);
    }

    public void setTerrainAsset(TerrainAsset terrainAsset) {
        this.terrainAsset = terrainAsset;
        modelInstance = new ModelInstance(terrainAsset.getTerrain().getModel());
        modelInstance.transform = gameObject.getTransform();
        setDimensions(modelInstance);
    }

    public TerrainAsset getTerrainAsset() {
        return terrainAsset;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (isCulled) return;
        triggerBeforeRenderEvent();
        gameObject.sceneGraph.scene.batch.render(modelInstance, gameObject.sceneGraph.scene.environment);
    }

    @Override
    public void render(float delta, Vector3 clippingPlane, float clipHeight) {
        TerrainUberShader.terrainClippingHeight = clipHeight;
        TerrainUberShader.terrainClippingPlane.set(clippingPlane);
        render(delta);
    }

    @Override
    public void renderDepth(float delta, Vector3 clippingPlane, float clipHeight, Shader shader) {
        if (isCulled) return;

        if (shader instanceof ClippableShader) {
            ((ClippableShader) shader).setClippingPlane(clippingPlane);
            ((ClippableShader) shader).setClippingHeight(clipHeight);
        }

        triggerBeforeDepthRenderEvent();

        gameObject.sceneGraph.scene.depthBatch.render(modelInstance, gameObject.sceneGraph.scene.environment, shader);
    }

    @Override
    public Component clone(GameObject go) {
        throw new GdxRuntimeException("Duplicating terrains is not supported.");
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (Objects.equals(terrainAsset.getID(), assetToCheck.getID()))
            return true;

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
     * Determines if the world coordinates are within the terrains X and Z boundaries, does not including height
     * @param worldX worldX to check
     * @param worldZ worldZ to check
     * @return boolean true if within the terrains boundary, else false
     */
    public boolean isOnTerrain(float worldX, float worldZ) {
        return terrainAsset.getTerrain().isOnTerrain(worldX, worldZ, modelInstance.transform);
    }
}
