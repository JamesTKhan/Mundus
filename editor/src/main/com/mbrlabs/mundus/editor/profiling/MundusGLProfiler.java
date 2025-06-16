package com.mbrlabs.mundus.editor.profiling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.FloatCounter;

/**
 * Extends libGDX GLProfiler to add support for using the MundusGLInterceptor instead.
 *
 * @author JamesTKhan
 * @version June 30, 2022
 */
public class MundusGLProfiler extends GLProfiler {

    private final Graphics graphics;
    private final MundusGLInterceptor glInterceptor;
    private boolean enabled = false;

    /**
     * Create a new instance of GLProfiler to monitor a {@link Graphics} instance's gl calls
     *
     * @param graphics instance to monitor with this instance, With Lwjgl 2.x you can pass in Gdx.graphics, with Lwjgl3 use
     *                 Lwjgl3Window.getGraphics()
     */
    public MundusGLProfiler(Graphics graphics) {
        super(graphics);
        this.graphics = graphics;
        GL30 gl30 = graphics.getGL30();
        if (gl30 != null) {
            glInterceptor = new MundusGL30Interceptor(this, graphics.getGL30());
        } else {
            glInterceptor = new MundusGL20Interceptor(this, graphics.getGL20());
        }
    }

    /** Enables profiling by replacing the {@code GL20} and {@code GL30} instances with profiling ones. */
    @Override
    public void enable () {
        if (enabled) return;

        GL30 gl30 = graphics.getGL30();
        if (gl30 != null) {
            graphics.setGL30((GL30)glInterceptor);
        } else {
            graphics.setGL20(glInterceptor);
        }

        Gdx.gl30 = graphics.getGL30();
        Gdx.gl20 = graphics.getGL20();
        Gdx.gl = graphics.getGL20();

        enabled = true;
    }

    /**
     * Pauses interceptor data collection without clearing statistics
     */
    public void pause() {
        glInterceptor.setPaused(true);
    }

    /**
     * Resumes statistics collection
     */
    public void resume() {
        glInterceptor.setPaused(false);
    }

    /** Disables profiling by resetting the {@code GL20} and {@code GL30} instances with the original ones. */
    @Override
    public void disable () {
        if (!enabled) return;

        GL30 gl30 = graphics.getGL30();
        if (gl30 != null) graphics.setGL30(((MundusGL30Interceptor) graphics.getGL30()).gl30);
        else graphics.setGL20(((MundusGL20Interceptor) graphics.getGL20()).gl20);

        enabled = false;
    }

    @Override
    public boolean isEnabled () {
        return enabled;
    }

    /**
     *
     * @return the total gl calls made since the last reset
     */
    @Override
    public int getCalls () {
        return glInterceptor.getCalls();
    }

    /**
     *
     * @return the total amount of texture bindings made since the last reset
     */
    @Override
    public int getTextureBindings () {
        return glInterceptor.getTextureBindings();
    }

    /**
     *
     * @return the total amount of draw calls made since the last reset
     */
    @Override
    public int getDrawCalls () {
        return glInterceptor.getDrawCalls();
    }

    /**
     *
     * @return the total amount of shader switches made since the last reset
     */
    @Override
    public int getShaderSwitches () {
        return glInterceptor.getShaderSwitches();
    }

    /**
     *
     * @return {@link FloatCounter} containing information about rendered vertices since the last reset
     */
    @Override
    public FloatCounter getVertexCount () {
        return glInterceptor.getVertexCount();
    }


    /** Will reset the statistical information which has been collected so far. This should be called after every frame.
     * Error listener is kept as it is. */
    @Override
    public void reset () {
        glInterceptor.reset();
    }
}
