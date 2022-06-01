package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class LightComponent extends AbstractComponent {
    private final PointLight pointLight = new PointLight();
    private final Vector3 tmp = new Vector3();

    public LightComponent(GameObject go) {
        super(go);
        type = Type.LIGHT;

        pointLight.intensity = 1.0f;
        pointLight.setColor(Color.RED);
        pointLight.position.set(go.getPosition(new Vector3()));

        gameObject.sceneGraph.scene.environment.add(pointLight);
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void update(float delta) {
        pointLight.position.set(gameObject.getPosition(tmp));
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }

    public PointLight getPointLight() {
        return pointLight;
    }
}
