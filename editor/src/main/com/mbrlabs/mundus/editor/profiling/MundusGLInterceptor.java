package com.mbrlabs.mundus.editor.profiling;

import com.badlogic.gdx.graphics.profiling.GLInterceptor;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

/**
 * Extends libGDX GLInterceptor to include the ability to pause profiling without clearing the data. This is to
 * allow accurate profiling of only the actual scene profiling without any of the editors UI rendering
 * stats polluting the profiling results.
 *
 * @author JamesTKhan
 * @version June 30, 2022
 */
public abstract class MundusGLInterceptor extends GLInterceptor {
    protected boolean isPaused = false;

    protected MundusGLInterceptor(GLProfiler profiler) {
        super(profiler);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    protected void incrementCalls() {
        if (isPaused) return;
        calls++;
    }

    protected void incrementDrawCalls() {
        if (isPaused) return;
        drawCalls++;
    }

    protected void incrementTextureBindings() {
        if (isPaused) return;
        textureBindings++;
    }

    protected void incrementShaderSwitches() {
        if (isPaused) return;
        shaderSwitches++;
    }

    protected void putVertexCount(int count) {
        if (isPaused) return;
        vertexCount.put(count);
    }
}
