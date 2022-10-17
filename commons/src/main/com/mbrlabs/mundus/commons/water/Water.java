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
import com.mbrlabs.mundus.commons.water.attributes.WaterFloatAttributeU;
import com.mbrlabs.mundus.commons.water.attributes.WaterMaterialAttribute;
import com.mbrlabs.mundus.commons.water.attributes.WaterTextureAttributeU;

public class Water implements RenderableProvider, Disposable {
    public static final int DEFAULT_SIZE = 1600;
    public static final float DEFAULT_TILING = 0.04f;
    public static final float DEFAULT_WAVE_STRENGTH = 0.04f;
    public static final float DEFAULT_WAVE_SPEED = 0.03f;
    public static final float DEFAULT_REFLECTIVITY = 0.6f;
    public static final float DEFAULT_SHINE_DAMPER = 20.0f;
    public static final float DEFAULT_FOAM_SCALE = 0.8f;
    public static final float DEFAULT_FOAM_EDGE_BIAS = 0.0f;
    public static final float DEFAULT_FOAM_EDGE_DISTANCE = 0.2f;
    public static final float DEFAULT_FOAM_FALL_OFF_DISTANCE = 12.0f;
    public static final float DEFAULT_FOAM_SCROLL_SPEED = 4.0f;
    private static final String materialId = "waterMat";


    public Matrix4 transform;
    public int waterWidth;
    public int waterDepth;

    private WaterMaterial waterMaterial;

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
        waterMaterial = new WaterMaterial();

        // Attach our custom water material to the main material
        Material material = modelInstance.getMaterial(materialId);
        material.set(WaterMaterialAttribute.createWaterMaterialAttribute(waterMaterial));

        // Set default values
        setFloatAttribute(WaterFloatAttributeU.Tiling, DEFAULT_TILING);
        setFloatAttribute(WaterFloatAttributeU.WaveStrength, DEFAULT_WAVE_STRENGTH);
        setFloatAttribute(WaterFloatAttributeU.WaveSpeed, DEFAULT_WAVE_SPEED);
        setFloatAttribute(WaterFloatAttributeU.FoamPatternScale, Water.DEFAULT_FOAM_SCALE);
        setFloatAttribute(WaterFloatAttributeU.FoamScrollSpeed, Water.DEFAULT_FOAM_SCROLL_SPEED);
        setFloatAttribute(WaterFloatAttributeU.FoamEdgeDistance, Water.DEFAULT_FOAM_EDGE_DISTANCE);
        setFloatAttribute(WaterFloatAttributeU.FoamEdgeBias, Water.DEFAULT_FOAM_EDGE_BIAS);
        setFloatAttribute(WaterFloatAttributeU.FoamFallOffDistance, Water.DEFAULT_FOAM_FALL_OFF_DISTANCE);
        setFloatAttribute(WaterFloatAttributeU.Reflectivity, DEFAULT_REFLECTIVITY);
        setFloatAttribute(WaterFloatAttributeU.ShineDamper, DEFAULT_SHINE_DAMPER);
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
        waterMaterial.set(new WaterTextureAttributeU(WaterTextureAttributeU.Reflection, texture));
    }

    public void setFoamTexture(Texture texture) {
        waterMaterial.set(new WaterTextureAttributeU(WaterTextureAttributeU.Foam, texture));
    }

    public void setDudvTexture(Texture texture) {
        waterMaterial.set(new WaterTextureAttributeU(WaterTextureAttributeU.Dudv, texture));
    }

    public void setNormalMap(Texture texture) {
        waterMaterial.set(new WaterTextureAttributeU(WaterTextureAttributeU.NormalMap, texture));
    }

    public void setWaterRefractionTexture(Texture texture) {
        waterMaterial.set(new WaterTextureAttributeU(WaterTextureAttributeU.Refraction, texture));
    }

    public void setWaterRefractionDepthTexture(Texture texture) {
        waterMaterial.set(new WaterTextureAttributeU(WaterTextureAttributeU.RefractionDepth, texture));
    }

    public void setFloatAttribute(long attributeType, float value) {
        waterMaterial.set(new WaterFloatAttributeU(attributeType, value));
    }

    public float getFloatAttribute(long attributeType) {
         return waterMaterial.get(WaterFloatAttributeU.class, attributeType).value;
    }
}
