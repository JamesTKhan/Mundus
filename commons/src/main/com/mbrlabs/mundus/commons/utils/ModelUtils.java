package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

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
}
