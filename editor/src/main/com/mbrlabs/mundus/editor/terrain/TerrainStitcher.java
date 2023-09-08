package com.mbrlabs.mundus.editor.terrain;

import com.badlogic.gdx.Gdx;
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
        //need to use heightMaps because that is what we are keeping current in terrainComponent.update wrt mesh size
        int currentLength = terrainComponent.getTerrainAsset().getTerrain().heightData.length;
        int neighborLength = neighbor.getTerrainAsset().getTerrain().heightData.length;

        Gdx.app.log("Current length: " + currentLength, "Neighbor length: " + neighborLength);
        int heightsStitched = 0;

        float currWH = getWorldHeight(terrainComponent);
        float neighborWH = getWorldHeight(neighbor);

        float[] currentHeightMap = terrainComponent.getTerrainAsset().getTerrain().heightData;
        float[] neighborHeightMap = neighbor.getTerrainAsset().getTerrain().heightData;

        float stepFactor = neighborLength / currentLength;

        for (int i = 0; i < currentLength; i++) {
            int currentIndex = adjustIndex(i, direction, stepFactor, currentLength);
            int neighborIndex = adjustNeighborIndex(i, direction, stepFactor, neighborLength);
            heightsStitched += blendVertices(currentHeightMap, neighborHeightMap, currentIndex, neighborIndex, stepFactor, currWH, neighborWH);
        }

        return heightsStitched;
    }

    private static int blendVertices(float[] currentHeightMap, float[] neighborHeightMap, int index, int neighborIndex, float stepFactor, float currWH, float neighborWH) {
        float currHeight = currentHeightMap[index] + (includeWorldHeight ? currWH : 0);
        float neighborHeight = neighborHeightMap[neighborIndex] + (includeWorldHeight ? neighborWH: 0);

        if (Math.abs(currHeight - neighborHeight) < threshold) {
            return 0; //vertices are close enough
        }

        if (stepFactor > 1) {
            // Main terrain has same or lower resolution than the neighbor
            float accumulatedHeight = 0;

            // Accumulate heights from the neighbor based on the step factor
            for (int step = 0; step < stepFactor; step++) {
                int adjustedNeighborIdx = neighborIndex + step;
                accumulatedHeight += neighborHeightMap[adjustedNeighborIdx] + (includeWorldHeight ? neighborWH : 0);
            }

            // Calculate the average height
            float averageNeighborHeight = accumulatedHeight / stepFactor;
            float height = MathUtils.lerp(currHeight, averageNeighborHeight, 0.5f);  // Blend 50-50 for simplicity
            currentHeightMap[index] = height - (includeWorldHeight ? currWH : 0);

        } else if (stepFactor == 1) {
            // Main terrain and neighbor have the same resolution
            float neighborHeight = neighborHeightMap[neighborIndex] + (includeWorldHeight ? neighborWH : 0);
            float height = MathUtils.lerp(currHeight, neighborHeight, 0.5f);  // Blend 50-50 for simplicity
            currentHeightMap[index] = height - (includeWorldHeight ? currWH : 0);

        } else {
            // stepFactor < 1 means main terrain has a higher resolution than the neighbor

            float height = MathUtils.lerp(neighborHeightMap[neighborIndex] + (includeWorldHeight ? neighborWH : 0), currHeight, (float) index / (float) numSteps);
            currentHeightMap[index] = height - (includeWorldHeight ? currWH : 0);
        }

        return 1;
    }


    private static float getWorldHeight(TerrainComponent terrainComponent) {
        Vector3 tmp = Pools.vector3Pool.obtain();
        float worldHeight = terrainComponent.getGameObject().getTransform().getTranslation(tmp).y;
        Pools.vector3Pool.free(tmp);
        return worldHeight;
    }

    private static int adjustIndex(int baseIndex, Direction direction, float stepFactor, int length) {
        //we have the same or lower resolution than our neighbor, so we iterate over all our points
        if (stepFactor >= 1)
        {
            switch (direction){
                case TOP:
                    return (length - 1) * length + baseIndex;
                case BOTTOM:
                    return baseIndex;
                case LEFT:
                    return baseIndex * length;
                case RIGHT:
                    return baseIndex * length + (length -1);
            }
            return baseIndex;
        }

        else {
            // Main terrain has higher resolution so need to scale
            int offset = (int) (1.0f / stepFactor);
            switch (direction) {
                case TOP:
                    return ((length - 1) * length + baseIndex) / offset;
                case BOTTOM:
                    return baseIndex / offset;
                case LEFT:
                    return baseIndex * length / offset;
                case RIGHT:
                    return (baseIndex * length + (length - 1)) / offset;
            }
        }

        throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    private static int adjustNeighborIndex(int baseIndex, Direction direction, float stepFactor, int neighborLength) {

        if (stepFactor <= 1) {
            // Neighbor has same or lower resolution than current terrain

            switch (direction) {
                case TOP:
                    return baseIndex;
                case BOTTOM:
                    return (neighborLength - 1) * neighborLength + baseIndex;
                case LEFT:
                    return baseIndex * neighborLength + (neighborLength -1);
                case RIGHT:
                    return baseIndex * neighborLength;
            }
        }

        else {
            // Neighbor terrain has higher resolution as current
            int offset = (int) (stepFactor);
            switch (direction) {
                case TOP:
                    return ((neighborLength - 1) * neighborLength + baseIndex) / offset;
                case BOTTOM:
                    return baseIndex / offset;
                case LEFT:
                    return (baseIndex * neighborLength + (neighborLength - 1)) / offset;
                case RIGHT:
                    return baseIndex* neighborLength / offset;
            }
        }
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }
}