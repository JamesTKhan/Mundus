package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.graphics.g3d.Shader
import com.mbrlabs.mundus.commons.assets.WaterAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.editor.scene3d.components.PickableWaterComponent

fun createWaterGO(sg: SceneGraph, shader: Shader?, goID: Int, goName: String,
                  water: WaterAsset): GameObject {
    val waterGO = GameObject(sg, null, goID)
    waterGO.name = goName
    waterGO.hasWaterComponent = true

    water.water.setTransform(waterGO.transform)
    val waterComponent = PickableWaterComponent(waterGO, shader)
    waterComponent.waterAsset = water
    waterGO.components.add(waterComponent)
    waterComponent.encodeRaypickColorId()

    return waterGO
}
