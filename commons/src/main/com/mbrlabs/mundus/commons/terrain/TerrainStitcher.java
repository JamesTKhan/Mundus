package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.utils.Pools;

/**
 * Utility class to stitch all terrains within a scene together based on their neighbors.
 * Adapted from <a href="https://gist.github.com/andrew-raphael-lukasik/d2903247b3f9c94e6faa9c042d9f0460">...</a>
 *
 * @author JamesTKhan
 * @version June 26, 2023
 */
public class TerrainStitcher {

    /** The number of steps to take when stitching, lower = sharper transitions */
    public static int numSteps = 10;

    /** Whether to include the GameObject world height when stitching */
    public static boolean includeWorldHeight = false;

    /** Float comparison threshold */
    private static final float threshold = 0.001f;

    public static boolean stitch(Array<TerrainComponent> terrainComponents, Pool<Vector3> pool) {
        int heightsStitched = 0;
        for (TerrainComponent terrainComponent : terrainComponents) {
            heightsStitched = stitchTopBottomNeighbors(terrainComponent);
        }

        for (TerrainComponent terrainComponent : terrainComponents) {
            heightsStitched += stitchLeftRightNeighbors(terrainComponent);
        }

        return heightsStitched > 0;
    }

    /**
     * Used as a post-processing step for terrains with neighbors. Smooths the normals along the edges of the terrain
     * to prevent visible seams between neighboring terrains.
     * Because terrains are stored as heightmaps, this must be done at runtime once all neighbors are loaded.
     * @return true if any normals were stitched, false if all normals were already seamless
     */
    public static boolean stitchNormals(TerrainComponent tc, Pool<Vector3> pool)
    {
        PlaneMesh top = tc.getTopNeighbor() != null ? tc.getTopNeighbor().getTerrainAsset().getTerrain().getPlaneMesh() : null;
        PlaneMesh left = tc.getLeftNeighbor() != null ? tc.getLeftNeighbor().getTerrainAsset().getTerrain().getPlaneMesh() : null;
        PlaneMesh bottom = tc.getBottomNeighbor() != null ? tc.getBottomNeighbor().getTerrainAsset().getTerrain().getPlaneMesh() : null;
        PlaneMesh right = tc.getRightNeighbor() != null ? tc.getRightNeighbor().getTerrainAsset().getTerrain().getPlaneMesh() : null;

        PlaneMesh mesh = tc.getTerrainAsset().getTerrain().getPlaneMesh();

        return mesh.stitchEdgeNormalsFromNeighbors(top, left, bottom, right, pool);
    }

    private static int stitchTopBottomNeighbors(TerrainComponent terrainComponent) {
        int length = terrainComponent.getTerrainAsset().getTerrain().vertexResolution;
        int last = length - 1;
        int heightsStitched = 0;

        float currWH = getWorldHeight(terrainComponent);

        TerrainComponent top = terrainComponent.getTopNeighbor();
        TerrainComponent bottom = terrainComponent.getBottomNeighbor();

        float[] heightMap = terrainComponent.getTerrainAsset().getTerrain().heightData;

        if (top != null) {
            float[] topHeightMap = top.getTerrainAsset().getTerrain().heightData;
            float topWH = getWorldHeight(top);

            for (int x = 0; x < length; x++) {
                float currHeight = heightMap[last * length + x] + (includeWorldHeight ? currWH : 0);
                float neighborHeight = topHeightMap[x] + (includeWorldHeight ? topWH : 0);

                if (currHeight + threshold >= neighborHeight) continue;

                float height = neighborHeight;
                for (int step = 0; step < numSteps; step++) {
                    int idx = (last - step) * length + x;
                    float targetHeight = heightMap[idx] + (includeWorldHeight ? currWH : 0);
                    height = MathUtils.lerp(height, targetHeight, (float) step / (float) numSteps);
                    heightMap[idx] = height - (includeWorldHeight ? currWH : 0);
                    heightsStitched++;
                }
            }
        }

        if (bottom != null) {
            float[] bottomHeightMap = bottom.getTerrainAsset().getTerrain().heightData;
            float bottomWH = getWorldHeight(bottom);

            for (int x = 0; x < length; x++) {
                float neighborHeight = bottomHeightMap[last * length + x] + (includeWorldHeight ? bottomWH : 0);
                float currHeight = heightMap[x] + (includeWorldHeight ? currWH : 0);

                if (currHeight + threshold >= neighborHeight) continue;

                float height = neighborHeight;
                for (int step = 0; step < numSteps; step++) {
                    int idx = step * length + x;
                    float targetHeight = heightMap[idx] + (includeWorldHeight ? currWH : 0);
                    height = MathUtils.lerp(height, targetHeight, (float) step / (float) numSteps);
                    heightMap[idx] = height - (includeWorldHeight ? currWH : 0);
                    heightsStitched++;
                }
            }
        }

        return heightsStitched;
    }

    private static int stitchLeftRightNeighbors(TerrainComponent terrainComponent) {
        int length = terrainComponent.getTerrainAsset().getTerrain().vertexResolution;
        int last = length - 1;
        int heightsStitched = 0;

        float currWH = getWorldHeight(terrainComponent);

        TerrainComponent left = terrainComponent.getLeftNeighbor();
        TerrainComponent right = terrainComponent.getRightNeighbor();

        float[] heightMap = terrainComponent.getTerrainAsset().getTerrain().heightData;

        if (left != null) {
            float[] leftHeightMap = left.getTerrainAsset().getTerrain().heightData;
            float leftWH = getWorldHeight(left);

            for (int z = 0; z < length; z++) {
                float currHeight = heightMap[z * length + last] + (includeWorldHeight ? currWH : 0);
                float neighborHeight = leftHeightMap[z * length] + (includeWorldHeight ? leftWH : 0);

                if (currHeight + threshold >= neighborHeight) continue;

                float height = neighborHeight;
                for (int step = 0; step < numSteps; step++) {
                    int idx = z * length + last - step;
                    float targetHeight = heightMap[idx] + (includeWorldHeight ? currWH : 0);
                    height = MathUtils.lerp(height, targetHeight, (float) step / (float) numSteps);
                    heightMap[idx] = height - (includeWorldHeight ? currWH : 0);
                    heightsStitched++;
                }
            }
        }

        if (right != null) {
            float[] rightHeightMap = right.getTerrainAsset().getTerrain().heightData;
            float rightWH = getWorldHeight(right);

            for (int z = 0; z < length; z++) {
                float neighborHeight = rightHeightMap[z * length + last] + (includeWorldHeight ? rightWH : 0);
                float currHeight = heightMap[z * length] + (includeWorldHeight ? currWH : 0);

                if (currHeight + threshold >= neighborHeight) continue;

                float height = neighborHeight;
                for (int step = 0; step < numSteps; step++) {
                    int idx = z * length + step;
                    float targetHeight = heightMap[idx] + (includeWorldHeight ? currWH : 0);
                    height = MathUtils.lerp(height, targetHeight, (float) step / (float) numSteps);
                    heightMap[idx] = height - (includeWorldHeight ? currWH : 0);
                    heightsStitched++;
                }
            }
        }

        return heightsStitched;
    }

    private static float getWorldHeight(TerrainComponent terrainComponent) {
        Vector3 tmp = Pools.vector3Pool.obtain();
        float worldHeight = terrainComponent.getGameObject().getTransform().getTranslation(tmp).y;
        Pools.vector3Pool.free(tmp);
        return worldHeight;
    }

}
