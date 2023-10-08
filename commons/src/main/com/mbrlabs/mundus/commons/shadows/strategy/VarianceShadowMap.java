package com.mbrlabs.mundus.commons.shadows.strategy;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.rendering.BlurEffect;
import com.mbrlabs.mundus.commons.shaders.VarianceDepthShader;
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight;
import com.mbrlabs.mundus.commons.shadows.ShadowStrategyAttribute;

/**
 * @author JamesTKhan
 * @version October 06, 2023
 */
public class VarianceShadowMap extends BaseShadowMap {

    private final Shader varianceDepthShader;
    private BlurEffect blurEffect;
    private final SpriteBatch spriteBatch;
    private boolean intialized = false;

    public VarianceShadowMap() {
        attribute = new ShadowStrategyAttribute(ShadowStrategyAttribute.VarianceShadowMap);
        varianceDepthShader = new VarianceDepthShader();
        varianceDepthShader.init();
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void renderShadowMap(Scene scene) {
        if (!intialized) {
            intialized = true;
            setTextureSettings(scene.dirLight);

            // Moments are stored in Red/Green channels of the FBO
            int internalFormat = GL30.GL_RG32F;
            int format = GL30.GL_RG;
            int type = GL30.GL_FLOAT;

            scene.dirLight.setFrameBufferFormat(internalFormat, format, type);

            if (blurEffect == null) {
                int w = scene.dirLight.getFrameBuffer().getColorBufferTexture().getWidth();
                int h = scene.dirLight.getFrameBuffer().getColorBufferTexture().getHeight();
                blurEffect = new BlurEffect(w, h, spriteBatch, internalFormat, format, type);
            }
        }

        renderShadowMap(scene, varianceDepthShader);
        blurEffect.process(scene.dirLight.getFrameBuffer(), scene.dirLight.getFrameBuffer());
    }

    private void setTextureSettings(MundusDirectionalShadowLight light) {
        TextureDescriptor textureDesc = light.getDepthMap();
        // VSM requires linear filtering
        textureDesc.minFilter = textureDesc.magFilter = Texture.TextureFilter.Linear;
        textureDesc.uWrap = textureDesc.vWrap = Texture.TextureWrap.ClampToEdge;
    }

    @Override
    public void dispose() {
        varianceDepthShader.dispose();
        spriteBatch.dispose();
    }
}
