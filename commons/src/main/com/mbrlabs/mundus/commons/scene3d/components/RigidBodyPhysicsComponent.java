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

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.physics.bullet.BulletBuilder;
import com.mbrlabs.mundus.commons.physics.bullet.GameObjectMotionState;
import com.mbrlabs.mundus.commons.physics.bullet.RigidBodyResult;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsBody;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * @author James Pooley
 * @version June 16, 2022
 */
public class RigidBodyPhysicsComponent extends AbstractPhysicsComponent implements Disposable {
    private static final Vector3 scale = new Vector3();

    // We hold reference to this for memory reasons
    protected btRigidBody.btRigidBodyConstructionInfo constructionInfo;

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

    @Override
    public void initializeBody() {
        if (bodyInitialized) {
            constructionInfo.release();
            collisionObject.release();
            collisionShape.release();
        }

        bodyInitialized = true;

        // Get models bounding box
        BoundingBox boundingBox = null;
        ModelComponent modelComponent = (ModelComponent) gameObject.findComponentByType(Type.MODEL);
        if (modelComponent != null) {
            boundingBox = new BoundingBox();
            modelComponent.getModelInstance().calculateBoundingBox(boundingBox);
        }

        // Build the collision shape
        if (physicsBodyType == PhysicsBody.STATIC && modelComponent != null) {
            //TODO Many small btBvhTriangleMeshShape pollute the broadphase. Better combine them.
            collisionShape = Bullet.obtainStaticNodeShape(modelComponent.modelInstance.nodes);
        } else {
            collisionShape = new BulletBuilder.ShapeBuilder(physicsShape)
                    .scale(gameObject.getLocalScale(scale))
                    .boundingBox(boundingBox)
                    .model(modelComponent == null ? null : modelComponent.getModelInstance().model)
                    .build();
        }

        // Build the rigid body
        RigidBodyResult result = new BulletBuilder.RigidBodyBuilder(collisionShape)
                .mass(physicsBodyType == PhysicsBody.DYNAMIC ? mass : 0f)
                .friction(physicsBodyType == PhysicsBody.DYNAMIC ? friction : 0f)
                .restitution(physicsBodyType == PhysicsBody.DYNAMIC ? restitution : 0f)
                .btMotionState(physicsBodyType == PhysicsBody.DYNAMIC ? new GameObjectMotionState(gameObject) : null)
                .collisionFlags(physicsBodyType == PhysicsBody.KINEMATIC ? btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT : -1)
                .activationState(disableDeactivation ? Collision.DISABLE_DEACTIVATION : Collision.ACTIVE_TAG)
                .build();

        // Set transform on static bodies
        if (physicsBodyType == PhysicsBody.STATIC && modelComponent != null) {
            result.rigidBody.setWorldTransform(gameObject.getTransform());
        }

        constructionInfo = result.constructionInfo;
        collisionObject = result.rigidBody;

        constructionInfo.obtain();
        collisionObject.obtain();
        collisionShape.obtain();
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
