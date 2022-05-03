package com.mbrlabs.mundus.commons.water;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class Water implements RenderableProvider, Disposable {
    public static final int DEFAULT_SIZE = 1600;

    public Matrix4 transform;
    public int waterWidth;
    public int waterDepth;

    // Textures
    public Texture waterRefractionTexture;
    private final Material material;

    // Mesh
    private Model model;
    public ModelInstance modelInstance;
    public Mesh mesh;

    public Water(int size) {
        this.waterWidth = size;
        this.waterDepth = size;
        this.transform = new Matrix4();
        material = new Material("texture");
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
                | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);
        builder.rect(waterPos.x, 0, waterPos.z + waterDepth, waterPos.x + waterWidth, 0, waterPos.z + waterDepth, waterPos.x + waterWidth, 0, waterPos.z, waterPos.x, 0, waterPos.z, 0, 1, 0);
        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
        modelInstance.transform = transform;
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
        modelInstance.getMaterial("texture").set(new TextureAttribute(TextureAttribute.Diffuse, texture));
    }

    public void setDudvTexture(Texture texture) {
        modelInstance.getMaterial("texture").set(new WaterTextureAttribute(WaterTextureAttribute.Dudv, texture));
    }

    public void setNormalMap(Texture texture) {
        modelInstance.getMaterial("texture").set(new WaterTextureAttribute(WaterTextureAttribute.NormalMap, texture));
    }

    public void setWaterRefractionTexture(Texture texture) {
        waterRefractionTexture = texture;
        modelInstance.getMaterial("texture").set(new TextureAttribute(TextureAttribute.Normal, waterRefractionTexture));
    }

}
