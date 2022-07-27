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

package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.shaders.ClippableShader;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 18-01-2016
 */
public class TerrainComponent extends CullableComponent implements AssetUsage, ClippableComponent {

    private static final String TAG = TerrainComponent.class.getSimpleName();

    protected TerrainAsset terrain;
    protected Shader shader;

    public TerrainComponent(GameObject go, Shader shader) {
        super(go);
        this.shader = shader;
        type = Component.Type.TERRAIN;
    }

    public void updateUVs(Vector2 uvScale) {
        terrain.updateUvScale(uvScale);
    }

    public void setTerrain(TerrainAsset terrain) {
        this.terrain = terrain;
        setDimensions(terrain.getTerrain().modelInstance);
    }

    public TerrainAsset getTerrain() {
        return terrain;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (isCulled) return;
        gameObject.sceneGraph.scene.batch.render(terrain.getTerrain(), gameObject.sceneGraph.scene.environment, shader);
    }

    @Override
    public void render(float delta, Vector3 clippingPlane, float clipHeight) {
        if (shader instanceof ClippableShader) {
            ((ClippableShader) shader).setClippingPlane(clippingPlane);
            ((ClippableShader) shader).setClippingHeight(clipHeight);
        }
        render(delta);
    }

    @Override
    public void renderDepth(float delta, Vector3 clippingPlane, float clipHeight, Shader shader) {
        if (isCulled) return;

        if (shader instanceof ClippableShader) {
            ((ClippableShader) shader).setClippingPlane(clippingPlane);
            ((ClippableShader) shader).setClippingHeight(clipHeight);
        }

        gameObject.sceneGraph.scene.depthBatch.render(terrain.getTerrain(), gameObject.sceneGraph.scene.environment, shader);
    }

    @Override
    public Component clone(GameObject go) {
        // Cant be cloned right now
        return null;
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (Objects.equals(terrain.getID(), assetToCheck.getID()))
            return true;

        return terrain.usesAsset(assetToCheck);
    }
}
