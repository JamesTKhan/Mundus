package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
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
    private static final Vector3 tmpPos = new Vector3();
    private static final Vector3 tmpCenter = new Vector3();

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
            go.getPosition(tmpPos);
            tmpCenter.set(cullableComponent.getCenter());
            tmpPos.add(tmpCenter);

            shapeRenderer.setColor(getColor(component));
            shapeRenderer.box(
                    tmpPos.x - (cullableComponent.getDimensions().x / 2),
                    tmpPos.y - (cullableComponent.getDimensions().y / 2),
                    tmpPos.z + (cullableComponent.getDimensions().z / 2),
                    cullableComponent.getDimensions().x,
                    cullableComponent.getDimensions().y,
                    cullableComponent.getDimensions().z);
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
}
