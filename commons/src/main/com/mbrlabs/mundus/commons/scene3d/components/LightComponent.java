package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class LightComponent extends AbstractComponent {
    PointLight point = new PointLight();
    Vector3 tmp = new Vector3();

    public LightComponent(GameObject go) {
        super(go);

        point.intensity = 1.0f;
        point.setColor(Color.RED);
        type = Type.LIGHT;
        point.position.set( go.getPosition(new Vector3()));
        gameObject.sceneGraph.scene.environment.add(point);
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void update(float delta) {
        point.position.set(gameObject.getPosition(tmp));
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }
}
