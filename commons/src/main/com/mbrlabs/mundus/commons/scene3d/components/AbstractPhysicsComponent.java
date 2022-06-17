package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

/**
 * The primary purpose of the physics components is to hold a reference to the btXXXXX bullet objects.
 * Because these are linked to native code we must keep a reference of every btXXXX class instantiated
 * with the new keyword. If we don't the object will eventually be garbage collected and the program
 * will get a native bullet.dll crash.
 *
 * @author James Pooley
 * @version June 16, 2022
 */
public abstract class AbstractPhysicsComponent extends AbstractComponent {
    protected btCollisionShape collisionShape;
    protected btCollisionObject collisionObject;

    public AbstractPhysicsComponent(GameObject gameObject) {
        super(gameObject);
        this.type = Type.PHYSICS;
    }

    public btCollisionShape getCollisionShape() {
        return collisionShape;
    }

    public btCollisionObject getCollisionObject() {
        return collisionObject;
    }
}
