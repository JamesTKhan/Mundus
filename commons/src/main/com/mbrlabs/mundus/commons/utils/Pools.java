package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool;

/**
 * Pooling of commonly used objects.
 *
 * @author JamesTKhan
 * @version May 14, 2023
 */
public class Pools {

    public final static Pool<Vector2> vector2Pool = new Pool<Vector2>() {
        @Override
        protected Vector2 newObject () {
            return new Vector2();
        }

        @Override
        protected void reset(Vector2 object) {
            object.set(0, 0);
        }
    };

    public final static Pool<Vector3> vector3Pool = new Pool<Vector3>() {
        @Override
        protected Vector3 newObject () {
            return new Vector3();
        }

        @Override
        protected void reset(Vector3 object) {
            object.set(0,0,0);
        }
    };

    public final static Pool<BoundingBox> boundingBoxPool = new Pool<BoundingBox>(4) {
        @Override
        protected BoundingBox newObject () {
            return new BoundingBox();
        }

        @Override
        protected void reset(BoundingBox object) {
            object.set(Vector3.Zero, Vector3.Zero);
        }
    };


    /**
     * Convenience method, free array of objects
     * @param objects objects to free
     */
    public static void free(Vector2... objects ) {
        for (Vector2 object : objects) {
            vector2Pool.free(object);
        }
    }

    public static void free(Vector3... objects ) {
        for (Vector3 object : objects) {
            vector3Pool.free(object);
        }
    }
}
