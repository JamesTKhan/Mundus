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

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btGImpactCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.physics.PhysicsSystem;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.RigidBodyPhysicsComponent;

/**
 * @author James Pooley
 * @version June 15, 2022
 */
@SuppressWarnings("NonJREEmulationClassesInClientCode")
public class BulletPhysicsSystem implements PhysicsSystem {
    private static final String TAG = AssetManager.class.getSimpleName();

    private final btDynamicsWorld dynamicsWorld;
    private final btCollisionConfiguration collisionConfig;
    private final btDispatcher dispatcher;
    private final btBroadphaseInterface broadphase;
    private final btConstraintSolver constraintSolver;

    private float timeStep = 1/60f;
    private final Array<btRigidBody> rigidBodyList;
    private boolean bodiesInitialized = false;

    public BulletPhysicsSystem() {
        // Init Bullet classes
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));

        btGImpactCollisionAlgorithm.registerAlgorithm((btCollisionDispatcher) dispatcher);

        collisionConfig.obtain();
        dispatcher.obtain();
        broadphase.obtain();
        constraintSolver.obtain();
        dynamicsWorld.obtain();

        rigidBodyList = new Array<>();
    }

    public void removeRigidBody(btRigidBody rigidBody) {
        dynamicsWorld.removeCollisionObject(rigidBody);
        rigidBodyList.removeValue(rigidBody, true);
    }

    public RigidBodyResult addRigidBody(btCollisionShape shape, float mass, float friction, Matrix4 worldTrans,
                                        btMotionState motionState, Object userData) {

        RigidBodyResult result = new BulletBuilder.RigidBodyBuilder(shape)
                .mass(mass)
                .friction(friction)
                .btMotionState(motionState)
                .userData(userData)
                .activationState(Collision.DISABLE_DEACTIVATION) // Must parameterize this
                .build();

        result.rigidBody.setWorldTransform(worldTrans);

        dynamicsWorld.addRigidBody(result.rigidBody);
        rigidBodyList.add(result.rigidBody);

        return result;
    }

    public void initializePhysicsComponents(Array<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {

            RigidBodyPhysicsComponent physicsComponent = (RigidBodyPhysicsComponent) gameObject.findComponentByType(Component.Type.PHYSICS);

            if (physicsComponent != null) {
                physicsComponent.initializeBody();
                dynamicsWorld.addRigidBody((btRigidBody) physicsComponent.getCollisionObject());
            }
        }

        bodiesInitialized = true;
    }

    public void removeGameObjectsFromPhysics(Array<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            Component component = gameObject.findComponentByType(Component.Type.PHYSICS);
            if (component instanceof RigidBodyPhysicsComponent) {
                removeRigidBody((btRigidBody) ((RigidBodyPhysicsComponent) component).getCollisionObject());
            }
        }

        bodiesInitialized = false;
    }

    public btDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    @Override
    public void setGravity(float x, float y, float z) {
        dynamicsWorld.setGravity(new Vector3(x,y,z));
    }

    @Override
    public void setTimeStep(float timeStep) {
        this.timeStep = timeStep;
    }

    @Override
    public void update(float delta) {
        // Update physics sim
        dynamicsWorld.stepSimulation(timeStep, 1, 1f / 60f);
    }

    private void disposeOfBodies() {
        for (btRigidBody rigidBody : rigidBodyList) {
            removeRigidBody(rigidBody);
            rigidBody.release();
        }
        rigidBodyList.clear();
    }

    @Override
    public void dispose() {
        collisionConfig.release();
        dispatcher.release();
        broadphase.release();
        constraintSolver.release();
        dynamicsWorld.release();

        disposeOfBodies();
    }

    public boolean isBodiesInitialized() {
        return bodiesInitialized;
    }
}
