/*
 * Copyright (c) 2021. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.core.converter;

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.WaterAsset;
import com.mbrlabs.mundus.commons.dto.WaterComponentDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.scene3d.components.PickableWaterComponent;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.utils.Log;

import java.util.Map;

/**
 * The converter for Water.
 */
public class WaterComponentConverter {

    private final static String TAG = WaterComponentConverter.class.getSimpleName();

    /**
     * Converts {@link WaterComponentDTO} to {@link PickableWaterComponent}.
     */
    public static PickableWaterComponent convert(WaterComponentDTO dto, GameObject go,
                                                 Map<String, Asset> assets) {
        // find waterAsset
        WaterAsset water = (WaterAsset) assets.get(dto.getWaterId());

        if (water == null) {
            Log.fatal(TAG, "Water for WaterInstance not found");
            return null;
        }

        water.water.transform = go.getTransform();
        PickableWaterComponent waterComponent = new PickableWaterComponent(go, null);
        waterComponent.setWaterAsset(water);
        go.hasWaterComponent = true;

        return waterComponent;
    }

    /**
     * Converts {@link PickableWaterComponent} to {@link WaterComponentDTO}.
     */
    public static WaterComponentDTO convert(PickableWaterComponent waterComponent) {
        WaterComponentDTO descriptor = new WaterComponentDTO();
        descriptor.setWaterId(waterComponent.getWaterAsset().getID());

        return descriptor;
    }

}
