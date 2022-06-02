package com.mbrlabs.mundus.editor.ui.gizmos

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.mbrlabs.mundus.commons.scene3d.components.LightComponent

/**
 * @author James Pooley
 * @version June 01, 2022
 */
class LightGizmo(private var lightComponent: LightComponent) : Gizmo() {
    override lateinit var decal: Decal

    init {
        val region = TextureRegion(Texture(Gdx.files.internal("icon/gizmos/lightbulb-icon.png")))
        decal = Decal.newDecal(12f, 16f, region, true)
    }

    override fun update() {
        decal.position = lightComponent.pointLight.position
    }

}