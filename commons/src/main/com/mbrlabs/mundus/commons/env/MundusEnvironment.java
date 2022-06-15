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

import com.badlogic.gdx.graphics.g3d.Environment;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.PointLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.SunLight;
import com.mbrlabs.mundus.commons.env.lights.SunLightsAttribute;
import com.mbrlabs.mundus.commons.shadows.CascadeShadowMapper;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
public class MundusEnvironment extends Environment {

    private Fog fog;
    private BaseLight ambientLight;
    public CascadeShadowMapper cascadeShadowMapper;

    public MundusEnvironment() {
        super();
        ambientLight = new BaseLight();
        fog = null;
    }

    public MundusEnvironment add(SunLight light) {
        SunLightsAttribute sunLights = ((SunLightsAttribute) get(SunLightsAttribute.Type));
        if (sunLights == null) set(sunLights = new SunLightsAttribute());
        sunLights.lights.add(light);

        return this;
    }

    public MundusEnvironment add(DirectionalLight light) {
        DirectionalLightsAttribute dirLights = ((DirectionalLightsAttribute) get(DirectionalLightsAttribute.Type));
        if (dirLights == null) set(dirLights = new DirectionalLightsAttribute());
        dirLights.lights.add(light);

        return this;
    }

    public MundusEnvironment add(PointLight light) {
        if (light instanceof SpotLight) {
            return add((SpotLight) light);
        }

        PointLightsAttribute pointLights = ((PointLightsAttribute) get(PointLightsAttribute.Type));
        if (pointLights == null) set(pointLights = new PointLightsAttribute());
        pointLights.lights.add(light);

        return this;
    }

    public MundusEnvironment add(SpotLight light) {
        SpotLightsAttribute spotLights = ((SpotLightsAttribute) get(SpotLightsAttribute.Type));
        if (spotLights == null) set(spotLights = new SpotLightsAttribute());
        spotLights.lights.add(light);

        return this;
    }

    public Environment remove (PointLight light) {
        if (light instanceof SpotLight) {
            return remove((SpotLight) light);
        }

        if (has(PointLightsAttribute.Type)) {
            PointLightsAttribute pointLights = ((PointLightsAttribute)get(PointLightsAttribute.Type));
            pointLights.lights.removeValue(light, false);
            if (pointLights.lights.size == 0)
                remove(PointLightsAttribute.Type);
        }
        return this;
    }

    public Environment remove (SpotLight light) {
        if (has(SpotLightsAttribute.Type)) {
            SpotLightsAttribute spotLights = ((SpotLightsAttribute)get(SpotLightsAttribute.Type));
            spotLights.lights.removeValue(light, false);
            if (spotLights.lights.size == 0)
                remove(SpotLightsAttribute.Type);
        }
        return this;
    }

    public BaseLight getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(BaseLight ambientLight) {
        this.ambientLight = ambientLight;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

}
