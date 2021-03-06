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

package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Marcus Brummer
 * @version 14-02-2016
 */
public class BaseLight {

    public final Color color = new Color(1, 1, 1, 1);
    public float intensity = 1f;
    public boolean castsShadows = false;
    public LightType lightType;

    public void setColor(Color color) {
        this.color.set(color);
    }
}
