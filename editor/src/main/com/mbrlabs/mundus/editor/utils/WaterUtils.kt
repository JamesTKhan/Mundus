package com.mbrlabs.mundus.editor.utils

import com.mbrlabs.mundus.commons.assets.WaterAsset
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.commons.shaders.WaterShader
import com.mbrlabs.mundus.editor.scene3d.components.PickableWaterComponent
import com.mbrlabs.mundus.editor.shader.Shaders

fun createWaterGO(sg: SceneGraph, shader: WaterShader, goID: Int, goName: String,
                  water: WaterAsset): GameObject {
    val waterGO = GameObject(sg, null, goID)
    waterGO.name = goName
    waterGO.hasWaterComponent = true

    water.water.setTransform(waterGO.transform)
    val waterComponent = PickableWaterComponent(waterGO, Shaders.waterShader)
    waterComponent.waterAsset = water
    waterGO.components.add(waterComponent)
    waterComponent.shader = shader
    waterComponent.encodeRaypickColorId()

    return waterGO
}
