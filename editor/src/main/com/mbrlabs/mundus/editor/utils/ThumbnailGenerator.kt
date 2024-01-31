/*
 * Copyright (c) 2023. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.tan
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.ScreenUtils
import com.mbrlabs.mundus.commons.shadows.MundusDirectionalShadowLight
import com.mbrlabs.mundus.commons.utils.LightUtils
import com.mbrlabs.mundus.commons.utils.ModelUtils
import com.mbrlabs.mundus.commons.utils.MundusShaderParser
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider
import net.mgsx.gltf.scene3d.utils.IBLBuilder

object ThumbnailGenerator {

    private const val THUMBNAIL_WIDTH = 100f
    private const val THUMBNAIL_HEIGHT = 100f
    private val BACKGROUND_COLOR = Colors.GRAY_888

    fun generateThumbnail(model: Model): Texture {
        val config = PBRShaderConfig()
        config.numDirectionalLights = 1
        config.numBones = ModelUtils.getBoneCount(model)
        config.vertexShader = MundusShaderParser.parse(Gdx.files.classpath("com/mbrlabs/mundus/commons/shaders/pbr/pbr.vs.glsl"))
        config.fragmentShader = MundusShaderParser.parse(Gdx.files.classpath("com/mbrlabs/mundus/commons/shaders/pbr/pbr.fs.glsl"))

        val cam = PerspectiveCamera(67f, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
        val modelBatch = ModelBatch(PBRShaderProvider(config))

        val bounds = BoundingBox()
        val modelInstance = ModelInstance(model)
        modelInstance.calculateBoundingBox(bounds)

        val center = Vector3()
        val dimensions = Vector3()
        bounds.getCenter(center)
        bounds.getDimensions(dimensions)

        val maxDimension = Math.max(dimensions.x, Math.max(dimensions.y, dimensions.z))
        val distance =
                (maxDimension / (2 * tan((cam.fieldOfView / 2 * MathUtils.degreesToRadians))))

        var camFar = center.dst(bounds.getCorner000(Vector3())) + distance
        camFar += 100f // Add additional buffer to distance

        // Position up a bit, looking down at model
        val verticalDistance = distance * 0.25f

        cam.position.set(center.x + distance, center.y + verticalDistance, center.z + distance)
        cam.lookAt(center)
        cam.near = 0.1f
        cam.far = camFar
        cam.update()

        val directionalLightEx = MundusDirectionalShadowLight()
        directionalLightEx.intensity = LightUtils.DEFAULT_INTENSITY
        directionalLightEx.setColor(LightUtils.DEFAULT_COLOR)
        directionalLightEx.direction.set(-1f, -0.8f, -0.2f)

        val iblBuilder = IBLBuilder.createOutdoor(directionalLightEx)
        val diffuseCubemap = iblBuilder.buildIrradianceMap(256)
        val specularCubemap = iblBuilder.buildRadianceMap(10)
        iblBuilder.dispose()

        val env = Environment()

        val brdfLUT = Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"))
        env.set(ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f))
        env.set(PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT))
        env.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap))
        env.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap))


        val frameBufferBuilder = GLFrameBuffer.FrameBufferBuilder(THUMBNAIL_WIDTH.toInt(), THUMBNAIL_HEIGHT.toInt())
        frameBufferBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888)

        // Enhanced precision, only needed for 3D scenes
        frameBufferBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24)
        val fbo = frameBufferBuilder.build()

        fbo.begin()
        ScreenUtils.clear(BACKGROUND_COLOR, true)
        modelBatch.begin(cam)
        modelBatch.render(modelInstance, env)
        modelBatch.end()

        val p = Pixmap.createFromFrameBuffer(0, 0, THUMBNAIL_WIDTH.toInt(), THUMBNAIL_HEIGHT.toInt())
        // Flip the pixmap
        val flipped = Pixmap(p.width, p.height, p.format)
        for (x in 0 until p.width) {
            for (y in 0 until p.height) {
                flipped.drawPixel(x, p.height - 1 - y, p.getPixel(x, y))
            }
        }

        fbo.end()

        val thumbnail = Texture(flipped)

        fbo.dispose()
        p.dispose()
        flipped.dispose()

        return thumbnail
    }
}