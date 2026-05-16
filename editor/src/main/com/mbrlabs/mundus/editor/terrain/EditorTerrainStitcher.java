package com.mbrlabs.mundus.editor.terrain;

import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.terrain.TerrainStitcher;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.history.commands.TerrainStitchCommand;
import com.mbrlabs.mundus.editor.ui.UI;
import com.mbrlabs.mundus.editor.utils.ThreadLocalPools;
import com.mbrlabs.mundus.editorcommons.events.TerrainVerticesChangedEvent;

/**
 * Editor extension of the TerrainStitcher utility class.
 * Finds all terrain components in the current scene and stitches them together based on their neighbors.
 * Also adds the changes to the undo/redo history and marks the terrain assets as modified so they can be saved.
 * @author JamesTKhan
 * @version May 16, 2026
 */
public class EditorTerrainStitcher extends TerrainStitcher {

    public static void stitch(ProjectContext projectContext) {
        // Get all the terrain components
        Array<GameObject> terrainGOs = projectContext.currScene.sceneGraph.findAllByComponent(Component.Type.TERRAIN);
        Array<TerrainComponent> terrainComponents = new Array<>();

        for (GameObject go : terrainGOs) {
            TerrainComponent terrainComponent = go.findComponentByType(Component.Type.TERRAIN);

            int length = terrainComponent.getTerrainAsset().getTerrain().vertexResolution;
            if (numSteps > length) {
                throw new IllegalArgumentException("Number of Steps must be less than the vertex resolution of the terrain (" + length + ")");
            }

            terrainComponents.add(terrainComponent);
        }

        // Add command for undo/redo history
        TerrainStitchCommand command = new TerrainStitchCommand(terrainComponents);

        // Stitch them together
        boolean terrainUpdated = stitchComponent(terrainComponents);

        if (!terrainUpdated) {
            // No changes were made, so don't add to history
            return;
        }

        // Execute command/apply changes to terrains
        Mundus.INSTANCE.getCommandHistory().add(command);
        command.setHeightDataAfter();
        command.execute();

        // Now add to the modified assets so they can be saved
        for (TerrainComponent terrainComponent : terrainComponents) {
            projectContext.assetManager.addModifiedAsset(terrainComponent.getTerrainAsset());
            Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
        }

        // Post process normals after stitching heights
        for (TerrainComponent terrainComponent : terrainComponents) {
            stitchNormals(terrainComponent, ThreadLocalPools.vector3ThreadPool.get());
        }
    }

    public static boolean stitchComponent(Array<TerrainComponent> terrainComponents) {
        boolean updates = stitch(terrainComponents, ThreadLocalPools.vector3ThreadPool.get());

        if (updates) {
            UI.INSTANCE.getToaster().success("Terrain stitcher finished updates");
        } else {
            UI.INSTANCE.getToaster().info("Terrain stitcher finished, no updates needed");
        }

        return updates;
    }

}
