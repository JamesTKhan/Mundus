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

package com.mbrlabs.mundus.commons.physics.bullet;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsShape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Helper class for building bullet shapes and collision objects. Expand upon as needed.
 *
 * @author James Pooley
 * @version June 16, 2022
 */
public class BulletBuilder {

    private BulletBuilder() {
    }

    public static class RigidBodyBuilder {
        private final btCollisionShape shape;
        private final Vector3 localInertia = new Vector3();

        private int activationState = Collision.ACTIVE_TAG;
        private int collisionFlags = 0;
        private float mass = 0f;
        private float friction = 1f;
        private float restitution = 0f;
        private btMotionState motionState = null;
        private Object userData = null;

        public RigidBodyBuilder(btCollisionShape shape) {
            this.shape = shape;
        }

        public RigidBodyBuilder mass(float mass) {
            this.mass = mass;
            return this;
        }

        public RigidBodyBuilder friction(float friction) {
            this.friction = friction;
            return this;
        }

        public RigidBodyBuilder restitution(float restitution) {
            this.restitution = restitution;
            return this;
        }

        public RigidBodyBuilder localInertia(Vector3 localInertia) {
            this.localInertia.set(localInertia);
            return this;
        }

        public RigidBodyBuilder btMotionState(btMotionState motionState) {
            this.motionState = motionState;
            return this;
        }

        /**
         * Normally would use this for forcing the collision object to never deactivate physics by
         * passing Collision.DISABLE_DEACTIVATION
         *
         * @param activationState the activation state to set
         * @return the rigid body instance
         */
        public RigidBodyBuilder activationState(int activationState) {
            this.activationState = activationState;
            return this;
        }

        public RigidBodyBuilder collisionFlags(int collisionFlags) {
            this.collisionFlags = collisionFlags;
            return this;
        }

        public RigidBodyBuilder userData(Object userData) {
            this.userData = userData;
            return this;
        }

        public RigidBodyResult build() {
            if (mass > 0f) {
                shape.calculateLocalInertia(mass, localInertia);
            }

            btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(
                    mass, null, shape, localInertia);
            constructionInfo.setFriction(friction);
            constructionInfo.setRestitution(restitution);

            btRigidBody rigidBody = new btRigidBody(constructionInfo);
            rigidBody.setMotionState(motionState);
            rigidBody.userData = userData;

            if (activationState != Collision.ACTIVE_TAG) {
                rigidBody.setActivationState(activationState);
            }

            if (collisionFlags != 0) {
                rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | collisionFlags);
            }

