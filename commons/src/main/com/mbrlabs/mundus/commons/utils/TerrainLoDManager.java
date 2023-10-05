package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.terrain.Terrain;

/**
 * Manages LoD levels for a terrain component by checking distance from camera.
 *
 * @author JamesTKhan
 * @version September 30, 2023
 */
public class TerrainLoDManager implements LoDManager {
    private static final float UPDATE_INTERVAL = 0.5f;
    private int currentLodIndex = 0;
    private boolean lodDirty = false;
    private boolean enabled = true;
    private final TerrainComponent tc;
    private float timeSinceLastUpdate = 0f;

    public TerrainLoDManager(TerrainComponent terrainComponent) {
        this.tc = terrainComponent;
    }

    @Override
    public void update(float delta) {
        if (!enabled || tc.isCulled()) return;
        timeSinceLastUpdate += delta;

        if (timeSinceLastUpdate < UPDATE_INTERVAL) return;
        timeSinceLastUpdate = 0f;

        Vector3 terrainCenter = Pools.vector3Pool.obtain();
        Vector3 cameraPosition = Pools.vector3Pool.obtain();
        Vector3 scale = Pools.vector3Pool.obtain();

        // center of terrain
        tc.gameObject.getPosition(terrainCenter);
        tc.gameObject.getScale(scale);
        float terrainWidth = tc.getTerrainAsset().getTerrain().terrainWidth * scale.x;
        float terrainWidthHalved = terrainWidth * 0.5f;
        terrainCenter.add(terrainWidthHalved, 0, terrainWidthHalved);

        cameraPosition.set(tc.gameObject.sceneGraph.scene.cam.position);
        float distance = cameraPosition.dst(terrainCenter);

        int lodLevel = determineLodLevel(distance, terrainWidth);
        lodLevel = adjustLodBasedOnCamFar(distance, tc.gameObject.sceneGraph.scene.cam.far, lodLevel);

        if ((lodLevel != currentLodIndex || lodDirty) && tc.getTerrainAsset().hasLoD(lodLevel)) {
            currentLodIndex = lodLevel;
            lodDirty = false;
            setLoDLevel(lodLevel);
        }

        Pools.free(terrainCenter, cameraPosition, scale);
    }

    @Override
    public void markDirty() {
        lodDirty = true;
    }

    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;

        if (tc.getTerrainAsset().hasLoD(currentLodIndex)) {
            setLoDLevel(currentLodIndex);
        }
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;

        if (tc.getTerrainAsset().hasLoD(0)) {
            setLoDLevel(0);
        }
    }

    private void setLoDLevel(int lodLevel) {
        for (Node node : tc.getModelInstance().nodes) {
            MeshPart part = node.parts.get(0).meshPart;
            part.mesh = tc.getTerrainAsset().getLod(lodLevel).getLodMesh()[0];
            part.size = part.mesh.getNumIndices();
        }
    }

    private int determineLodLevel(float distance, float terrainWidth) {
        float distanceThreshold = terrainWidth * 1.2f;
        for (int i = 0; i < Terrain.DEFAULT_LODS; i++) {
            if (distance < (i + 1) * distanceThreshold) {
                return i;
            }
        }
        return Terrain.DEFAULT_LODS - 1;  // If beyond all thresholds, consider it the furthest LOD level
    }

    private int adjustLodBasedOnCamFar(float distance, float camFar, int currentLod) {
        float proximityToCamFar = distance / camFar;

        // If within the last 20% of cam.far, use the next LOD level
        if (proximityToCamFar > 0.8) {
            return Math.min(currentLod + 1, Terrain.DEFAULT_LODS - 1);
        }
        return currentLod;
    }


}
