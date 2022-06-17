package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * @author James Pooley
 * @version June 16, 2022
 */
public class RigidBodyPhysicsComponent extends AbstractPhysicsComponent implements Disposable {
    // We hold reference to this for memory reasons
    private final btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    public RigidBodyPhysicsComponent(GameObject gameObject, btRigidBody.btRigidBodyConstructionInfo constructionInfo, btCollisionShape collisionShape, btCollisionObject collisionObject) {
        super(gameObject);
        this.constructionInfo = constructionInfo;
        this.collisionShape = collisionShape;
        this.collisionObject = collisionObject;

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
