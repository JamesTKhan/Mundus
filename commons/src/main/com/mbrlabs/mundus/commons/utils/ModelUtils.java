package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;

/**
 * @author JamesTKhan
 * @version July 13, 2022
 */
public class ModelUtils {
    private static final Array<Renderable> renderables = new Array<>();
    private static final Pool<Renderable> pool = new RenderablePool();

    private ModelUtils() {}

    /**
     * Gets the total bone count for the given model based on having
     * one renderable.
     *
     * @param model the model to count bones for
     * @return the bone count
     */
    public static int getBoneCount(Model model) {
        int numBones = 0;

        ModelInstance instance = new ModelInstance(model);
        instance.getRenderables(renderables, pool);

        if (renderables.get(0).bones != null) {
            numBones = renderables.get(0).bones.length;
        }

        renderables.clear();
        pool.clear();

        return numBones;
    }

    /**
     * Applies materials on the provided GameObject and all of its children recursively
     *
     * @param rootGameObject the parent game object to apply
     */
    public static void applyGameObjectMaterials(GameObject rootGameObject) {
        ModelComponent mc = (ModelComponent) rootGameObject.findComponentByType(Component.Type.MODEL);
        if (mc != null) {
            mc.applyMaterials();
        }

        if (rootGameObject.getChildren() == null) return;

        // Update children recursively
        for (GameObject go : rootGameObject.getChildren()) {
            applyGameObjectMaterials(go);
        }
    }

    /**
     * Get Vertices count of a Model
     */
    public static int getVerticesCount(Model model) {
        int vertices = 0;
        for (Mesh mesh : model.meshes) {
            vertices += mesh.getNumVertices();
        }
        return vertices;
    }

    /**
     * Get Indices count of a Model
     */
    public static float getIndicesCount(Model model) {
        int indices = 0;
        for (Mesh mesh : model.meshes) {
            indices += mesh.getNumIndices();
        }
        return indices;
    }

}
