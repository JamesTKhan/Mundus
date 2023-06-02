/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.env;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
public class MundusEnvironment extends Environment {

    private float clippingHeight = 0;
    private Vector3 clippingPlane = new Vector3();

    public MundusEnvironment() {
        super();
    }

    public MundusEnvironment add(BaseLight light) {
        if (light instanceof SpotLightEx) {
            return add((SpotLightEx) light);
        }

        if (light instanceof PointLightEx) {
            PointLightsAttribute pointLights = ((PointLightsAttribute) get(PointLightsAttribute.Type));
            if (pointLights == null) set(pointLights = new PointLightsAttribute());
            pointLights.lights.add((PointLightEx) light);
        }

        return this;
    }

    public MundusEnvironment add(SpotLightEx light) {
        SpotLightsAttribute spotLights = ((SpotLightsAttribute) get(SpotLightsAttribute.Type));
        if (spotLights == null) set(spotLights = new SpotLightsAttribute());
        spotLights.lights.add(light);

        return this;
    }

    public Environment remove (BaseLight light) {
        if (light instanceof SpotLightEx) {
            return remove((SpotLightEx) light);
        }

        if (has(PointLightsAttribute.Type) && light instanceof PointLightEx) {
            PointLightsAttribute pointLights = ((PointLightsAttribute)get(PointLightsAttribute.Type));
            pointLights.lights.removeValue((PointLightEx) light, false);
            if (pointLights.lights.size == 0)
                remove(PointLightsAttribute.Type);
        }
        return this;
    }

    public Environment remove (SpotLightEx light) {
        if (has(SpotLightsAttribute.Type)) {
            SpotLightsAttribute spotLights = ((SpotLightsAttribute)get(SpotLightsAttribute.Type));
            spotLights.lights.removeValue(light, false);
            if (spotLights.lights.size == 0)
                remove(SpotLightsAttribute.Type);
        }
        return this;
    }

    public ColorAttribute getAmbientLight() {
        return get(ColorAttribute.class, ColorAttribute.AmbientLight);
    }

    public void setAmbientLight(Color ambientLight) {
        set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight));
    }

    public float getClippingHeight() {
        return clippingHeight;
    }

    public void setClippingHeight(float clippingHeight) {
        this.clippingHeight = clippingHeight;
    }

    public Vector3 getClippingPlane() {
        return clippingPlane;
    }

    public void setClippingPlane(Vector3 clippingPlane) {
        this.clippingPlane = clippingPlane;
    }
}
