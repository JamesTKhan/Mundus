package com.mbrlabs.mundus.editor.assets

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import net.mgsx.gltf.data.GLTF
import net.mgsx.gltf.data.data.GLTFBuffer
import net.mgsx.gltf.data.texture.GLTFImage
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class FileHandleWithDependenciesTest {

    @Test
    fun `Test copyTo method with non GLTF file`() {
        // given: the file
        val fileName = "model.g3db"
        val file = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(file.name()).thenReturn(fileName)

        // and: the json parser
        val json = Mockito.mock(Json::class.java)

        // and: the destination directory
        val destination = Mockito.mock(FileHandle::class.java)

        // and: the testable object
        val tested = FileHandleWithDependencies(file, json = json)

        // when: the 'copyTo' method called
        tested.copyTo(destination)

        // then: 'copyTo' method called on the file to the destination file
        Mockito.verify(file).copyTo(destination)

        // and: json parser has not used
        Mockito.verify(json, Mockito.never()).fromJson(Mockito.eq(GLTF::class.java), Mockito.any(FileHandle::class.java))
    }

    @Test
    fun `Test copyTo method with GLTF file without dependencies`() {
        // given: the file with path
        val path = "/tmp/"
        val fileName = "model.gltf"
        val file = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(file.name()).thenReturn(fileName)

        // and: parent directory
        val parent = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(parent.path()).thenReturn(path)
        Mockito.`when`(file.parent()).thenReturn(parent)

        // and: parsed GLTF file
        val gltf = GLTF()
        val gltfBuffer = GLTFBuffer()
        gltf.buffers = Array()
        gltf.buffers.add(gltfBuffer)

        // and: the json parser
        val json = Mockito.mock(Json::class.java)
        Mockito.`when`(json.fromJson(Mockito.eq(GLTF::class.java), Mockito.any(FileHandle::class.java))).thenReturn(gltf)

        // and: the copied file and destination directory
        val copiedFile = Mockito.mock(FileHandle::class.java)
        val destination = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(destination.child(fileName)).thenReturn(copiedFile)

        // and: the testable object
        val tested = FileHandleWithDependencies(file, json = json)

        // when: the 'copyTo' method called
        tested.copyTo(destination)

        // then: 'copyTo' method called on the file to the destination file
        Mockito.verify(file).copyTo(destination)

        // and: gltf file has not updated
        Mockito.verify(json, Mockito.never()).toJson(gltf, copiedFile)
    }

    @Test
    fun `Test copyTo method with GLTF file with image dependency in same directory`() {
        // given: the file with path
        val path = "/tmp/"
        val fileName = "model.gltf"
        val file = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(file.name()).thenReturn(fileName)

        // and: parent directory
        val parent = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(parent.path()).thenReturn(path)
        Mockito.`when`(file.parent()).thenReturn(parent)

        // and: parsed GLTF file
        val gltf = GLTF()
        val gltfBuffer = GLTFBuffer()
        val gltfImage = GLTFImage()
        gltfImage.uri = "texture.png"

        gltf.buffers = Array()
        gltf.buffers.add(gltfBuffer)
        gltf.images = Array()
        gltf.images.add(gltfImage)

        // and: the json parser
        val json = Mockito.mock(Json::class.java)
        Mockito.`when`(json.fromJson(Mockito.eq(GLTF::class.java), Mockito.any(FileHandle::class.java))).thenReturn(gltf)

        // and: the copied file and destination directory
        val copiedFile = Mockito.mock(FileHandle::class.java)
        val destination = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(destination.child(fileName)).thenReturn(copiedFile)

        // and: the testable object
        val tested = FileHandleWithDependencies(file, json = json)

        // when: the 'copyTo' method called
        tested.copyTo(destination)

        // then: 'copyTo' method called on the file to the destination file
        Mockito.verify(file).copyTo(destination)

        // and: gltf file has been not updated
        Mockito.verify(json, Mockito.never()).toJson(gltf, copiedFile)
    }

    @Test
    fun `Test copyTo method with GLTF file with image dependency in subdirectory`() {
        // given: the file with path
        val path = "/tmp/"
        val fileName = "model.gltf"
        val file = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(file.name()).thenReturn(fileName)

        // and: parent directory
        val parent = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(parent.path()).thenReturn(path)
        Mockito.`when`(file.parent()).thenReturn(parent)

        // and: parsed GLTF file
        val gltf = GLTF()
        val gltfBuffer = GLTFBuffer()
        val gltfImage = GLTFImage()
        gltfImage.uri = "textures/texture.png"

        gltf.buffers = Array()
        gltf.buffers.add(gltfBuffer)
        gltf.images = Array()
        gltf.images.add(gltfImage)

        // and: the json parser
        val json = Mockito.mock(Json::class.java)
        Mockito.`when`(json.fromJson(Mockito.eq(GLTF::class.java), Mockito.any(FileHandle::class.java))).thenReturn(gltf)

        // and: the copied file and destination directory
        val copiedFile = Mockito.mock(FileHandle::class.java)
        val destination = Mockito.mock(FileHandle::class.java)
        Mockito.`when`(destination.child(fileName)).thenReturn(copiedFile)

        // and: the testable object
        val tested = FileHandleWithDependencies(file, json = json)

        // when: the 'copyTo' method called
        tested.copyTo(destination)

        // then: 'copyTo' method called on the file to the destination file
        Mockito.verify(file).copyTo(destination)

        // and: gltf file has been updated
        Mockito.verify(json).toJson(gltf, copiedFile)

        // and: the uri of image has been updated to the same directory with gltf file
        Assert.assertEquals("texture.png", gltfImage.uri)
    }
}
