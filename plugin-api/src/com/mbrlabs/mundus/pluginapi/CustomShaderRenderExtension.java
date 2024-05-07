package com.mbrlabs.mundus.pluginapi;

import com.badlogic.gdx.graphics.Camera;
import org.pf4j.ExtensionPoint;

public interface CustomShaderRenderExtension extends ExtensionPoint {

    void render(Camera camera);
}
