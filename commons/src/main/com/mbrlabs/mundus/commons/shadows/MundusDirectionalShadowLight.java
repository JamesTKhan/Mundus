package com.mbrlabs.mundus.commons.shadows;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
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

    // FBO data
    protected int colorInternalFormat;
    protected int colorFormat;
    protected int colorType;

    public MundusDirectionalShadowLight() {
        super();
        determineFBOFormat();
    }

    public MundusDirectionalShadowLight(ShadowResolution resolution, int viewportWidth, int viewportHeight, float near, float far) {
        determineFBOFormat();
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

    public void setFrameBufferFormat(int internalFormat, int format, int type) {
        initFBO(internalFormat, format, type);
    }

    private void initFBO() {
        initFBO(colorInternalFormat, colorFormat, colorType);
    }

    private void initFBO(int internalFormat, int format, int type) {
        if (fbo != null) {
            fbo.dispose();
        }

        colorInternalFormat = internalFormat;
        colorFormat = format;
        colorType = type;

        Vector2 res = shadowResolution.getResolutionValues();

        GLFrameBuffer.FrameBufferBuilder frameBufferBuilder;
        if (Scene.isRuntime) {
            frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder((int) res.x, (int) res.y);
        } else {
            frameBufferBuilder = new NestableFrameBuffer.NestableFrameBufferBuilder((int) res.x, (int) res.y);
        }

        frameBufferBuilder.addColorTextureAttachment(internalFormat, format, type);
        frameBufferBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT16);
        fbo = frameBufferBuilder.build();
    }

    protected void determineFBOFormat() {
        // Defaults if no extension is supported and not GL3, precision will be low
//        colorInternalFormat = GL20.GL_RGBA;
//        colorFormat = GL20.GL_RGBA;
//        colorType = GL20.GL_UNSIGNED_BYTE;

        // For WebGL1 and OpenGL ES 2.0 only, because.. they decided to be different and make things confusing
//        final int GL_HALF_FLOAT_OES = 0x8D61;

//        if (!GLUtils.isGL3()) {
//            String extensions = GLUtils.getExtensions();
//            if (extensions.contains("GL_ARB_texture_float") || extensions.contains("GL_OES_texture_float")) {
//                colorInternalFormat = GL30.GL_R32F;
//                colorFormat = GL30.GL_RED;
//                colorType = GL30.GL_FLOAT;
//            } else if (extensions.contains("GL_ARB_half_float_pixel")) {
//                colorInternalFormat = GL30.GL_R16F;
//                colorFormat = GL30.GL_RED;
//                colorType = GL30.GL_HALF_FLOAT;
//            } else if (extensions.contains("GL_OES_texture_half_float")) {
//                colorInternalFormat = GL30.GL_R16F;
//                colorFormat = GL30.GL_RED;
//                colorType = GL_HALF_FLOAT_OES;
//            } else {
//                Gdx.app.error("MundusDirectionalShadowLight", "No float texture support, falling back to low precision");
//            }
//        } else {
            // GL3 supports float textures by default
            colorInternalFormat = GL30.GL_R32F;
            colorFormat = GL30.GL_RED;
            colorType = GL30.GL_FLOAT;
//        }
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }
}
