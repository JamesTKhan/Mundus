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

package com.mbrlabs.mundus.commons.dto;

import com.mbrlabs.mundus.commons.physics.enums.PhysicsBody;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsShape;

/**
 * @author James Pooley
 * @version July 05, 2022
 */
public class PhysicsComponentDTO {
    protected PhysicsBody physicsBodyType;
    protected PhysicsShape physicsShape;

    protected float mass = 1f;
    protected float friction = 1f;
    protected float restitution = 0f;
    protected boolean disableDeactivation = false;

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
}
