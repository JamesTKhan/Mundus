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
import com.mbrlabs.mundus.editor.utils.Log;

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
        //need to use heightMaps because that is what we are keeping current in terrainComponent.update wrt mesh size
        int currentLength = terrainComponent.getTerrainAsset().getTerrain().heightData.length;
        int neighborLength = neighbor.getTerrainAsset().getTerrain().heightData.length;

        Log.debug("Current length: " + currentLength, "Neighbor length: " + neighborLength);

        int heightsStitched = 0;

        int width = (int) Math.sqrt(currentLength);
        int neighborWidth = (int) Math.sqrt(neighborLength);

        float currWH = getWorldHeight(terrainComponent);
        float neighborWH = getWorldHeight(neighbor);

        float[] currentHeightMap = terrainComponent.getTerrainAsset().getTerrain().heightData;
        float[] neighborHeightMap = neighbor.getTerrainAsset().getTerrain().heightData;

        float stepFactor = (float) neighborWidth / width;

        for (int i = 0; i < width; i++) {
            int currentIndex = adjustIndex(i, direction, width);
            int neighborIndex = adjustNeighborIndex(i, direction, neighborWidth);
            heightsStitched += blendVertices(currentHeightMap, neighborHeightMap, currentIndex, neighborIndex, stepFactor, currWH, neighborWH);
        }

        return heightsStitched;
    }

    private static int adjustIndex(int baseIndex, Direction direction, int width) {
        switch (direction){
            case TOP:
                return width * width - width + baseIndex;
            case BOTTOM:
                return baseIndex;
            case LEFT:
                return baseIndex * width;
            case RIGHT:
                return baseIndex * width + width - 1;
        }
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    private static int adjustNeighborIndex(int baseIndex, Direction direction, int neighborWidth) {
        switch (direction){
            case TOP:
                return baseIndex;
            case BOTTOM:
                return neighborWidth * neighborWidth - neighborWidth + baseIndex;
            case LEFT:
                return baseIndex * neighborWidth + neighborWidth -1;
            case RIGHT:
                return baseIndex * neighborWidth;
            }
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    private static int blendVertices(float[] currentHeightMap, float[] neighborHeightMap, int index, int neighborIndex, float stepFactor, float currWH, float neighborWH) {
        float currHeight = currentHeightMap[index] + (includeWorldHeight ? currWH : 0);
        float neighborHeight = neighborHeightMap[neighborIndex] + (includeWorldHeight ? neighborWH: 0);

        if (Math.abs(currHeight - neighborHeight) < threshold) {
            return 0; //vertices are close enough
        }

        if (stepFactor > 1) {
            // Main terrain has lower resolution than the neighbor
            float accumulatedHeight = 0;

            // Accumulate heights from the neighbor based on the step factor
            for (int step = 0; step < stepFactor; step++) {
                int adjustedNeighborIdx = neighborIndex + step;
                accumulatedHeight += neighborHeightMap[adjustedNeighborIdx] + (includeWorldHeight ? neighborWH : 0);
            }

            // Calculate the average height
            float averageNeighborHeight = accumulatedHeight / stepFactor;
            currentHeightMap[index] = MathUtils.lerp(currHeight, averageNeighborHeight, 0.5f);  // Blend 50-50 for simplicity

        } else if (stepFactor == 1) {
            // Main terrain and neighbor have the same resolution
            currentHeightMap[index] = MathUtils.lerp(currHeight, neighborHeight, 0.5f);  // Blend 50-50 for simplicity

        } else {
            // stepFactor < 1 means main terrain has a higher resolution than the neighbor

            float height = MathUtils.lerp(neighborHeightMap[neighborIndex] + (includeWorldHeight ? neighborWH : 0), currHeight, (float) index / (float) numSteps);
            currentHeightMap[index] = height;
        }
        return 1;
    }


    private static float getWorldHeight(TerrainComponent terrainComponent) {
        Vector3 tmp = Pools.vector3Pool.obtain();
        float worldHeight = terrainComponent.getGameObject().getTransform().getTranslation(tmp).y;
        Pools.vector3Pool.free(tmp);
        return worldHeight;
    }
}