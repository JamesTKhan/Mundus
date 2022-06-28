package com.mbrlabs.mundus.commons.shadows;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.utils.LightUtils;

/**
 * @author James Pooley
 * @version June 14, 2022
 */
public class CascadeShadowMapper {
    public static final int CASCADE_MAP_COUNT = 3;
    public Array<ShadowMapper> shadowMappers = new Array<>();
    protected Vector3 clippingPlaneDisable = new Vector3(0.0f, 0f, 0.0f);
    public float[] shadowCascadeEnd = new float[3];
    public float[] viewportSizes = new float[3];

    private int viewportSize = 1000;

    public CascadeShadowMapper(PerspectiveCamera cam, Vector3 lightDirection) {
        shadowCascadeEnd[0] = cam.far * 0.05f;
        shadowCascadeEnd[1] = cam.far * 0.2f;
        shadowCascadeEnd[2] = cam.far ;

        viewportSizes[0] = viewportSize * 0.1f;
        viewportSizes[1] = viewportSize * 0.8f;
        viewportSizes[2] = viewportSize;

        for (int i = 0; i < CASCADE_MAP_COUNT; i++) {
            shadowMappers.add(new ShadowMapper(ShadowResolution._4096, (int) viewportSizes[i], (int) viewportSizes[i], cam.near, shadowCascadeEnd[i], lightDirection));
        }
    }

    Vector3 tmp = new Vector3();
    public void render(PerspectiveCamera cam, float delta, ModelBatch batch, SceneGraph sceneGraph, Shader shader) {
        Vector3 direction = LightUtils.getDirectionalLight(sceneGraph.scene.environment).direction;
        for (int i = 0; i < CASCADE_MAP_COUNT; i++) {
            ShadowMapper shadowMapper = shadowMappers.get(i);

            tmp.set(cam.position);

            if (i != 0)
                tmp.y = 100;

            // Shift the ortho cams "forward" a bit (cascade)
            if (i >0) {
                tmp.mulAdd(cam.direction, i * 60f);
            } else
                tmp.mulAdd(cam.direction, 10f);

            shadowMapper.setCenter(tmp);
            shadowMapper.begin(cam, direction);
            batch.begin(shadowMapper.cam);
            sceneGraph.renderDepth(delta, clippingPlaneDisable, 0, shader);
            batch.end();
            shadowMapper.end();
        }
    }
}
