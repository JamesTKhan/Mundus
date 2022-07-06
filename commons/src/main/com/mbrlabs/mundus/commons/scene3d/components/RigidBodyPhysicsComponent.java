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

package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.physics.bullet.BulletBuilder;
import com.mbrlabs.mundus.commons.physics.bullet.GameObjectMotionState;
import com.mbrlabs.mundus.commons.physics.bullet.RigidBodyResult;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsBody;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsShape;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * @author James Pooley
 * @version June 16, 2022
 */
public class RigidBodyPhysicsComponent extends AbstractPhysicsComponent implements Disposable {
    private static final Vector3 scale = new Vector3();
    private static final Vector3 translation = new Vector3();
    private static final Quaternion rotation = new Quaternion();

    // We hold reference to this for memory reasons
    protected btRigidBody.btRigidBodyConstructionInfo constructionInfo;
    // Only set for gimpact shape
    private btTriangleIndexVertexArray vertexArray = null;

    protected float mass = 1f;
    protected float friction = 1f;
    protected float restitution = 0f;
    protected boolean disableDeactivation = false;

    private boolean bodyInitialized = false;

    public RigidBodyPhysicsComponent(GameObject gameObject, btRigidBody.btRigidBodyConstructionInfo constructionInfo, btCollisionShape collisionShape, btCollisionObject collisionObject, PhysicsBody physicsBodyType) {
        super(gameObject, physicsBodyType);
        bodyInitialized = true;

        this.constructionInfo = constructionInfo;
        this.collisionShape = collisionShape;
        this.collisionObject = collisionObject;

        constructionInfo.obtain();
        collisionObject.obtain();
        collisionShape.obtain();
    }

    public RigidBodyPhysicsComponent(GameObject gameObject, PhysicsBody physicsBodyType) {
        super(gameObject, physicsBodyType);
    }

    /**
     * Initializes the physics body based on the components properties
     * and other attached components (model, terrain, etc..).
     *
     */
    @Override
    public void initializeBody() {
        if (bodyInitialized) {
            constructionInfo.release();
            collisionObject.release();
            collisionShape.release();
        }

        bodyInitialized = true;

        Model model = null;
        BoundingBox boundingBox = null;

        ModelComponent modelComponent = (ModelComponent) gameObject.findComponentByType(Type.MODEL);
        TerrainComponent terrainComponent = (TerrainComponent) gameObject.findComponentByType(Type.TERRAIN);

        if (modelComponent != null) {
            boundingBox = new BoundingBox();
            model = modelComponent.modelInstance.model;
            modelComponent.getModelInstance().calculateBoundingBox(boundingBox);
        } else if (terrainComponent != null) {
            model = terrainComponent.terrain.getTerrain().modelInstance.model;

            if (physicsBodyType == PhysicsBody.STATIC) {
                // For static terrains we use btHeightfieldTerrainShape which will only get be utilized
                // for TERRAIN shapes, so default to terrain shape.
                physicsShape = PhysicsShape.TERRAIN;
            }
        }

        // Build the collision shape
        if (physicsBodyType == PhysicsBody.STATIC && modelComponent != null) {
            //TODO Many small btBvhTriangleMeshShape pollute the broadphase. Better combine them.
            collisionShape = Bullet.obtainStaticNodeShape(modelComponent.modelInstance.nodes);
            collisionShape.setLocalScaling(gameObject.getLocalScale(scale));
        } else if (physicsShape == PhysicsShape.G_IMPACT_TRIANGLE_MESH){
            BulletBuilder.GimpactShapeBuilder.GimpactResult result  = new BulletBuilder.GimpactShapeBuilder(model, gameObject.getLocalScale(scale))
                            .build();
            collisionShape = result.shape;
            vertexArray = result.vertexArray;
            vertexArray.obtain();
        } else {
            collisionShape = new BulletBuilder.ShapeBuilder(physicsShape)
                    .scale(gameObject.getLocalScale(scale))
                    .boundingBox(boundingBox)
                    .model(model)
                    .terrainAsset(terrainComponent != null && physicsShape == PhysicsShape.TERRAIN ? terrainComponent.getTerrain() : null)
                    .build();
        }

        // Set collision flags accordingly
        int collisionFlags = getCollisionFlags(model, terrainComponent);

        // Build the rigid body
        RigidBodyResult result = new BulletBuilder.RigidBodyBuilder(collisionShape)
                .mass(physicsBodyType == PhysicsBody.DYNAMIC ? mass : 0f)
                .friction(friction)
                .restitution(restitution)
                .btMotionState(physicsBodyType == PhysicsBody.DYNAMIC ? new GameObjectMotionState(gameObject) : null)
                .collisionFlags(collisionFlags)
                .activationState(disableDeactivation ? Collision.DISABLE_DEACTIVATION : Collision.ACTIVE_TAG)
                .build();

        // Set transform on static bodies
        if (physicsBodyType == PhysicsBody.STATIC && modelComponent != null) {
            // You cannot set a scale on the rigid bodies transform (only set on shape)
            // Otherwise it causes collisions to fail on static bodies. Instead, we get the
            // rigid body transform and only apply position and rotation to it.
            Matrix4 goTrans = gameObject.getTransform();
            Matrix4 bodyTransform = result.rigidBody.getWorldTransform();

            goTrans.getTranslation(translation);
            goTrans.getRotation(rotation);

            bodyTransform.set(goTrans.getTranslation(translation), goTrans.getRotation(rotation));

            result.rigidBody.setWorldTransform(bodyTransform);
        } else if (physicsShape == PhysicsShape.TERRAIN && terrainComponent != null) {
            float size = terrainComponent.getTerrain().getTerrain().terrainWidth;
            float adjustedHeight = (terrainComponent.terrain.getMaxHeight() + terrainComponent.terrain.getMinHeight()) / 2f;
            result.rigidBody.setWorldTransform(result.rigidBody.getWorldTransform().setTranslation(new Vector3((size / 2f), adjustedHeight, (size / 2f))));
        }

        constructionInfo = result.constructionInfo;
        collisionObject = result.rigidBody;

        constructionInfo.obtain();
        collisionObject.obtain();
        collisionShape.obtain();
    }

    private int getCollisionFlags(Model model, TerrainComponent terrainComponent) {
        int collisionFlags = 0;
        if (physicsBodyType == PhysicsBody.KINEMATIC) {
            collisionFlags =  btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT;
        } else if (physicsBodyType == PhysicsBody.STATIC) {
            collisionFlags =  btCollisionObject.CollisionFlags.CF_STATIC_OBJECT;
        }

        if (gameObject.sceneGraph.scene.debugDrawMode == btIDebugDraw.DebugDrawModes.DBG_DrawWireframe) {
            // Get vertices count
            int numVertices = 0;
            if (model != null) {
                for (Mesh mesh : model.meshes) {
                    numVertices += mesh.getNumVertices();
                }
            }

            if (terrainComponent != null ) {
                // Disable debug drawing for terrain or high vertices models due to performance issues on wireframe mode
                collisionFlags = collisionFlags | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT;
            }
        }

        return collisionFlags;
    }

    @Override
    public void render(float delta) {
        // This component does not render
    }

    @Override
    public void update(float delta) {
        // No need to update because bullet handles it for us
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public boolean isDisableDeactivation() {
        return disableDeactivation;
    }

    public void setDisableDeactivation(boolean disableDeactivation) {
        this.disableDeactivation = disableDeactivation;
    }

    public boolean isBodyInitialized() {
        return bodyInitialized;
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }

    @Override
    public void dispose() {
        constructionInfo.release();
        collisionObject.release();
        collisionShape.release();
    }
}
