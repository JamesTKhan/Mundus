package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Shader;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.WaterAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class WaterComponent extends AbstractComponent implements AssetUsage {

    protected WaterAsset waterAsset;
    protected Shader shader;

    public WaterComponent(GameObject go, Shader shader) {
        super(go);
        this.shader = shader;
        type = Component.Type.WATER;
    }

    public WaterAsset getWaterAsset() {
        return waterAsset;
    }

    public void setWaterAsset(WaterAsset waterAsset) {
        this.waterAsset = waterAsset;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        return false;
    }

    @Override
    public void render(float delta) {
        gameObject.sceneGraph.scene.batch.render(waterAsset.water, gameObject.sceneGraph.scene.environment, shader);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }
}
