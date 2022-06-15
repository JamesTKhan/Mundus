package com.mbrlabs.mundus.commons.physics.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.physics.PhysicsSystem;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * @author James Pooley
 * @version June 15, 2022
 */
public class BulletPhysicsSystem implements PhysicsSystem {

    private btDynamicsWorld dynamicsWorld;
    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private btBroadphaseInterface broadphase;
    private btConstraintSolver constraintSolver;

    private float timeStep = 1/60f;
    private Array<btRigidBody> rigidBodyList;

    public BulletPhysicsSystem() {
        // Init Bullet classes
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -8f, 0));

        rigidBodyList = new Array<>();
    }

    public void removeRigidBody(btRigidBody rigidBody) {
        dynamicsWorld.removeCollisionObject(rigidBody);
        rigidBodyList.removeValue(rigidBody, true);
        rigidBody.dispose();
    }

    public void addRigidBody(btCollisionShape shape, float mass, float friction, Matrix4 worldTrans,
                                btMotionState motionState, Object userData) {

        Vector3 localInertia = new Vector3();
        if (mass > 0f) {
            shape.calculateLocalInertia(mass, localInertia);
        } else {
            localInertia.set(0, 0, 0);
        }
        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(
                mass, null, shape, localInertia);
        constructionInfo.setFriction(friction);

        btRigidBody rigidBody = new btRigidBody(constructionInfo);
        rigidBody.setMotionState(motionState);
        rigidBody.userData = userData;
//        rigidBody.setCollisionFlags(rigidBody.getCollisionFlags()
//                | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

        //rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        constructionInfo.dispose();

        if (worldTrans != null)
            rigidBody.setWorldTransform(worldTrans);

        //addRigidBody(rigidBody, CollisionTypes.GROUND);
        dynamicsWorld.addRigidBody(rigidBody);
        rigidBodyList.add(rigidBody);
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

    /**
     * Updates all rigid bodies that have a GameObject
     * in userData to be the same transform as the game object.
     */
    public void updatePhysicsTransforms() {
        for (btRigidBody rigidBody : rigidBodyList) {
            if (!(rigidBody.userData instanceof GameObject)) continue;

            GameObject gameObject = (GameObject) rigidBody.userData;
            rigidBody.setWorldTransform(gameObject.getTransform());
        }
    }

    private void disposeOfBodies() {
        for (btRigidBody rigidBody : rigidBodyList) {
            removeRigidBody(rigidBody);
        }
    }

    @Override
    public void dispose() {
        disposeOfBodies();
        collisionConfig.dispose();
        dispatcher.dispose();
        dynamicsWorld.dispose();
        broadphase.dispose();
        constraintSolver.dispose();
    }

}
