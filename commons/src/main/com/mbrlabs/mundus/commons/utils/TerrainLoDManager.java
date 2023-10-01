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
    private static final Vector3 tmp = new Vector3();
    private static final Vector3 tmp2 = new Vector3();

    private int currentLodIndex = 0;
    private boolean lodDirty = false;
    private boolean enabled = true;

    private final TerrainComponent tc;

    public TerrainLoDManager(TerrainComponent terrainComponent) {
        this.tc = terrainComponent;
    }

    @Override
    public void update(float delta) {
        if (!enabled) return;
        if (tc.isCulled()) return;

        tmp.set(tc.gameObject.sceneGraph.scene.cam.position);
        tc.gameObject.getPosition(tmp2);

        // center of terrain
        tmp2.add(tc.getTerrainAsset().getTerrain().terrainWidth * 0.5f, 0, tc.getTerrainAsset().getTerrain().terrainDepth * 0.5f);
        float distance = tmp.dst(tmp2);

        int lodLevel = determineLodLevel(distance);
        if ((lodLevel != currentLodIndex || lodDirty) && tc.getTerrainAsset().hasLoD(lodLevel)) {
            currentLodIndex = lodLevel;
            lodDirty = false;
            setLoDLevel(lodLevel);
        }
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

    private int determineLodLevel(float distance) {
        float distanceThreshold = tc.getTerrainAsset().getTerrain().terrainWidth * 1.2f;
        for (int i = 0; i < Terrain.DEFAULT_LODS; i++) {
            if (distance < (i + 1) * distanceThreshold) {
                return i;
            }
        }
        return Terrain.DEFAULT_LODS - 1;  // If beyond all thresholds, consider it the furthest LOD level
    }

}
