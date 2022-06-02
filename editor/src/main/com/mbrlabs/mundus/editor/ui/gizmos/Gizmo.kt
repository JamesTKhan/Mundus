package com.mbrlabs.mundus.editor.ui.gizmos

import com.badlogic.gdx.graphics.g3d.decals.Decal

/**
 * Gizmos are 3D icons using libGDX Decals to provide information on game objects like a Light Bulb icon
 * for a GameObject that has a light component.
 *
 * @author James Pooley
 * @version June 01, 2022
 */
abstract class Gizmo {
    abstract var decal: Decal

    /**
     * Update method. Update decals position as needed.
     */
    abstract fun update()
}