package com.mbrlabs.mundus.commons.physics.bullet;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

/**
 * Result object from using BulletBuilder which contains references to rigid body objects.
 */
public class RigidBodyResult {
    public btRigidBody rigidBody;
    public btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    public RigidBodyResult(btRigidBody rigidBody, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        this.rigidBody = rigidBody;
        this.constructionInfo = constructionInfo;
    }
}
