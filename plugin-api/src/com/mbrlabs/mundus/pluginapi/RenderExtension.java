package com.mbrlabs.mundus.pluginapi;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import org.pf4j.ExtensionPoint;

public interface RenderExtension extends ExtensionPoint {

    RenderableProvider getRenderableProvider();
}
