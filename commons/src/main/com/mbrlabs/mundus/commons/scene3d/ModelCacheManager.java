package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

/**
 * Manages a ModelCache and keeps it up to date based on requests for rebuilds and set intervals
 *
 * @author JamesTKhan
 * @version August 02, 2022
 */
public class ModelCacheManager implements Disposable {
    private final Scene scene;

    public ModelCache modelCache;
    protected float modelCacheUpdateInterval = 0.5f;
    protected float lastModelCacheRebuild = modelCacheUpdateInterval;
    protected boolean modelCacheRebuildRequested = true;
    private final Array<ModelEventable> modelEventables;

    public ModelCacheManager(Scene scene) {
        modelCache = new ModelCache();
        this.scene = scene;
        this.modelEventables = new Array<>();
    }

    public void update(float delta) {
        if (modelCacheRebuildRequested) {
            lastModelCacheRebuild += delta;

            if (lastModelCacheRebuild > modelCacheUpdateInterval) {
                modelCacheRebuildRequested = false;
                lastModelCacheRebuild = 0f;
                rebuildModelCache();
            }
        }
    }

    /**
     * Rebuilds model cache for the current scene. Potentially expensive
     * depending on the size of the scene and should only be rebuilt when needed.
     */
    public void rebuildModelCache() {
        modelEventables.clear();
        modelCache.begin(scene.cam);
        addModelsToCache(scene.sceneGraph.getGameObjects());
        modelCache.end();
    }

    protected void addModelsToCache(Array<GameObject> gameObjects) {
        for (GameObject go : gameObjects) {

            if (!go.active) continue;

            for (Component comp : go.getComponents()) {
                if (comp instanceof ModelCacheable && ((ModelCacheable) comp).shouldCache()) {
                    ModelInstance modelInstance = ((ModelCacheable) comp).getModelInstance();

                    boolean skip = false;
                    for (Mesh mesh : modelInstance.model.meshes) {
                        if (mesh.getNumIndices() <= 0) {
                            Gdx.app.error(this.getClass().getSimpleName(), "Issues in mesh for " + go.name + " prevent it from being cacheable. Try cleaning mesh up in 3D modeling software.");
                            skip = true;
                            break;
                        }
                    }
                    if (skip) {
                        continue;
                    }

                    modelCache.add(((ModelCacheable) comp).getModelInstance());

                    if (comp instanceof ModelEventable) {
                        modelEventables.add((ModelEventable) comp);
                    }
                }
            }

            if (go.getChildren() != null) {
                addModelsToCache(go.getChildren());
            }
        }
    }

    /**
     * Request for the model cache to be rebuilt on the next interval
     */
    public void requestModelCacheRebuild() {
        modelCacheRebuildRequested = true;
    }

    /**
     * Change how often the model cache should be updated, in seconds.
     *
     * @param interval update interval
     */
    public void setModelCacheUpdateInterval(float interval) {
        modelCacheUpdateInterval = interval;
    }

    public void triggerBeforeDepthRenderEvent() {
        for (final ModelEventable me : modelEventables) {
            me.triggerBeforeDepthRenderEvent();
        }
    }

    public void triggerBeforeRenderEvent() {
        for (final ModelEventable me : modelEventables) {
            me.triggerBeforeRenderEvent();
        }
    }

    /**
     * Rebuild model cache if given GameObject has a cacheable component.
     */
    public static void rebuildIfCached(GameObject go, boolean immediately) {
        for (int i = 0; i < go.getComponents().size; i++) {
            if (go.getComponents().get(i) instanceof ModelCacheable) {
                if (immediately)
                    go.sceneGraph.scene.modelCacheManager.rebuildModelCache();
                else
                    go.sceneGraph.scene.modelCacheManager.requestModelCacheRebuild();
                break;
            }
        }
    }

    @Override
    public void dispose() {
        modelCache.dispose();
    }
}
