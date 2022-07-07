/*
 * Copyright (c) 2022. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.physics.bullet.builders;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import com.badlogic.gdx.utils.Array;

import java.nio.FloatBuffer;

/**
 *
 * Some shapes produce multiple btXXXX objects.
 *
 * For example The btGImpactMeshShape and the btTriangleIndexVertexArray.
 *
 * Due to Garbage Collection references to objects passed to bullet must be kept in memory, so this returns all objects
 * used for a shape. Make sure to store references to these objects while you need them to try and prevent GC.

 * @author James Pooley
 * @version July 07, 2022
 */
public class ShapeBuilderResult {
    // The actual shape
    public btCollisionShape shape;

    // Used for Gimpact shapes
    public btTriangleIndexVertexArray vertexArray;

    // For Terrain Shapes, The caller is responsible for maintaining the height field array, bullet does not make a copy
    FloatBuffer fb;

    // Additional instances created for convex hull that we hold a reference to
    Array<btConvexHullShape> convexHullShapes;
}
