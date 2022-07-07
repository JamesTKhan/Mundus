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

package com.mbrlabs.mundus.commons.physics.bullet.builders;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.physics.enums.PhysicsShape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author James Pooley
 * @version July 07, 2022
 */
public class ShapeBuilder {
    private static final Vector3 dimensions = new Vector3();

    private final PhysicsShape shapeEnum;
    private BoundingBox boundingBox = null;
    private Vector3 scale = null;
    private Model model = null;
    private TerrainAsset terrainAsset = null;
    private ShapeBuilderResult shapeBuilderResult;

    private float radius = 1f;

    public ShapeBuilder(PhysicsShape shapeEnum) {
        this.shapeEnum = shapeEnum;
    }

    public ShapeBuilder boundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }

    public ShapeBuilder scale(Vector3 scale) {
        this.scale = scale;
        return this;
    }

    public ShapeBuilder model(Model model) {
        this.model = model;
        return this;
    }

    public ShapeBuilder terrainAsset(TerrainAsset terrainAsset) {
        this.terrainAsset = terrainAsset;
        return this;
    }

    public ShapeBuilderResult build() {

        // Get dimensions needed for shape building
        if (boundingBox != null) {
            // Get the dimensions
            boundingBox.getDimensions(dimensions);

            dimensions.scl(0.5f);// Scale for half extents

            // Handle scale
            if (scale != null) {
                dimensions.scl(scale);
            }

            radius = dimensions.len() / 2f;
        } else {
            dimensions.set(1f,1f,1f);
        }

        shapeBuilderResult = new ShapeBuilderResult();

        switch (shapeEnum) {
            case BOX:
                return buildBoxShape();
            case SPHERE:
                return buildSphereShape();
            case CAPSULE:
                return buildCapsuleShape();
            case CYLINDER:
                return buildCylinderShape();
            case CONE:
                return buildConeShape();
            case CONVEX_HULL:
                return buildConvexHullShape();
            case G_IMPACT_TRIANGLE_MESH:
                return buildGimpactShape();
            case TERRAIN:
                return buildTerrainShape();
            case SCALED_BVH_TRIANGLE:
                throw new GdxRuntimeException("SCALED_BVH_TRIANGLE shape not implemented for manual creation.");
            case BVH_TRIANGLE:
                throw new GdxRuntimeException("BVH_TRIANGLE shape not implemented for manual creation.");
        }

        return null;
    }

    private ShapeBuilderResult buildGimpactShape() {
        if (model == null) {
            throw new GdxRuntimeException("Model is required for ShapeBuilder to create gimpact shapes.");
        }
        shapeBuilderResult.vertexArray = new btTriangleIndexVertexArray(model.meshParts);

        shapeBuilderResult.shape = new btGImpactMeshShape(shapeBuilderResult.vertexArray);

        if (scale != null) {
            shapeBuilderResult.shape.setLocalScaling(scale);
        }

        ((btGImpactMeshShape) shapeBuilderResult.shape).updateBound();
        return shapeBuilderResult;
    }

    private ShapeBuilderResult buildTerrainShape() {
        if (terrainAsset == null) {
            throw new GdxRuntimeException("TerrainAsset object is required for ShapeBuilder to create terrain shapes.");
        }

        float minHeight = 999;// min height for entire chunk
        float maxHeight = -999;// max height for entire chunk
        for (float height : terrainAsset.getData()) {
            // Set height min/maxes for bullet
            if (height < minHeight)
                minHeight = height;
            else if (height > maxHeight)
                maxHeight = height;
        }

        terrainAsset.setMaxHeight(maxHeight);
        terrainAsset.setMinHeight(minHeight);

        ByteBuffer vbb = ByteBuffer.allocateDirect(terrainAsset.getData().length * 4);
        vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order

        shapeBuilderResult.fb = vbb.asFloatBuffer();  // create floating point buffer using bytebuffer
        shapeBuilderResult.fb.put(terrainAsset.getData()); // add height data to buffer
        shapeBuilderResult.fb.position(0);

        float size = terrainAsset.getTerrain().terrainWidth;
        float vertexCount = terrainAsset.getTerrain().vertexResolution;
        shapeBuilderResult.shape = new btHeightfieldTerrainShape(terrainAsset.getTerrain().vertexResolution, terrainAsset.getTerrain().vertexResolution, shapeBuilderResult.fb, 1, minHeight, maxHeight, 1, true);
        shapeBuilderResult.shape.setLocalScaling(new Vector3((size) / ((vertexCount - 1)), 1, (size) / ((vertexCount - 1))));

        return shapeBuilderResult;
    }

    private ShapeBuilderResult buildConvexHullShape() {
        if (model == null) {
            throw new GdxRuntimeException("Model is required for ShapeBuilder to create convex hull shapes.");
        }

        shapeBuilderResult.convexHullShapes = new Array<>();
        shapeBuilderResult.shape = new btCompoundShape();

        for (MeshPart meshPart : model.meshParts) {
            Mesh mesh = meshPart.mesh;
            btConvexHullShape convexHullShape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());

            final btShapeHull hull = new btShapeHull(convexHullShape);
            hull.buildHull(convexHullShape.getMargin());
            final btConvexHullShape result = new btConvexHullShape(hull);
            ((btCompoundShape) shapeBuilderResult.shape).addChildShape(new Matrix4().setToTranslation(meshPart.center), result);

            hull.dispose();
            shapeBuilderResult.convexHullShapes.add(result);
        }

        if (scale != null) {
            shapeBuilderResult.shape.setLocalScaling(scale);
        }

        return shapeBuilderResult;
    }

    private ShapeBuilderResult buildConeShape() {
        shapeBuilderResult.shape = new btConeShape(radius, dimensions.y);
        return shapeBuilderResult;
    }

    private ShapeBuilderResult buildCylinderShape() {
        shapeBuilderResult.shape = new btCylinderShape(dimensions);
        return shapeBuilderResult;
    }

    private ShapeBuilderResult buildSphereShape() {
        shapeBuilderResult.shape = new btSphereShape(radius);
        return shapeBuilderResult;
    }

    private ShapeBuilderResult buildCapsuleShape() {
        shapeBuilderResult.shape = new btCapsuleShape(radius, dimensions.y);
        return shapeBuilderResult;
    }

    private ShapeBuilderResult buildBoxShape() {
        if (boundingBox == null)
            shapeBuilderResult.shape = new btBoxShape(new Vector3(1,1,1));
        else {
            shapeBuilderResult.shape = new btBoxShape(dimensions);
        }
        return shapeBuilderResult;
    }
}
