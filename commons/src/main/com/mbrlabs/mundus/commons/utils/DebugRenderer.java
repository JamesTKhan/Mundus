package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.CullableComponent;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple debug renderer that only renders bounding boxes of {@link CullableComponent}
 * which are used for frustum culling.
 *
 * @author JamesTKhan
 * @version July 25, 2022
 */
public class DebugRenderer implements Renderer, Disposable {
    private static final OrientedBoundingBox tmpObb = new OrientedBoundingBox();
    //Game Object needed for rendering forward facing arrow
    private static GameObject selectedGameObject;
    private static CullableComponent cullableComponent;

    // Shape Renderer
    private final boolean ownsShapeRenderer;
    private final ShapeRenderer shapeRenderer;
    private ShapeRenderer.ShapeType shapeType = ShapeRenderer.ShapeType.Line;

    // Model Renderer
    private ModelBatch modelBatch;
    private final Map<Component, ModelInstance> modelInstancesCache = new HashMap<>();
    private final Array<ModelInstance> instances = new Array<>();

    // Debug settings
    private boolean appearOnTop = true;
    private boolean enabled = false;
    private boolean drawFacingArrow = true;

    public DebugRenderer() {
        shapeRenderer = new ShapeRenderer();
        ownsShapeRenderer = true;
    }

    public DebugRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        modelBatch = new ModelBatch();
        ownsShapeRenderer = false;
    }

    public static void setSelectedGameObject(GameObject go){
        selectedGameObject = go;
    }

    public void setShapeType(ShapeRenderer.ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    @Override
    public void begin(Camera camera) {
        if (!enabled) return;

        if (appearOnTop) {
            // Clearing depth puts the model debug lines on top of everything else
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        }
        modelBatch.begin(camera);
    }

    @Override
    public void render(Array<GameObject> gameObjects) {
        instances.clear();
        for (GameObject object : gameObjects) {
            render(object);
        }
        modelBatch.render(instances);

        // If it's in the cache but not in instances, remove from cache
        Iterator<Map.Entry<Component, ModelInstance>> it = modelInstancesCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Component, ModelInstance> item = it.next();
            if (!instances.contains(item.getValue(), true)) {
                // Dispose the debug model
                item.getValue().model.dispose();
                it.remove();
            }
        }


        // Any shape rendering should be done here if needed
    }

    @Override
    public void render(GameObject go) {
        if (!enabled) return;
        if (!go.active) return;

        for (Component component : go.getComponents()) {

            // Only CullableComponents have bounds info available right now.
            if (!(component instanceof CullableComponent)) {
                continue;
            }

            cullableComponent = (CullableComponent) component;

            if (!modelInstancesCache.containsKey(component)) {
                OrientedBoundingBox orientedBoundingBox = cullableComponent.getOrientedBoundingBox();
                if (orientedBoundingBox == null) continue;
                tmpObb.set(orientedBoundingBox.getBounds(), new Matrix4());
                Model model = buildModel(tmpObb, getColor(component));
                modelInstancesCache.put(component, new ModelInstance(model));
            }

            ModelInstance modelInstance = modelInstancesCache.get(component);
            modelInstance.transform.set(cullableComponent.getOrientedBoundingBox().getTransform());
            instances.add(modelInstance);
        }

        if (drawFacingArrow && go == selectedGameObject){
            //TODO Cache arrow model
            Vector3 origin = new Vector3();
            Vector3 facing = new Vector3();

            float scale = cullableComponent.getOrientedBoundingBox().getBounds().max.len2();

            Vector3 offset = facing.cpy().scl(scale);

            go.getForwardDirection(facing);
            go.getPosition(origin);



            facing.scl(scale);

            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            MeshPartBuilder builder =  modelBuilder.part("forward", GL20.GL_TRIANGLES,
                    (VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked), new Material());
            builder.setColor(Color.RED);
            ArrowShapeBuilder.build(builder, origin.x, origin.y,origin.z, facing.x,facing.y, facing.z, .02f, .2f, 20);
            Model arrowModel = modelBuilder.end();
            ModelInstance arrow = new ModelInstance(arrowModel);
            instances.add(arrow);
        }

        if (go.getChildren() == null) return;

        for (GameObject child : go.getChildren()) {
            render(child);
        }

    }

    @Override
    public void end() {
        if (!enabled) return;
        modelBatch.end();
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

        if (modelBatch != null) {
            modelBatch.dispose();
        }

        // Dispose all cached models
        Iterator<Map.Entry<Component, ModelInstance>> it = modelInstancesCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Component, ModelInstance> item = it.next();
            // Dispose the debug model
            item.getValue().model.dispose();
            it.remove();
        }
    }

    /**
     * Builds a model for the given oriented bounding box.
     *
     * @param orientedBoundingBox the oriented bounding box
     * @param color               the color of the model
     * @return the model
     */
    private Model buildModel(OrientedBoundingBox orientedBoundingBox, Color color) {
        Material material = new Material(ColorAttribute.createDiffuse(color));
        com.badlogic.gdx.graphics.g3d.utils.ModelBuilder mb = new com.badlogic.gdx.graphics.g3d.utils.ModelBuilder();
        mb.begin();
        MeshPartBuilder meshPartBuilder = mb.part("debug_box", GL20.GL_LINES, VertexAttributes.Usage.Position, material);
        BoxShapeBuilder.build(meshPartBuilder, orientedBoundingBox.getCorner000(new Vector3()),
                orientedBoundingBox.getCorner010(new Vector3()), orientedBoundingBox.getCorner100(new Vector3()),
                orientedBoundingBox.getCorner110(new Vector3()), orientedBoundingBox.getCorner001(new Vector3()),
                orientedBoundingBox.getCorner011(new Vector3()), orientedBoundingBox.getCorner101(new Vector3()),
                orientedBoundingBox.getCorner111(new Vector3()));
        return mb.end();
    }

    public void setAppearOnTop(boolean appearOnTop) {
        this.appearOnTop = appearOnTop;
    }

    public boolean isAppearOnTop() {
        return appearOnTop;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
