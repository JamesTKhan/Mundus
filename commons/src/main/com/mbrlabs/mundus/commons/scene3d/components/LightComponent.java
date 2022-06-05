package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class LightComponent extends AbstractComponent {
    private PointLight light;
    private final Vector3 tmp = new Vector3();

    public LightComponent(GameObject go) {
        super(go);
        type = Type.LIGHT;

        light = new PointLight();
        light.position.set(go.getPosition(tmp));

        gameObject.sceneGraph.scene.environment.add(light);
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void update(float delta) {
        light.position.set(gameObject.getPosition(tmp));
    }

    @Override
    public void remove() {
        super.remove();

        // remove the light from the environment
        gameObject.sceneGraph.scene.environment.remove(light);
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }

    public PointLight getLight() {
        return light;
    }

    public void toggleSpotLight(boolean value) {
        if (value && light.lightType != LightType.SPOT_LIGHT) {
            gameObject.sceneGraph.scene.environment.remove(light);
            light = new SpotLight(Vector3.X.cpy(), SpotLight.DEFAULT_CUTOFF);
            gameObject.sceneGraph.scene.environment.add(light);
        } else if (light.lightType != LightType.POINT_LIGHT) {
            gameObject.sceneGraph.scene.environment.remove(light);
            light = new PointLight();
            gameObject.sceneGraph.scene.environment.add(light);
        }
    }
}
