package com.mbrlabs.mundus.commons.shadows;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;

/**
 * Extension of gdx-gltf DirectionalShadowLight with additional logic
 * for convenience in editor.
 *
 * @author JamesTKhan
 * @version June 01, 2023
 */
public class MundusDirectionalShadowLight extends DirectionalShadowLight {
    public static final int DEFAULT_VIEWPORT_SIZE = 250;
    public static final float DEFAULT_CAM_NEAR = 0.2f;
    public static final float DEFAULT_CAM_FAR = 100f;

    private ShadowResolution shadowResolution = ShadowResolution.DEFAULT_SHADOW_RESOLUTION;
    private boolean castsShadows = false;

    public MundusDirectionalShadowLight() {
        super();
    }
    public MundusDirectionalShadowLight(ShadowResolution resolution, int viewportWidth, int viewportHeight, float near, float far) {
        set(resolution, viewportWidth, viewportHeight, near, far);
    }

    public void set(ShadowResolution resolution, int viewportWidth, int viewportHeight, float nearPlane, float farPlane) {
        this.shadowResolution = resolution;

        if (cam == null) {
            cam = new OrthographicCamera(viewportWidth, viewportHeight);
        } else {
            cam.viewportWidth = viewportWidth;
            cam.viewportHeight = viewportHeight;
        }

        cam.near = nearPlane;
        cam.far = farPlane;
        cam.update();

        initFBO();
    }

    public void setShadowResolution(ShadowResolution shadowResolution) {
        this.shadowResolution = shadowResolution;
        initFBO();
    }

    public ShadowResolution getShadowResolution() {
        return shadowResolution;
    }

    private void initFBO() {
        if (fbo != null) {
            fbo.dispose();
        }

        Vector2 res = shadowResolution.getResolutionValues();
        fbo = new NestableFrameBuffer(Pixmap.Format.RGBA8888, (int) res.x, (int) res.y, true);
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }
}
