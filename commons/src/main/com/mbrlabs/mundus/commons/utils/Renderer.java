package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * Renderer interface for whatever usage, like DebugRenderer.
 *
 * Ex.
 * renderer.begin(camera);
 * renderer.render(gameObject);
 * renderer.end;
 *
 * @author JamesTKhan
 * @version July 25, 2022
 */
public interface Renderer {

    /**
     * Called before rendering
     * @param camera the camera to render to
     */
    void begin(Camera camera);

    /**
     * Render a single game object
     */
    void render(GameObject gameObject);

    /**
     * Render a list of gameObjects
     */
    void render(Array<GameObject> gameObjects);

    /**
     * Called at the end of rendering
     */
    void end();
}
