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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.WaterAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.water.attributes.WaterFloatAttribute;

public class WaterComponent extends CullableComponent implements AssetUsage, RenderableComponent {

    protected WaterAsset waterAsset;
    protected Shader shader;
    private float moveFactor = 0;
    private float u_Offset = 0;

    public WaterComponent(GameObject go, Shader shader) {
        super(go);
        this.shader = shader;
        type = Component.Type.WATER;
    }

    @Override
    public RenderableProvider getRenderableProvider() {
        return waterAsset.water;
    }

    public WaterAsset getWaterAsset() {
        return waterAsset;
    }

    public void setWaterAsset(WaterAsset waterAsset) {
        this.waterAsset = waterAsset;
        setDimensions(waterAsset.water.modelInstance);
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        return assetToCheck == waterAsset;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updateMoveFactor();
        updateFoamScroll();
    }

    private void updateFoamScroll() {
        // Slowly increment offset UVs for foam texturing
        u_Offset += 1 / 512f;

        // Wrap it back... this should not happen often unless the game is running for a very long time
        // but will cause a slight jitter in the foam pattern when this resets
        if (u_Offset > 10000) {
            u_Offset = 0.0f;
        }

        waterAsset.water.setFloatAttribute(WaterFloatAttribute.FoamUVOffset, u_Offset);
    }

    private void updateMoveFactor() {
        float waveSpeed = waterAsset.water.getFloatAttribute(WaterFloatAttribute.WaveSpeed);
        moveFactor += waveSpeed * Gdx.graphics.getDeltaTime();
        moveFactor %= 1;
        waterAsset.water.setFloatAttribute(WaterFloatAttribute.MoveFactor, moveFactor);
    }

    @Override
    public Component clone(GameObject go) {
        throw new GdxRuntimeException("Duplicating water is not supported.");
    }
}
