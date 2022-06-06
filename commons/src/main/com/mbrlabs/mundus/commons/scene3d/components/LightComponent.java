package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class LightComponent extends AbstractComponent {
    private PointLight light;
    protected final Vector3 tmp = new Vector3();

    public LightComponent(GameObject go, LightType lightType) {
        super(go);
        type = Type.LIGHT;

        switch (lightType) {
            case DIRECTIONAL_LIGHT:
                throw new GdxRuntimeException("Directional Light not support for LightComponent");
            case POINT_LIGHT:
                light = new PointLight();
                break;
            case SPOT_LIGHT:
                light = new SpotLight();
                break;
        }

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
            light = new SpotLight();
            gameObject.sceneGraph.scene.environment.add(light);
        } else if (light.lightType != LightType.POINT_LIGHT) {
            gameObject.sceneGraph.scene.environment.remove(light);
            light = new PointLight();
            gameObject.sceneGraph.scene.environment.add(light);
        }
    }
}
