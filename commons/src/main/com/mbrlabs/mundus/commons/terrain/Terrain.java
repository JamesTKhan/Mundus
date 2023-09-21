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

package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.terrain.attributes.TerrainMaterialAttribute;
import com.mbrlabs.mundus.commons.utils.MathUtils;

import static com.badlogic.gdx.math.MathUtils.lerp;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class Terrain implements Disposable {

    public static final int DEFAULT_SIZE = 400;
    public static final int DEFAULT_VERTEX_RESOLUTION = 64;
    public static final int DEFAULT_UV_SCALE = 60;
    public static final int DEFAULT_LODS = 4;
    public static final float DEFAULT_LOD_THRESHOLD = 1200f;

    private static final Vector3 c00 = new Vector3();
    private static final Vector3 c01 = new Vector3();
    private static final Vector3 c10 = new Vector3();
    private static final Vector3 c11 = new Vector3();
    private static final Vector3 tmp = new Vector3();
    private static final Vector2 tmpV2 = new Vector2();
    private static final Matrix4 tmpMatrix = new Matrix4();

    public float[] heightData;
    public int terrainWidth = DEFAULT_SIZE;
    public int terrainDepth = DEFAULT_SIZE;
    public int vertexResolution;
    public float lodThreshold;
    public int lodLevels;
    public int currentLod;
    public boolean terraformed;
    public boolean terrainModified;

    // used for building the mesh
    private final VertexAttributes attribs;
    private Vector2 uvScale = new Vector2(DEFAULT_UV_SCALE, DEFAULT_UV_SCALE);

    // Textures
    private TerrainMaterial terrainMaterial;
    private final Material material;

    // Model and mesh
    private Model[] models;
    public static float[] thresholds;
    private PlaneMesh planeMesh;

    private Terrain(int vertexResolution) {
        this.attribs = new VertexAttributes(
                VertexAttribute.Position(),
                VertexAttribute.Normal(),
                new VertexAttribute(VertexAttributes.Usage.Tangent, 4, ShaderProgram.TANGENT_ATTRIBUTE),
                VertexAttribute.TexCoords(0)
        );

        this.vertexResolution = vertexResolution;
        this.heightData = new float[vertexResolution * vertexResolution];

        this.terrainMaterial = new TerrainMaterial();
        this.terrainMaterial.setTerrain(this);

        // Attach our custom terrain material to the main material
        material = new Material();
        material.set(TerrainMaterialAttribute.createTerrainMaterialAttribute(terrainMaterial));
    }

    public Terrain(int size, float[] heightData) {
        this((int) Math.sqrt(heightData.length));
        this.terrainWidth = size;
        this.terrainDepth = size;
        this.heightData = heightData;
    }

    public void init() {
        Gdx.app.log("T", "Init");
        models = new Model[lodLevels];
        thresholds = new float[lodLevels];

        PlaneMesh.MeshInfo info = new PlaneMesh.MeshInfo();
        info.attribs = attribs;
        info.vertexResolution = vertexResolution;
        info.heightData = heightData;
        info.width = terrainWidth;
        info.depth = terrainDepth;
        info.uvScale = uvScale;

        planeMesh = new PlaneMesh(info);
        //generate base model vertices
        Mesh mesh = planeMesh.buildMesh(false);
        planeMesh.calculateAverageNormals();
        planeMesh.computeTangents();
        planeMesh.updateMeshVertices();
        planeMesh.resetBoundingBox();

        MeshPart meshPart = new MeshPart(null, mesh, 0, mesh.getNumIndices(), GL20.GL_TRIANGLES);
        meshPart.update();
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.part(meshPart, material);
        models[0] = mb.end();
        computeThresholds();
    }

    public Vector3 getVertexPosition(Vector3 out, int x, int z) {
        final float dx = (float) x / (float) (vertexResolution - 1);
        final float dz = (float) z / (float) (vertexResolution - 1);
        final float height = heightData[z * vertexResolution + x];
        out.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        return out;
    }

    /**
     * Returns the terrain height at the given world coordinates, in world coordinates.
     *
     * @param worldX X world position to get height
     * @param worldZ Z world position to get height
     * @param terrainTransform The world transform (modelInstance transform) of the terrain
     * @return
     */
    public float getHeightAtWorldCoord(float worldX, float worldZ, Matrix4 terrainTransform) {
        // Translates world coordinates to local coordinates
        tmp.set(worldX, 0f, worldZ).mul(tmpMatrix.set(terrainTransform).inv());

        float height = getHeightAtLocalCoord(tmp.x, tmp.z);

        // Translates to world coordinate
        height *= terrainTransform.getScale(tmp).y;
        return height;
    }

    public float getHeightAtLocalCoord(float terrainX, float terrainZ) {
        float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        // Check if we are outside the terrain, if so use nearest point
        gridX = com.badlogic.gdx.math.MathUtils.clamp(gridX, 0, vertexResolution - 2);
        gridZ = com.badlogic.gdx.math.MathUtils.clamp(gridZ, 0, vertexResolution - 2);

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        c01.set(1, heightData[(gridZ + 1) * vertexResolution + gridX], 0);
        c10.set(0, heightData[gridZ * vertexResolution + gridX + 1], 1);

        float height;
        if (xCoord <= (1 - zCoord)) { // we are in upper left triangle of the square
            c00.set(0, heightData[gridZ * vertexResolution + gridX], 0);
            height = MathUtils.barryCentric(c00, c10, c01, tmpV2.set(zCoord, xCoord));
        } else { // bottom right triangle
            c11.set(1, heightData[(gridZ + 1) * vertexResolution + gridX + 1], 1);
            height = MathUtils.barryCentric(c10, c11, c01, tmpV2.set(zCoord, xCoord));
        }

        return height;
    }

    /**
     * Casts the given ray to determine where it intersects on the terrain.
     *
     * @param out Vector3 to populate with intersect point with
     * @param ray the ray to cast
     * @param terrainTransform The world transform (modelInstance transform) of the terrain
     * @return true if the ray intersects the terrain, false otherwise
     */
    public boolean getRayIntersection(Vector3 out, Ray ray, Matrix4 terrainTransform) {
        // Performs a binary search to find the intersection point
        float minDistance = 2;
        float maxDistance = 80000;

        // Interval halving
        for(int i = 0; i < 500; i++) {
            float middleDistance = (minDistance + maxDistance) / 2;
            ray.getEndPoint(out, middleDistance);

            if(isUnderTerrain(out, terrainTransform)) {
                maxDistance = middleDistance;
            } else {
                minDistance = middleDistance;
            }

            // If min and max are very close, we found the intersection
            if(Math.abs(minDistance - maxDistance) < 0.1f) {
                return true;
            }
        }

        return false;
    }

    public Material getMaterial() {
        return material;
    }

    public void modifyVertex(int x, int z) {
        planeMesh.modifyVertex(x, z);
    }

    public void updateUvScale(Vector2 uvScale) {
        this.uvScale = uvScale;
    }

    public void updateLodData(int lodLevels, float lodThreshold){
        this.lodLevels = lodLevels;
        this.lodThreshold = lodThreshold;
        this.currentLod = 0;
    }

    public Vector2 getUvScale() {
        return uvScale;
    }

    /**
     * Get normal at world coordinates. The methods calculates exact point
     * position in terrain coordinates and returns normal at that point. If
     * point doesn't belong to terrain -- it returns default
     * <code>Vector.Y<code> normal.
     *
     * @param worldX
     *            the x coord in world
     * @param worldZ
     *            the z coord in world
     * @param terrainTransform
     *             The world transform (modelInstance transform) of the terrain
     * @return normal at that point. If point doesn't belong to terrain -- it
     *         returns default <code>Vector.Y<code> normal.
     */
    public Vector3 getNormalAtWordCoordinate(Vector3 out, float worldX, float worldZ, Matrix4 terrainTransform) {
        // Translates world coordinates to local coordinates
        tmp.set(worldX, 0f, worldZ).mul(tmpMatrix.set(terrainTransform).inv());

        float terrainX = tmp.x;
        float terrainZ = tmp.z;

        float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) {
            return Vector3.Y.cpy();
        }

        return planeMesh.getNormalAt(out, gridX, gridZ);
    }

    /**
     * Checks if given world coordinates are above or below the terrain
     * @param worldCoords the world coordinates to check
     * @param terrainTransform the world transform (modelInstance transform) of the terrain
     * @return boolean true if under the terrain, else false
     */
    public boolean isUnderTerrain(Vector3 worldCoords, Matrix4 terrainTransform) {
        // Factor in world height position as well via getPosition.
        float terrainHeight = getHeightAtWorldCoord(worldCoords.x, worldCoords.z, terrainTransform) + terrainTransform.getTranslation(tmp).y;
        return terrainHeight > worldCoords.y;
    }

    /**
     * Determines if the world coordinates are within the terrains X and Z boundaries, does not including height
     * @param worldX worldX to check
     * @param worldZ worldZ to check
     * @param terrainTransform the world transform (modelInstance transform) of the terrain
     * @return boolean true if within the terrains boundary, else false
     */
    public boolean isOnTerrain(float worldX, float worldZ, Matrix4 terrainTransform) {
        // Translates world coordinates to local coordinates
        tmp.set(worldX, 0f, worldZ).mul(tmpMatrix.set(terrainTransform).inv());
        return 0 <= tmp.x && tmp.x <= terrainWidth && 0 <= tmp.z && tmp.z <= terrainDepth;
    }

    public TerrainMaterial getTerrainTexture() {
        return terrainMaterial;
    }

    public void setTerrainTexture(TerrainMaterial terrainMaterial) {
        if (terrainMaterial == null) return;

        terrainMaterial.setTerrain(this);
        this.terrainMaterial = terrainMaterial;

        material.set(TerrainMaterialAttribute.createTerrainMaterialAttribute(terrainMaterial));
    }

    public float[] getVertices() {
        return planeMesh.getVertices();
    }

    public void update() {
        planeMesh.buildVertices();
        planeMesh.calculateAverageNormals();
        planeMesh.computeTangents();
        planeMesh.updateMeshVertices();
        planeMesh.resetBoundingBox();
    }

    public Model getModel(int index) {
        if (models[index] == null){
            models[index] = createLod(index);
        }
        return models[index];
    }


    public void clearLodModels(){
        terrainModified = true;
        for (int i = 1; i < lodLevels; i++) {
            models[i] = null;
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < lodLevels; i++)
            if (models[i] != null)
                models[i].dispose();
        planeMesh.dispose();
    }

    /**
     * Returns the plane mesh used by the terrain
     * @return the plane mesh
     */
    public PlaneMesh getPlaneMesh() {
        return planeMesh;
    }

    //find closest divisible factor (not currently implemented, will be helpful to align vertices in the future)
    public int findClosestFactor(int number) {
        int half = number / 2;
        for (int i = half; i > 0; i--) {
            if (number % i == 0) {
                return i;
            }
        }
        return 1; // In case the number is a prime number, return 1
    }

    public Model createLod(int index) {
            int newResolution = vertexResolution / (index + 1);

            float[] newHeightData = new float[newResolution * newResolution];

            int step = vertexResolution / newResolution;

            for (int j = 0; j < newResolution; j++) {
                for (int k = 0; k < newResolution; k++) {
                    // Sampling the original heightData
                    newHeightData[k * newResolution + j] = heightData[(k * step) * vertexResolution + (j * step)];
                }
            }

            PlaneMesh.MeshInfo info = new PlaneMesh.MeshInfo();
            info.attribs = attribs;
            info.vertexResolution = newResolution;
            info.heightData = newHeightData;
            info.width = terrainWidth;
            info.depth = terrainDepth;
            info.uvScale = uvScale;

            PlaneMesh generator = new PlaneMesh(info);
            Mesh mesh = generator.buildMesh(index == 0 ? false : true);
            generator.calculateAverageNormals();
            generator.computeTangents();
            generator.updateMeshVertices();
            generator.resetBoundingBox();

            MeshPart meshPart = new MeshPart(null, mesh, 0, mesh.getNumIndices(), GL20.GL_TRIANGLES);
            meshPart.update();
            ModelBuilder mb = new ModelBuilder();
            mb.begin();
            mb.part(meshPart, material);
            return mb.end();
    }
    public void computeThresholds() {
        thresholds = new float[lodLevels];
        thresholds[0] = lodThreshold;

        for (int i = 1; i < lodLevels; i++){
            thresholds[i] = thresholds[i-1] * (i+1);
            Gdx.app.log("T", "Threshold: " + thresholds[i]);
        }
    }
}
