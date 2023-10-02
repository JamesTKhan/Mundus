package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.env.lights.LightType;
import com.mbrlabs.mundus.commons.scene3d.DirtyListener;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.utils.LightUtils;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;

/**
 * LightComponent allows attachment of a Point or SpotLight to a GameObject.
 * LightComponent holds reference to the actual Light object. LightComponents do not
 * have associated assets, instead their data is saved within the GameObjects json inside
 * the scene file.
 *
 * @author JamesTKhan
 * @version May 30, 2022
 */
public class LightComponent extends AbstractComponent implements DirtyListener {
    private static final String TAG = LightComponent.class.getSimpleName();

    private BaseLight light;
    private LightType lightType;
    private Vector3 position = new Vector3();
    protected final Vector3 tmp = new Vector3();

    public LightComponent(GameObject go, LightType lightType) {
        super(go);
        go.addDirtyListener(this);

        type = Type.LIGHT;
        this.lightType = lightType;

        switch (lightType) {
            case DIRECTIONAL_LIGHT:
                throw new GdxRuntimeException("Directional Light not support for LightComponent");
            case POINT_LIGHT:
                light = new PointLightEx();
                ((PointLightEx) light).set(Color.WHITE, go.getPosition(tmp), 2500.0f);
                position = ((PointLightEx) light).position;
                break;
            case SPOT_LIGHT:
                light = new SpotLightEx();
                ((SpotLightEx) light).set(Color.WHITE, go.getPosition(tmp), Vector3.Z.cpy(), 2500.0f, 40f, 1f);
                position = ((SpotLightEx) light).position;
                break;
        }

        gameObject.sceneGraph.scene.environment.add(light);
    }

    @Override
    public void update(float delta) {
        // nothing to do here
    }

    @Override
    public void onDirty() {
        position.set(gameObject.getPosition(tmp));
        if (lightType == LightType.SPOT_LIGHT) {
            ((SpotLightEx) light).direction.set(gameObject.getForwardDirection(tmp));
        }
    }

    @Override
    public void remove() {
        super.remove();

        // remove the light from the environment
        gameObject.sceneGraph.scene.environment.remove(light);
    }

    @Override
    public Component clone(GameObject go) {
        if (!LightUtils.canCreateLight(go.sceneGraph.scene.environment, lightType)) {
            Gdx.app.log(TAG, "Could not clone Light Component, max lights reached.");
            return null;
        }

        LightComponent lightComponent = new LightComponent(go, lightType);
        LightUtils.copyLightSettings(getLight(), lightComponent.getLight());

        return lightComponent;
    }

    public BaseLight getLight() {
        return light;
    }

    public Vector3 getPosition() {
        return position;
    }

    public LightType getLightType() {
        return lightType;
    }

    public void toggleSpotLight(boolean value) {
        Color color = light.color.cpy();
        if (value && light instanceof PointLightEx) {
            int intensity = (int) ((PointLightEx) light).intensity;

            gameObject.sceneGraph.scene.environment.remove(light);
            light = new SpotLightEx();
            lightType = LightType.SPOT_LIGHT;

            ((SpotLightEx) light).intensity = intensity;
            ((SpotLightEx) light).position.set(position);
            ((SpotLightEx) light).direction.set(gameObject.getForwardDirection(tmp));
            ((SpotLightEx) light).cutoffAngle = 45;
            ((SpotLightEx) light).exponent = 30;
           // ((SpotLightEx) light).range = 100f;
            position = ((SpotLightEx) light).position;

            gameObject.sceneGraph.scene.environment.add(light);
        } else if (light instanceof SpotLightEx) {
            int intensity = (int) ((SpotLightEx) light).intensity;

            gameObject.sceneGraph.scene.environment.remove(light);
            light = new PointLightEx();
            lightType = LightType.POINT_LIGHT;

            //((PointLightEx) light).range = 100f;
            ((PointLightEx) light).position.set(position);
            ((PointLightEx) light).intensity = intensity;
            position = ((PointLightEx) light).position;

            gameObject.sceneGraph.scene.environment.add(light);
        }
        light.color.set(color);
    }
}
