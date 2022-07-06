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

package com.mbrlabs.mundus.runtime.converter;

import com.mbrlabs.mundus.commons.dto.PhysicsComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractPhysicsComponent;
import com.mbrlabs.mundus.commons.scene3d.components.RigidBodyPhysicsComponent;

/**
 * @author James Pooley
 * @version July 05, 2022
 */
public class PhysicsComponentConverter {

    /**
     * Converts {@link PhysicsComponentDTO} to {@link AbstractPhysicsComponent}.
     */
    public static AbstractPhysicsComponent convert(PhysicsComponentDTO dto, GameObject go) {
        RigidBodyPhysicsComponent component = new RigidBodyPhysicsComponent(go, dto.getPhysicsBodyType());

        component.setPhysicsShape(dto.getPhysicsShape());
        component.setFriction(dto.getFriction());
        component.setRestitution(dto.getRestitution());
        component.setMass(dto.getMass());
        component.setDisableDeactivation(dto.isDisableDeactivation());

        return component;
    }
}
