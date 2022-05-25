package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.WaterAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.water.WaterFloatAttribute;

public class WaterComponent extends AbstractComponent implements AssetUsage {

    protected WaterAsset waterAsset;
    protected Shader shader;
    private float moveFactor = 0;
    private float u_Offset = 0;

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
        return null;
    }
}
