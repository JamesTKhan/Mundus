package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * Indicates the object has a ModelInstance that can be cached.
 *
 * @author JamesTKhan
 * @version August 02, 2022
 */
public interface ModelCacheable {

    /**
     * Should this be used in a model cache
     */
    boolean shouldCache();

    /**
     * Sets if this should use a model cache or not
     */
    void setUseModelCache(boolean value);

    /**
     * Returns the model instance for model caching
     */
    ModelInstance getModelInstance();
}
