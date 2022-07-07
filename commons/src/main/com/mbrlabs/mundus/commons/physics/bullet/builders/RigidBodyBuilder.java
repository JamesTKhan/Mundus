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

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * @author James Pooley
 * @version July 07, 2022
 */
public class RigidBodyBuilder {

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
