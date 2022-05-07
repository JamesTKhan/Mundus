package com.mbrlabs.mundus.commons.water;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class Water implements RenderableProvider, Disposable {
    public static final int DEFAULT_SIZE = 1600;
    public static final float DEFAULT_TILING = 0.04f;
    public static final float DEFAULT_WAVE_STRENGTH = 0.04f;
    public static final float DEFAULT_WAVE_SPEED = 0.03f;
    public static final float DEFAULT_REFLECTIVITY = 0.6f;
    public static final float DEFAULT_SHINE_DAMPER = 20.0f;
    private static final String materialId = "waterMat";


    public Matrix4 transform;
    public int waterWidth;
    public int waterDepth;

    // Textures
    public Texture waterRefractionTexture;
    private Material material;

    // Mesh
    private Model model;
    public ModelInstance modelInstance;

    public Water(int size) {
        this.waterWidth = size;
        this.waterDepth = size;
        this.transform = new Matrix4();
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
        modelInstance.transform = this.transform;
    }

    public void init() {
        Vector3 waterPos = new Vector3(0f,0f,0f);
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder builder = modelBuilder.part("water", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, new Material(materialId));
        builder.rect(waterPos.x, 0, waterPos.z + waterDepth, waterPos.x + waterWidth, 0, waterPos.z + waterDepth, waterPos.x + waterWidth, 0, waterPos.z, waterPos.x, 0, waterPos.z, 0, 1, 0);
        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
        modelInstance.transform = transform;

        // Hold reference to the material
        material = modelInstance.getMaterial(materialId);

        setTiling(DEFAULT_TILING);
        setWaveStrength(DEFAULT_WAVE_STRENGTH);
        setWaveSpeed(DEFAULT_WAVE_SPEED);
        setReflectivity(DEFAULT_REFLECTIVITY);
        setShineDamper(DEFAULT_SHINE_DAMPER);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        modelInstance.getRenderables(renderables, pool);
    }

    @Override
    public void dispose() {
        model.dispose();
    }

    public void setWaterReflection(Texture texture) {
        material.set(new WaterTextureAttribute(WaterTextureAttribute.Reflection, texture));
    }

    public void setDudvTexture(Texture texture) {
        material.set(new WaterTextureAttribute(WaterTextureAttribute.Dudv, texture));
    }

    public void setNormalMap(Texture texture) {
        material.set(new WaterTextureAttribute(WaterTextureAttribute.NormalMap, texture));
    }

    public void setWaterRefractionTexture(Texture texture) {
        waterRefractionTexture = texture;
        material.set(new WaterTextureAttribute(WaterTextureAttribute.Refraction, waterRefractionTexture));
    }

    public void setTiling(float tiling) {
        material.set(new WaterFloatAttribute(WaterFloatAttribute.Tiling, tiling));
    }

    public float getTiling() {
        return material.get(WaterFloatAttribute.class, WaterFloatAttribute.Tiling).value;
    }

    public void setWaveStrength(float strength) {
        material.set(new WaterFloatAttribute(WaterFloatAttribute.WaveStrength, strength));
    }

    public float getWaveStrength() {
        return material.get(WaterFloatAttribute.class, WaterFloatAttribute.WaveStrength).value;
    }

    public void setWaveSpeed(float speed) {
        material.set(new WaterFloatAttribute(WaterFloatAttribute.WaveSpeed, speed));
    }

    public float getWaveSpeed() {
        return material.get(WaterFloatAttribute.class, WaterFloatAttribute.WaveSpeed).value;
    }

    public void setReflectivity(float reflectivity) {
        material.set(new WaterFloatAttribute(WaterFloatAttribute.Reflectivity, reflectivity));
    }

    public float getReflectivity() {
        return material.get(WaterFloatAttribute.class, WaterFloatAttribute.Reflectivity).value;
    }

    public void setShineDamper(float shineDamper) {
        material.set(new WaterFloatAttribute(WaterFloatAttribute.ShineDamper, shineDamper));
    }

    public float getShineDamper() {
        return material.get(WaterFloatAttribute.class, WaterFloatAttribute.ShineDamper).value;
    }

}
