package com.mbrlabs.mundus.pluginapi;

import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import org.pf4j.ExtensionPoint;

public interface SceneExtension extends ExtensionPoint {

    void sceneLoaded(Array<TerrainComponent> terrains);
}
