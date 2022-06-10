package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.env.lights.PointLight;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.utils.LightUtils;

/**
 * LightComponent allows attachment of a Point or SpotLight to a GameObject.
 * LightComponent holds reference to the actual Light object. LightComponents do not
 * have associated assets, instead their data is saved within the GameObjects json inside
 * the scene file.
 *
 * @author James Pooley
 * @version May 30, 2022
 */
public class LightComponent extends AbstractComponent {
    private static final String TAG = LightComponent.class.getSimpleName();

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
        if (!LightUtils.canCreateLight(go.sceneGraph.scene.environment, this.getLight().lightType)) {
            Gdx.app.log(TAG, "Could not clone Light Component, max lights reached.");
            return null;
        }

        LightComponent lightComponent = new LightComponent(go, this.getLight().lightType);
        LightUtils.copyLightSettings(getLight(), lightComponent.getLight());

        return lightComponent;
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
