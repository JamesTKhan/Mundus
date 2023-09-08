package com.mbrlabs.mundus.editor.terrain;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.utils.Pools;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.events.TerrainVerticesChangedEvent;
import com.mbrlabs.mundus.editor.history.commands.TerrainStitchCommand;
import com.mbrlabs.mundus.editor.ui.UI;

/**
 * Utility class to stitch all terrains within a scene together based on their neighbors.
 * Adapted from <a href="https://gist.github.com/andrew-raphael-lukasik/d2903247b3f9c94e6faa9c042d9f0460">...</a>
 *
 * @author JamesTKhan
 * @version June 26, 2023
 */
public class TerrainStitcher {

    public static int numSteps = 10;

    public static boolean includeWorldHeight = false;

    private static final float threshold = 0.001f;

    private enum Direction {
        TOP, BOTTOM, LEFT, RIGHT
    }

    public static void stitch(ProjectContext projectContext) {
        Array<GameObject> terrainGOs = projectContext.currScene.sceneGraph.findAllByComponent(Component.Type.TERRAIN);
        Array<Terrain> terrains = new Array<>();
        Array<TerrainComponent> terrainComponents = new Array<>();

        for (GameObject go : terrainGOs) {
            TerrainComponent terrainComponent = (TerrainComponent) go.findComponentByType(Component.Type.TERRAIN);
            int length = terrainComponent.getTerrainAsset().getTerrain().vertexResolution;
            if (numSteps > length) {
                throw new IllegalArgumentException("Number of Steps must be less than the vertex resolution of the terrain (" + length + ")");
            }

            terrainComponents.add(terrainComponent);
            terrains.add(terrainComponent.getTerrainAsset().getTerrain());
        }

        TerrainStitchCommand command = new TerrainStitchCommand(terrains);
        int heightsStitched = stitch(terrainComponents);

        if (heightsStitched == 0) {
            return;
        }

        Mundus.INSTANCE.getCommandHistory().add(command);
        command.setHeightDataAfter();
        command.execute();

        for (TerrainComponent terrainComponent : terrainComponents) {
            projectContext.assetManager.addModifiedAsset(terrainComponent.getTerrainAsset());
            Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
        }
    }

    public static int stitch(Array<TerrainComponent> terrainComponents) {
        int neighbors = 0;
        int heightsStitched = 0;

        for (TerrainComponent terrainComponent : terrainComponents) {
            if (terrainComponent.getTopNeighbor() != null) {
                neighbors++;
                heightsStitched = stitchNeighbor(terrainComponent, terrainComponent.getTopNeighbor(), Direction.TOP);
            }

            if (terrainComponent.getBottomNeighbor() != null) {
                neighbors++;
                heightsStitched += stitchNeighbor(terrainComponent, terrainComponent.getBottomNeighbor(), Direction.BOTTOM);
            }

            if (terrainComponent.getLeftNeighbor() != null) {
                neighbors++;
                heightsStitched += stitchNeighbor(terrainComponent, terrainComponent.getLeftNeighbor(), Direction.LEFT);
            }

            if (terrainComponent.getRightNeighbor() != null) {
                neighbors++;
                heightsStitched += stitchNeighbor(terrainComponent, terrainComponent.getRightNeighbor(), Direction.RIGHT);
            }
        }

        if (neighbors == 0) {
            UI.INSTANCE.getToaster().error("No terrains had neighbors assigned.");
        } else if (heightsStitched == 0) {
            UI.INSTANCE.getToaster().info("No terrains needed stitching.");
        } else {
            UI.INSTANCE.getToaster().success("Stitched " + heightsStitched + " height values.");
        }

        return heightsStitched;
    }

    private static int stitchNeighbor(TerrainComponent terrainComponent, TerrainComponent neighbor, Direction direction) {
        int length = terrainComponent.getTerrainAsset().getTerrain().heightData.length;
        int neighborLength = neighbor.getTerrainAsset().getTerrain().heightData.length;
        int heightsStitched = 0;

        float currWH = getWorldHeight(terrainComponent);
        float neighborWH = getWorldHeight(neighbor);

        float[] heightMap = terrainComponent.getTerrainAsset().getTerrain().heightData;
        float[] neighborHeightMap = neighbor.getTerrainAsset().getTerrain().heightData;

        int stepFactor = length / neighborLength;

        switch (direction) {
            case TOP:
                for (int x = 0; x < length; x++) {
                    int idx = (length - 1) * length + x;
                    int neighborIdx = x;
                    heightsStitched += blendVertices(heightMap, neighborHeightMap, idx, neighborIdx, stepFactor, currWH, neighborWH);
                }
                break;
            case BOTTOM:
                for (int x = 0; x < length; x++) {
                    int idx = x;
                    int neighborIdx = (neighborLength - 1) * neighborLength + x;
                    heightsStitched += blendVertices(heightMap, neighborHeightMap, idx, neighborIdx, stepFactor, currWH, neighborWH);
                }
                break;
            case LEFT:
                for (int y = 0; y < length; y++) {
                    int idx = y * length;
                    int neighborIdx = y * neighborLength + neighborLength - 1;
                    heightsStitched += blendVertices(heightMap, neighborHeightMap, idx, neighborIdx, stepFactor, currWH, neighborWH);
                }
                break;
            case RIGHT:
                for (int y = 0; y < length; y++) {
                    int idx = y * length + length - 1;
                    int neighborIdx = y * neighborLength;
                    heightsStitched += blendVertices(heightMap, neighborHeightMap, idx, neighborIdx, stepFactor, currWH, neighborWH);
                }
                break;
        }

        return heightsStitched;
    }

    private static int blendVertices(float[] mainHeightMap, float[] neighborHeightMap, int idx, int neighborIdx, int stepFactor, float currWH, float neighborWH) {
        float currHeight = mainHeightMap[idx] + (includeWorldHeight ? currWH : 0);
        float neighborHeight = neighborHeightMap[neighborIdx] + (includeWorldHeight ? neighborWH : 0);

        if (Math.abs(currHeight - neighborHeight) < threshold) {
            return 0;
        }

        if (stepFactor > 1) {
            for (int step = 0; step < stepFactor; step++) {
                int adjustmentIndex = idx - step;
                adjustVertexForStitching(mainHeightMap, neighborHeight, adjustmentIndex, currWH, includeWorldHeight, step);
            }
        } else {
            adjustVertexForStitching(mainHeightMap, neighborHeight, idx, currWH, includeWorldHeight, 0);
        }

        return 1;
    }

    private static float getWorldHeight(TerrainComponent terrainComponent) {
        Vector3 tmp = Pools.vector3Pool.obtain();
        float worldHeight = terrainComponent.getGameObject().getTransform().getTranslation(tmp).y;
        Pools.vector3Pool.free(tmp);
        return worldHeight;
    }

    private static void adjustVertexForStitching(float[] mainHeightMap, float neighborHeight, int idx, float wh, boolean includeWorldHeight, int step) {
        float targetHeight = mainHeightMap[idx] + (includeWorldHeight ? wh : 0);
        float height = MathUtils.lerp(neighborHeight, targetHeight, (float) step / (float) numSteps);
        mainHeightMap[idx] = height - (includeWorldHeight ? wh : 0);
    }
}
