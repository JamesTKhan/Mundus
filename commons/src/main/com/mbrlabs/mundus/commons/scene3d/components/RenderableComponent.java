package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;

/**
 * A component that can be rendered, likely by a ModelBatch. Provides a RenderableProvider.
 * @author JamesTKhan
 * @version June 08, 2023
 */
public interface RenderableComponent extends Component {
    RenderableProvider getRenderableProvider();
}
