package com.mbrlabs.mundus.commons.physics.bullet;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

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
        private float mass = 0f;
        private float friction = 1f;
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

            btRigidBody rigidBody = new btRigidBody(constructionInfo);
            rigidBody.setMotionState(motionState);
            rigidBody.userData = userData;

            if (activationState != Collision.ACTIVE_TAG) {
                rigidBody.setActivationState(activationState);
            }

            constructionInfo.dispose();

            return new RigidBodyResult(rigidBody, constructionInfo);
        }

    }

    enum ShapeEnum {
        BOX
    }

    public static class ShapeBuilder {
        private btCollisionShape shape;
        private ShapeEnum shapeEnum;
        private BoundingBox boundingBox = null;
        private Vector3 scale = null;

        public ShapeBuilder(ShapeEnum shapeEnum) {
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

        public btCollisionShape build() {
            if (shapeEnum == ShapeEnum.BOX) {
                if (boundingBox == null)
                    shape = new btBoxShape(new Vector3(1,1,1));
                else {
                    // Get the dimensions
                    Vector3 dim = new Vector3();
                    boundingBox.getDimensions(dim);
                    dim.scl(0.4f);// half extents plus a bit more

                     // Handle scale
                    if (scale == null) scale = new Vector3(1,1,1);
                    dim.scl(scale);

                    shape = new btBoxShape(dim);
                }

                return shape;
            }

            return null;
        }

    }

}

