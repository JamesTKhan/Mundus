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

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.terrain.attributes.TerrainMaterialAttribute;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import net.mgsx.gltf.loaders.shared.geometry.MeshTangentSpaceGenerator;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class Terrain implements Disposable {

    public static final int DEFAULT_SIZE = 1200;
    public static final int DEFAULT_VERTEX_RESOLUTION = 180;
    public static final int DEFAULT_UV_SCALE = 60;

    private static final MeshPartBuilder.VertexInfo tempVertexInfo = new MeshPartBuilder.VertexInfo();
    private static final Vector3 c00 = new Vector3();
    private static final Vector3 c01 = new Vector3();
    private static final Vector3 c10 = new Vector3();
    private static final Vector3 c11 = new Vector3();
    private static final Vector3 tmp = new Vector3();
    private static final Vector2 tmpV2 = new Vector2();
    private static final Matrix4 tmpMatrix = new Matrix4();

    public float[] heightData;
    public int terrainWidth = 1200;
    public int terrainDepth = 1200;
    public int vertexResolution;

    // used for building the mesh
    private final VertexAttributes attribs;
    private Vector2 uvScale = new Vector2(DEFAULT_UV_SCALE, DEFAULT_UV_SCALE);
    private float[] vertices;
    private final int stride;
    private final int posPos;
    private final int norPos;
    private final int uvPos;

    // Textures
    private TerrainMaterial terrainMaterial;
    private Material material;

    // Mesh
    private Model model;
    private Mesh mesh;

    private Terrain(int vertexResolution) {
        this.attribs = new VertexAttributes(
                VertexAttribute.Position(),
                VertexAttribute.Normal(),
                new VertexAttribute(VertexAttributes.Usage.Tangent, 4, ShaderProgram.TANGENT_ATTRIBUTE),
                VertexAttribute.TexCoords(0)
        );

        this.posPos = attribs.getOffset(VertexAttributes.Usage.Position, -1);
        this.norPos = attribs.getOffset(VertexAttributes.Usage.Normal, -1);
        this.uvPos = attribs.getOffset(VertexAttributes.Usage.TextureCoordinates, -1);
        this.stride = attribs.vertexSize / 4;

        this.vertexResolution = vertexResolution;
        this.heightData = new float[vertexResolution * vertexResolution];

        this.terrainMaterial = new TerrainMaterial();
        this.terrainMaterial.setTerrain(this);

        // Attach our custom water material to the main material
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
        final int numVertices = this.vertexResolution * vertexResolution;
        final int numIndices = (this.vertexResolution - 1) * (vertexResolution - 1) * 6;

        mesh = new Mesh(true, numVertices, numIndices, attribs);
        this.vertices = new float[numVertices * stride];
        mesh.setIndices(buildIndices());
        buildVertices();
        mesh.setVertices(vertices);

        MeshPart meshPart = new MeshPart(null, mesh, 0, numIndices, GL20.GL_TRIANGLES);
        meshPart.update();
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.part(meshPart, material);
        model = mb.end();
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
        // Translates word coordinates to local coordinates
        tmp.set(worldX, 0f, worldZ).mul(tmpMatrix.set(terrainTransform).inv());

        float terrainX = tmp.x;
        float terrainZ = tmp.z;

        float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        c01.set(1, heightData[(gridZ + 1) * vertexResolution + gridX], 0);
        c10.set(0, heightData[gridZ * vertexResolution + gridX + 1], 1);

        // we are in upper left triangle of the square
        if (xCoord <= (1 - zCoord)) {
            c00.set(0, heightData[gridZ * vertexResolution + gridX], 0);
            return MathUtils.barryCentric(c00, c10, c01, tmpV2.set(zCoord, xCoord));
        }
        // bottom right triangle
        c11.set(1, heightData[(gridZ + 1) * vertexResolution + gridX + 1], 1);
        return MathUtils.barryCentric(c10, c11, c01, tmpV2.set(zCoord, xCoord));
    }

    /**
     * Casts the given ray to determine where it intersects on the terrain.
     *
     * @param out Vector3 to populate with intersect point with
     * @param ray the ray to cast
     * @param terrainTransform The world transform (modelInstance transform) of the terrain
     * @return
     */
    public Vector3 getRayIntersection(Vector3 out, Ray ray, Matrix4 terrainTransform) {
        // TODO improve performance. use binary search
        float curDistance = 2;
        int rounds = 0;

        ray.getEndPoint(out, curDistance);
        boolean isUnder = isUnderTerrain(out, terrainTransform);

        while (true) {
            rounds++;
            ray.getEndPoint(out, curDistance);

            boolean u = isUnderTerrain(out, terrainTransform);
            if (u != isUnder || rounds == 20000) {
                return out;
            }
            curDistance += u ? -0.1f : 0.1f;
        }

    }

    public Material getMaterial() {
        return material;
    }

    private short[] buildIndices() {
        final int w = vertexResolution - 1;
        final int h = vertexResolution - 1;
        short[] indices = new short[w * h * 6];
        int i = -1;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final int c00 = y * vertexResolution + x;
                final int c10 = c00 + 1;
                final int c01 = c00 + vertexResolution;
                final int c11 = c10 + vertexResolution;
                indices[++i] = (short) c11;
                indices[++i] = (short) c10;
                indices[++i] = (short) c00;
                indices[++i] = (short) c00;
                indices[++i] = (short) c01;
                indices[++i] = (short) c11;
            }
        }
        return indices;
    }

    private void buildVertices() {
        for (int x = 0; x < vertexResolution; x++) {
            for (int z = 0; z < vertexResolution; z++) {
                calculateVertexAt(tempVertexInfo, x, z);
                calculateNormalAt(tempVertexInfo, x, z);
                setVertex(z * vertexResolution + x, tempVertexInfo);
            }
        }
    }

    private void setVertex(int index, MeshPartBuilder.VertexInfo info) {
        index *= stride;
        if (posPos >= 0) {
            vertices[index + posPos] = info.position.x;
            vertices[index + posPos + 1] = info.position.y;
            vertices[index + posPos + 2] = info.position.z;
        }
        if (uvPos >= 0) {
            vertices[index + uvPos] = info.uv.x;
            vertices[index + uvPos + 1] = info.uv.y;
        }
        if (norPos >= 0) {
            vertices[index + norPos] = info.normal.x;
            vertices[index + norPos + 1] = info.normal.y;
            vertices[index + norPos + 2] = info.normal.z;
        }
    }

    private MeshPartBuilder.VertexInfo calculateVertexAt(MeshPartBuilder.VertexInfo out, int x, int z) {
        final float dx = (float) x / (float) (vertexResolution - 1);
        final float dz = (float) z / (float) (vertexResolution - 1);
        final float height = heightData[z * vertexResolution + x];

        out.position.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        out.uv.set(dx, dz).scl(uvScale);

        return out;
    }

    public void updateUvScale(Vector2 uvScale) {
        this.uvScale = uvScale;
    }

    public Vector2 getUvScale() {
        return uvScale;
    }

    /**
     * Calculates normal of a vertex at x,y based on the verticesOnZ of the
     * surrounding vertices
     */
    private MeshPartBuilder.VertexInfo calculateNormalAt(MeshPartBuilder.VertexInfo out, int x, int y) {
        getNormalAt(out.normal, x, y);
        return out;
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
        // Translates word coordinates to local coordinates
        tmp.set(worldX, 0f, worldZ).mul(tmpMatrix.set(terrainTransform).inv());

        float terrainX = tmp.x;
        float terrainZ = tmp.z;

        float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) {
            return Vector3.Y.cpy();
        }

        return getNormalAt(out, gridX, gridZ);
    }

    /**
     * Get Normal at x,y point of terrain
     *
     * @param out
     *            Output vector
     * @param x
     *            the x coord on terrain
     * @param y
     *            the y coord on terrain( actual z)
     * @return the normal at the point of terrain
     */
    public Vector3 getNormalAt(Vector3 out, int x, int y) {
        // handle edges of terrain
        int xP1 = (x + 1 >= vertexResolution) ? vertexResolution - 1 : x + 1;
        int yP1 = (y + 1 >= vertexResolution) ? vertexResolution - 1 : y + 1;
        int xM1 = (x - 1 < 0) ? 0 : x - 1;
        int yM1 = (y - 1 < 0) ? 0 : y - 1;

        float hL = heightData[y * vertexResolution + xM1];
        float hR = heightData[y * vertexResolution + xP1];
        float hD = heightData[yM1 * vertexResolution + x];
        float hU = heightData[yP1 * vertexResolution + x];
        out.x = hL - hR;
        out.y = 2;
        out.z = hD - hU;
        out.nor();
        return out;
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
        // Translates word coordinates to local coordinates
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
        return vertices;
    }

    public void update() {
        buildVertices();

        VertexAttribute normalMapUVs = null;
        for(VertexAttribute a : attribs){
            if(a.usage == VertexAttributes.Usage.TextureCoordinates){
                normalMapUVs = a;
            }
        }
        // Get tangents added to terrains vertices array for normal mapping
        MeshTangentSpaceGenerator.computeTangentSpace(vertices, buildIndices(), attribs, false, true, normalMapUVs);

        mesh.setVertices(vertices);
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void dispose() {

    }

}
