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

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsBody;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsShape;
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
    protected PhysicsBody physicsBodyType;
    protected PhysicsShape physicsShape;

    protected btCollisionShape collisionShape;
    protected btCollisionObject collisionObject;

    public AbstractPhysicsComponent(GameObject gameObject, PhysicsBody physicsBodyType) {
        super(gameObject);
        this.type = Type.PHYSICS;
        this.physicsBodyType = physicsBodyType;
        this.physicsShape = PhysicsShape.BOX;
    }

    public abstract void initializeBody();

    public btCollisionShape getCollisionShape() {
        return collisionShape;
    }

    public btCollisionObject getCollisionObject() {
        return collisionObject;
    }

    public PhysicsBody getPhysicsBodyType() {
        return physicsBodyType;
    }

    public void setPhysicsBodyType(PhysicsBody physicsBodyType) {
        this.physicsBodyType = physicsBodyType;
    }

    public PhysicsShape getPhysicsShape() {
        return physicsShape;
    }

    public void setPhysicsShape(PhysicsShape physicsShape) {
        this.physicsShape = physicsShape;
    }
}
