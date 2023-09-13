package com.mbrlabs.mundus.editor.terrain;

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
            //if (numSteps > length) {
            //    throw new IllegalArgumentException("Number of Steps must be less than the vertex resolution of the terrain (" + length + ")");
            //}
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
        int components = 0;
        int stitchStep = 0;

        for (TerrainComponent terrainComponent : terrainComponents) {
            components++;
            if (terrainComponent.getTopNeighbor() != null) {
                neighbors++;
                stitchStep = stitchNeighbor(terrainComponent, terrainComponent.getTopNeighbor(), Direction.TOP);
                heightsStitched += stitchStep;
            }

            if (terrainComponent.getBottomNeighbor() != null) {
                neighbors++;
                stitchStep = stitchNeighbor(terrainComponent, terrainComponent.getBottomNeighbor(), Direction.BOTTOM);
                heightsStitched += stitchStep;
            }

            if (terrainComponent.getLeftNeighbor() != null) {
                neighbors++;
                stitchStep = stitchNeighbor(terrainComponent, terrainComponent.getLeftNeighbor(), Direction.LEFT);
                heightsStitched += stitchStep;
            }

            if (terrainComponent.getRightNeighbor() != null) {
                neighbors++;
                stitchStep = stitchNeighbor(terrainComponent, terrainComponent.getRightNeighbor(), Direction.RIGHT);
                heightsStitched += stitchStep;
            }
        }

        if (neighbors == 0) {
            UI.INSTANCE.getToaster().error("No terrains had neighbors assigned.");
        } else if (heightsStitched == 0) {
            UI.INSTANCE.getToaster().info("No terrains needed stitching.");
        } else {
            UI.INSTANCE.getToaster().success("Stitched " + components + " terrain components with " + neighbors + " neighbors, "+ heightsStitched + " height values stitched.");
        }
        return heightsStitched;
    }

    private static int stitchNeighbor(TerrainComponent terrain, TerrainComponent neighbor, Direction direction) {

        int width = terrain.getTerrainAsset().getTerrain().vertexResolution;
        int neighborWidth = neighbor.getTerrainAsset().getTerrain().vertexResolution;

        int heightsStitched = 0;

        Log.debug("Current width: " + width, "Neighbor width: " + neighborWidth);

        float terrainWH = getWorldHeight(terrain);
        float neighborWH = getWorldHeight(neighbor);

        float[] currentHeightMap = terrain.getTerrainAsset().getTerrain().heightData;
        float[] neighborHeightMap = neighbor.getTerrainAsset().getTerrain().heightData;

        int stepFactor = width / neighborWidth;

        // Main terrain has lower resolution than the neighbor skip it and let neighbor process
        if (stepFactor < 1) return 0;

            // Same size stitch
        else if (stepFactor == 1) {
            for (int i = 0; i < width; i++) {
                int currentIndex = adjustIndex(i, direction, width);
                int neighborIndex = adjustNeighborIndex(i, direction, neighborWidth);
                heightsStitched += setVertices(currentHeightMap, neighborHeightMap, currentIndex, neighborIndex, terrainWH, neighborWH);
            }
            return heightsStitched;
        }

        // Main terrain is larger, so we need to scale to neighbor's indices
        else{
            for (int i = 0; i < width; i++) {
                int x = i / stepFactor;
                int currentIndex = adjustIndex(i, direction, width);
                int neighborIndex = adjustNeighborIndex(x, direction, neighborWidth);
                heightsStitched += setVertices(currentHeightMap, neighborHeightMap,currentIndex, neighborIndex, terrainWH, neighborWH);
            }
            return heightsStitched;
        }
    }

    private static int setVertices(float[] currentHeightMap, float[] neighborHeightMap, int index, int neighborIndex, float currWH, float neighborWH) {
        float currHeight = currentHeightMap[index] + (includeWorldHeight ? currWH : 0);
        float neighborHeight = neighborHeightMap[neighborIndex] + (includeWorldHeight ? neighborWH: 0);
        if (currHeight != neighborHeight){
            currentHeightMap[index] = neighborHeight;
            return 1;
        }
        else return 0;
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

    private static float getWorldHeight(TerrainComponent terrainComponent) {
        Vector3 tmp = Pools.vector3Pool.obtain();
        float worldHeight = terrainComponent.getGameObject().getTransform().getTranslation(tmp).y;
        Pools.vector3Pool.free(tmp);
        return worldHeight;
    }
}