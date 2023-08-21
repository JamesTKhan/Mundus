package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.CullableComponent;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent;

/**
 * Simple debug renderer that only renders bounding boxes of {@link CullableComponent}
 *  which are used for frustum culling.
 *
 * @author JamesTKhan
 * @version July 25, 2022
 */
public class DebugRenderer implements Renderer, Disposable {
    private static final Matrix4 tmpTransform = new Matrix4();
    private static final Vector3 tmpDims = new Vector3();
    private static final Vector3 tmpCenter = new Vector3();
    private Vector3[] vertices = new Vector3[8];

    private final ShapeRenderer shapeRenderer;
    private final boolean ownsShapeRenderer;

    private ShapeRenderer.ShapeType shapeType = ShapeRenderer.ShapeType.Line;

    public DebugRenderer() {
        shapeRenderer = new ShapeRenderer();
        ownsShapeRenderer = true;
    }

    public DebugRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        ownsShapeRenderer = false;
    }

    public void setShapeType(ShapeRenderer.ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    @Override
    public void begin(Camera camera) {
        shapeRenderer.begin(shapeType);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(Array<GameObject> gameObjects) {
        for (GameObject object : gameObjects) {
            render(object);
        }
    }

    @Override
    public void render(GameObject go) {
        if (!go.active) return;

        for (Component component : go.getComponents()) {

            // Only CullableComponents have bounds info available right now.
            if (!(component instanceof CullableComponent)) {
                continue;
            }
            CullableComponent cullableComponent = (CullableComponent) component;

            tmpTransform.set(go.getTransform());
            ((CullableComponent) component).getDimensions(tmpDims);

            vertices = calculateVertices(tmpTransform, tmpDims);
            shapeRenderer.setColor(getColor(component));
            for(int i = 0; i < 8; i++) {
            }
            shapeRenderer.line(vertices[0], vertices[1]);
            shapeRenderer.line(vertices[1], vertices[2]);
            shapeRenderer.line(vertices[2], vertices[3]);
            shapeRenderer.line(vertices[3], vertices[0]);
            shapeRenderer.line(vertices[4], vertices[5]);
            shapeRenderer.line(vertices[5], vertices[6]);
            shapeRenderer.line(vertices[6], vertices[7]);
            shapeRenderer.line(vertices[7], vertices[4]);
            shapeRenderer.line(vertices[0], vertices[4]);
            shapeRenderer.line(vertices[1], vertices[5]);
            shapeRenderer.line(vertices[3], vertices[7]);
            shapeRenderer.line(vertices[2], vertices[6]);

            shapeRenderer.point(tmpCenter.x, tmpCenter.y, tmpCenter.z);
        }

        if (go.getChildren() == null) return;

        for (GameObject child : go.getChildren()) {
            render(child);
        }

    }

    @Override
    public void end() {
        shapeRenderer.end();
    }

    protected Color getColor(Component component) {
        if (component instanceof ModelComponent) {
            return Color.RED;
        } else if (component instanceof TerrainComponent) {
            return Color.GREEN;
        } else if (component instanceof WaterComponent) {
            return Color.NAVY;
        }
        return Color.WHITE;
    }

    @Override
    public void dispose() {
        if (ownsShapeRenderer) {
            shapeRenderer.dispose();
        }
    }


    private Vector3[] calculateVertices(Matrix4 transform, Vector3 dimensions) {
        Vector3[] vertices = new Vector3[8];

        Vector3 halfDimensions = dimensions.cpy().scl(0.5f);

        Vector3 right = new Vector3(1, 0, 0);
        Vector3 up = new Vector3(0, 1, 0);
        Vector3 forward = new Vector3(0, 0, 1);
        transform.getTranslation(tmpCenter);
        right.set(right.mul(transform));
        up.set(up.mul(transform));
        forward.set(forward.mul(transform));

        // Calculate near face vertices
        vertices[0] = tmpCenter.cpy().sub(right.cpy().scl(halfDimensions.x)).sub(up.cpy().scl(halfDimensions.y)).sub(forward.cpy().scl(halfDimensions.z));
        vertices[1] = tmpCenter.cpy().add(right.cpy().scl(halfDimensions.x)).sub(up.cpy().scl(halfDimensions.y)).sub(forward.cpy().scl(halfDimensions.z));
        vertices[2] = tmpCenter.cpy().add(right.cpy().scl(halfDimensions.x)).add(up.cpy().scl(halfDimensions.y)).sub(forward.cpy().scl(halfDimensions.z));
        vertices[3] = tmpCenter.cpy().sub(right.cpy().scl(halfDimensions.x)).add(up.cpy().scl(halfDimensions.y)).sub(forward.cpy().scl(halfDimensions.z));

        // Calculate far face vertices
        vertices[4] = tmpCenter.cpy().sub(right.cpy().scl(halfDimensions.x)).sub(up.cpy().scl(halfDimensions.y)).add(forward.cpy().scl(halfDimensions.z));
        vertices[5] = tmpCenter.cpy().add(right.cpy().scl(halfDimensions.x)).sub(up.cpy().scl(halfDimensions.y)).add(forward.cpy().scl(halfDimensions.z));
        vertices[6] = tmpCenter.cpy().add(right.cpy().scl(halfDimensions.x)).add(up.cpy().scl(halfDimensions.y)).add(forward.cpy().scl(halfDimensions.z));
        vertices[7] = tmpCenter.cpy().sub(right.cpy().scl(halfDimensions.x)).add(up.cpy().scl(halfDimensions.y)).add(forward.cpy().scl(halfDimensions.z));

        return vertices;
    }

}
