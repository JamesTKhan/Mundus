/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.exporter

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.kotcrab.vis.ui.util.async.AsyncTask
import com.kotcrab.vis.ui.util.async.AsyncTaskListener
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.commons.dto.GameObjectDTO
import com.mbrlabs.mundus.commons.dto.ModelComponentDTO
import com.mbrlabs.mundus.commons.dto.SceneDTO
import com.mbrlabs.mundus.commons.dto.TerrainComponentDTO
import com.mbrlabs.mundus.commons.importer.JsonScene
import com.mbrlabs.mundus.editor.core.converter.SceneConverter
import com.mbrlabs.mundus.editor.core.io.IOManager
import com.mbrlabs.mundus.editor.core.project.ProjectContext
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.core.scene.SceneManager
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.Writer

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
class Exporter(val ioManager: IOManager, val project: ProjectContext) {

    /**
     *
     */
    fun exportAsync(outputFolder: FileHandle, listener: AsyncTaskListener) {

        // convert current project on the main thread to avoid nested array iterators
        // because it would iterate over the scene graph arrays while rendering (on the main thread)
        // and while converting (on the other thread)
        val currentSceneDTO = SceneConverter.convert(project.currScene)
        val jsonType = project.settings.export.jsonType

        val task = object: AsyncTask("export_${project.name}") {
            override fun doInBackground() {
                val assetManager = project.assetManager
                val step = 100f / (assetManager.assets.size + project.scenes.size)
                var progress = 0f

                // create folder structure
                createFolders(outputFolder)

                // copy assets
                val assetFolder = FileHandle(FilenameUtils.concat(outputFolder.path(), "assets/"))
                val scenesFolder = FileHandle(FilenameUtils.concat(outputFolder.path(), "scenes/"))

                // sleep a bit to open the progress dialog
                Thread.sleep(250)

                for(asset in assetManager.assets) {
                    exportAsset(asset, assetFolder)
                    progress += step
                    setProgressPercent(progress.toInt())
                    setMessage(asset.id)
                    Thread.sleep(50)
                }

                // load, convert & copy scenes
                for(sceneName in project.scenes) {
                    val file = FileHandle(FilenameUtils.concat(scenesFolder.path(),
                            sceneName + "." + ProjectManager.PROJECT_SCENE_EXTENSION))

                    // load from disk or convert current scene
                    var scene: SceneDTO
                    if(project.currScene.name == sceneName) {
                        scene = currentSceneDTO
                    } else {
                        scene = SceneManager.loadScene(project, sceneName)
                    }

                    // convert & export
                    exportScene(scene, file, jsonType)
                    progress += step
                    setProgressPercent(progress.toInt())
                    setMessage(scene.name)
                    Thread.sleep(50)
                }
            }
        }

        task.addListener(listener)
        task.execute()
    }

    private fun createFolders(exportRootFolder: FileHandle) {
        // ROOT/assets
        val assets = File(FilenameUtils.concat(exportRootFolder.path(), "assets/"))
        assets.mkdirs()

        // ROOT/scenes
        val scenes = File(FilenameUtils.concat(exportRootFolder.path(), "scenes/"))
        scenes.mkdirs()
    }

    private fun exportAsset(asset: Asset, folder: FileHandle) {
        asset.file.copyTo(folder)
        asset.meta.file.copyTo(folder)
    }

    private fun exportScene(scene: SceneDTO, file: FileHandle, jsonType: JsonWriter.OutputType) {
        val writer = file.writer(false)
        exportScene(scene, writer, jsonType)
    }

    fun exportScene(scene: SceneDTO, writer: Writer, jsonType: JsonWriter.OutputType) {
        val json = Json()
        json.setOutputType(jsonType)
        json.setWriter(writer)

        json.writeObjectStart()

        // START basics
        json.writeValue(JsonScene.ID, scene.id)
        json.writeValue(JsonScene.NAME, scene.name)
        // END basics

        // START game objects
        json.writeArrayStart(JsonScene.GAME_OBJECTS)
        for(go in scene.gameObjects) {
            convertGameObject(go, json)
        }
        json.writeArrayEnd()
        // END game objects

        json.writeObjectEnd()

        json.writer.flush()
    }

    private fun convertGameObject(go: GameObjectDTO, json: Json) {
        // convert game object
        json.writeObjectStart()
        json.writeValue(JsonScene.GO_ID, go.id)
        json.writeValue(JsonScene.GO_NAME, go.name)
        json.writeValue(JsonScene.GO_ACTIVE, go.isActive)
        json.writeValue(JsonScene.GO_TRANSFORM, go.transform)

        // START tags
        json.writeArrayStart(JsonScene.GO_TAGS)
        if (go.tags != null) {
            for (tag in go.tags) {
                json.writeValue(tag)
            }
        }
        json.writeArrayEnd()
        // END tags

        // components
        if(go.modelComponent != null) convertModelComponent(go.modelComponent, json)
        if(go.terrainComponent != null) convertTerrainComponent(go.terrainComponent, json)

        // children
        for(child in go.childs) {
            json.writeArrayStart(JsonScene.GO_CHILDREN)
            convertGameObject(child, json)
            json.writeArrayEnd()
        }

        json.writeObjectEnd()
    }

    private fun convertModelComponent(comp: ModelComponentDTO, json: Json) {
        json.writeObjectStart(JsonScene.GO_MODEL_COMPONENT)
        json.writeValue(JsonScene.MODEL_COMPONENT_MODEL_ID, comp.modelID)

        // materials
        json.writeObjectStart(JsonScene.MODEL_COMPONENT_MATERIALS)
        for((key, value) in comp.materials) {
            json.writeValue(key, value)
        }
        json.writeObjectEnd()

        json.writeObjectEnd()
    }

    private fun convertTerrainComponent(comp: TerrainComponentDTO, json: Json) {
        json.writeObjectStart(JsonScene.GO_TERRAIN_COMPONENT)
        json.writeValue(JsonScene.TERRAIN_COMPONENT_TERRAIN_ID, comp.terrainID)
        json.writeObjectEnd()
    }


}
