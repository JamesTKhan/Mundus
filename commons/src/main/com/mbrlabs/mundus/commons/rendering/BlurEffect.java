package com.mbrlabs.mundus.commons.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * @author JamesTKhan
 * @version October 07, 2023
 */
public class BlurEffect {
    protected static final String VERTEX_SHADER = "com/mbrlabs/mundus/commons/shaders/postprocess/spritebatch.vert.glsl";
    protected static final String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/shaders/postprocess/blur.frag.glsl";
    private final SpriteBatch spriteBatch;
    private FrameBuffer blurTargetA;
    private FrameBuffer blurTargetB;
    private ShaderProgram blurShader;

    private int pingPongCount = 1;
    private float maxBlur = 2f;

    public BlurEffect(int width, int height, SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;

        blurShader = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, null);

        float blurScale = 1f;
        blurTargetA = new NestableFrameBuffer(Pixmap.Format.RGBA8888, (int) (width * blurScale), (int) (height * blurScale), false);
        blurTargetB = new NestableFrameBuffer(Pixmap.Format.RGBA8888, (int) (width * blurScale), (int) (height * blurScale), false);
    }

    public BlurEffect(int width, int height, SpriteBatch spriteBatch, int internalFormat, int format, int type) {
        this.spriteBatch = spriteBatch;
        blurShader = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, null);
        blurTargetA = buildFrameBuffer(width, height, internalFormat, format, type);
        blurTargetB = buildFrameBuffer(width, height, internalFormat, format, type);
    }

    private FrameBuffer buildFrameBuffer(int width, int height, int internalFormat, int format, int type) {
        NestableFrameBuffer.NestableFrameBufferBuilder builder = new NestableFrameBuffer.NestableFrameBufferBuilder(width, height);
        builder.addColorTextureAttachment(internalFormat, format, type);
        return builder.build();
    }

    public void process(FrameBuffer src, FrameBuffer dest) {
        spriteBatch.setShader(blurShader);

        float srcWidth = src.getWidth();
        float srcHeight = src.getHeight();
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, src.getWidth(), src.getHeight());

        for (int i = 0; i < pingPongCount; i++) {
            // Horizontal blur pass
            blurTargetA.begin();
            spriteBatch.begin();
            blurShader.setUniformf("dir", .5f, 0);
            blurShader.setUniformf("radius", maxBlur);
            blurShader.setUniformf("resolution", srcWidth);
            spriteBatch.draw(i == 0 ? src.getColorBufferTexture() : blurTargetB.getColorBufferTexture(), 0, 0, srcWidth, srcHeight, 0, 0, 1, 1);
            spriteBatch.end();
            blurTargetA.end();

            // Verticle blur pass
            if (i == pingPongCount - 1) {
                dest.begin();
            } else {
                blurTargetB.begin();
            }
            spriteBatch.begin();
            blurShader.setUniformf("dir", 0, .5f);
            blurShader.setUniformf("radius", maxBlur);
            blurShader.setUniformf("resolution", srcHeight);
            spriteBatch.draw(blurTargetA.getColorBufferTexture(), 0, 0, srcWidth, srcHeight, 0, 0, 1, 1);
            spriteBatch.end();
            if (i == pingPongCount - 1) {
                dest.end();
            } else {
                blurTargetB.end();
            }
        }

        spriteBatch.setShader(null);
    }

    public void setRadius(float radius) {
        this.maxBlur = radius;
    }

    public float getRadius() {
        return maxBlur;
    }

    public void setPingPongCount(int pingPongCount) {
        this.pingPongCount = pingPongCount;
    }

    public int getPingPongCount() {
        return pingPongCount;
    }
}