            return new RigidBodyResult(rigidBody, constructionInfo);
        }

    }

    public static class ShapeBuilder {
        private static final Vector3 dimensions = new Vector3();

        private btCollisionShape shape;
        private final PhysicsShape shapeEnum;
        private BoundingBox boundingBox = null;
        private Vector3 scale = null;
        private Model model = null;
        private TerrainAsset terrainAsset = null;

        private float radius = 1f;

        public ShapeBuilder(PhysicsShape shapeEnum) {
            this.shapeEnum = shapeEnum;
        }

        public ShapeBuilder boundingBox(BoundingBox boundingBox) {
            this.boundingBox = boundingBox;
            return this;
        }

        public ShapeBuilder scale(Vector3 scale) {
            this.scale = scale;
            return this;
        }

        public ShapeBuilder model(Model model) {
            this.model = model;
            return this;
        }

        public ShapeBuilder terrainAsset(TerrainAsset terrainAsset) {
            this.terrainAsset = terrainAsset;
            return this;
        }

        public btCollisionShape build() {

            // Get dimensions needed for shape building
            if (boundingBox != null) {
                // Get the dimensions
                boundingBox.getDimensions(dimensions);

                dimensions.scl(0.5f);// Scale for half extents

                // Handle scale
                if (scale != null) {
                    dimensions.scl(scale);
                }

                radius = dimensions.len() / 2f;
            } else {
                dimensions.set(1f,1f,1f);
            }

            switch (shapeEnum) {
                case BOX:
                    return buildBoxShape();
                case SPHERE:
                    return buildSphereShape();
                case CAPSULE:
                    return buildCapsuleShape();
                case CYLINDER:
                    return buildCylinderShape();
                case CONE:
                    return buildConeShape();
                case CONVEX_HULL:
                    return buildConvexHullShape();
                case G_IMPACT_TRIANGLE_MESH:
                    throw new GdxRuntimeException("Use GimpactShapeBuilder for Gimpact shapes");
                case TERRAIN:
                    return buildTerrainShape();
                case SCALED_BVH_TRIANGLE:
                    throw new GdxRuntimeException("SCALED_BVH_TRIANGLE shape not implemented for manual creation.");
                case BVH_TRIANGLE:
                    throw new GdxRuntimeException("BVH_TRIANGLE shape not implemented for manual creation.");
            }

            return null;
        }

        private btCollisionShape buildTerrainShape() {
            if (terrainAsset == null) {
                throw new GdxRuntimeException("TerrainAsset object is required for ShapeBuilder to create terrain shapes.");
            }

            float minHeight = 999;// min height for entire chunk
            float maxHeight = -999;// max height for entire chunk
            for (float height : terrainAsset.getData()) {
                // Set height min/maxes for bullet
                if (height < minHeight)
                    minHeight = height;
                else if (height > maxHeight)
                    maxHeight = height;
            }

            terrainAsset.setMaxHeight(maxHeight);
            terrainAsset.setMinHeight(minHeight);

            ByteBuffer vbb = ByteBuffer.allocateDirect(terrainAsset.getData().length * 4);
            vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order

            FloatBuffer fb;// This may have to saved in memory
            fb = vbb.asFloatBuffer();  // create floating point buffer using bytebuffer
            fb.put(terrainAsset.getData()); // add height data to buffer
            fb.position(0);

            float size = terrainAsset.getTerrain().terrainWidth;
            float vertexCount = terrainAsset.getTerrain().vertexResolution;
            btHeightfieldTerrainShape terrainShape = new btHeightfieldTerrainShape(terrainAsset.getTerrain().vertexResolution, terrainAsset.getTerrain().vertexResolution, fb, 1, minHeight, maxHeight, 1, true);
            terrainShape.setLocalScaling(new Vector3((size) / ((vertexCount - 1)), 1, (size) / ((vertexCount - 1))));

            shape = terrainShape;
            return shape;
        }

        private btCollisionShape buildConvexHullShape() {
            if (model == null) {
                throw new GdxRuntimeException("Model is required for ShapeBuilder to create convex hull shapes.");
            }

            btCompoundShape compoundShape = new btCompoundShape();

            for (MeshPart meshPart : model.meshParts) {
                Mesh mesh = meshPart.mesh;
                btConvexHullShape convexHullShape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());

                final btShapeHull hull = new btShapeHull(convexHullShape);
                hull.buildHull(convexHullShape.getMargin());
                final btConvexHullShape result = new btConvexHullShape(hull);
                compoundShape.addChildShape(new Matrix4().setToTranslation(meshPart.center), result);
            }

            shape = compoundShape;

            if (scale != null) {
                shape.setLocalScaling(scale);
            }

            return shape;
        }

        private btCollisionShape buildConeShape() {
            shape = new btConeShape(radius, dimensions.y);
            return shape;
        }

        private btCollisionShape buildCylinderShape() {
            shape = new btCylinderShape(dimensions);
            return shape;
        }

        private btCollisionShape buildSphereShape() {
            shape = new btSphereShape(radius);
            return shape;
        }

        private btCollisionShape buildCapsuleShape() {
            shape = new btCapsuleShape(radius, dimensions.y);
            return shape;
        }

        private btCollisionShape buildBoxShape() {
            if (boundingBox == null)
                shape = new btBoxShape(new Vector3(1,1,1));
            else {
                shape = new btBoxShape(dimensions);
            }
            return shape;
        }

    }


    /**
     * Gimpact shape produces two bullet objects. The btGImpactMeshShape and the btTriangleIndexVertexArray.
     *
     * Due to Garbage Collection references to both objects must be kept in memory, so this returns both objects
     * in a result object. Make sure to store references to both bullet objects while you need them.
     */
    public static class GimpactShapeBuilder {
        private final Model model;
        private final Vector3 scale;

        public class GimpactResult {
            public btTriangleIndexVertexArray vertexArray;
            public btGImpactMeshShape shape;

            public GimpactResult(btTriangleIndexVertexArray vertexArray, btGImpactMeshShape shape) {
                this.vertexArray = vertexArray;
                this.shape = shape;
            }
        }

        public GimpactShapeBuilder(Model model, Vector3 scale) {
            this.model = model;
            this.scale = scale;
        }

        public GimpactResult build() {
            if (model == null) {
                throw new GdxRuntimeException("Model is required for ShapeBuilder to create gimpact shapes.");
            }
            btTriangleIndexVertexArray vertexArray = new btTriangleIndexVertexArray(model.meshParts);

            btGImpactMeshShape shape = new btGImpactMeshShape(vertexArray);

            if (scale != null) {
                shape.setLocalScaling(scale);
            }

            shape.updateBound();
            return new GimpactResult(vertexArray, shape);
        }
    }

}

