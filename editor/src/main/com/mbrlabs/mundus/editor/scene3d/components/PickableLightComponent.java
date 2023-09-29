package com.mbrlabs.mundus.editor.scene3d.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.picker.PickerColorEncoder;
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;

/**
 * Pickable Light Component which allows the light components to be picked(clicked) on in the 3D scene. This required a
 * work around as all the picking uses renderables and materials but we render lights with 2D gizmo images/icons.
 *
 * The workaround is to create a 3D cube renderable about same size as the 2D decal, and we only render this cube
 * to the picker shader and encode its material with an id. After that, picking works as expected.
 *
 * @author JamesTKhan
 * @version June 01, 2022
 */
public class PickableLightComponent extends LightComponent implements PickableComponent {
    private ModelInstance modelInstance;
    public PickableLightComponent(GameObject go, LightType lightType) {
        super(go, lightType);

        Material material = new Material();
        material.set(new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE));

        // Build simple cube as a workaround for making lights pickable
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("ID"+go.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        BoxShapeBuilder.build(builder, 0,0,0, 12f, 16f, 10f);
        Model model = modelBuilder.end();
        modelInstance = new ModelInstance(model);

        encodeRaypickColorId();

    }

    @Override
    public void update(float delta) {
        // Keeping this here for debugging if we need to render this cube
        //gameObject.getPosition(tmp);
        //modelInstance.transform.setToTranslation(tmp);
        //gameObject.sceneGraph.scene.batch.render(modelInstance, gameObject.sceneGraph.scene.environment));
    }

    @Override
    public void encodeRaypickColorId() {
        if (modelInstance == null) return;
        PickerIDAttribute goIDa = PickerColorEncoder.encodeRaypickColorId(gameObject);
        modelInstance.materials.first().set(goIDa);
    }

    @Override
    public void renderPick() {
        gameObject.getPosition(tmp);
        modelInstance.transform.setToTranslation(tmp);
        gameObject.sceneGraph.scene.batch.render(modelInstance, Shaders.INSTANCE.getPickerShader());
    }
}
