/*
 * Copyright (c) 2021. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.core.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.editor.core.converter.SceneConverter;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SceneManager {

    private static final Json JSON = new Json();


    /**
     * Saves a scene.
     *
     * @param context project context of the scene
     * @param scene scene to save
     */
    public static void saveScene(ProjectContext context, Scene scene) {
        String sceneDir = getScenePath(context, scene.getName());

        SceneDTO sceneDTO = SceneConverter.convert(scene);
        FileHandle saveFile = Gdx.files.absolute(sceneDir);
        saveFile.writeString(JSON.toJson(sceneDTO), false);
    }

    /**
     * Loads a scene.
     *
     * Does however not initialize ModelInstances, Terrains, ... -> ProjectManager
     *
     * @param context project context of the scene
     * @param sceneName name of the scene to load
     * @return loaded scene
     * @throws FileNotFoundException
     */
    public static SceneDTO loadScene(ProjectContext context, String sceneName) throws FileNotFoundException {
        String sceneDir = getScenePath(context, sceneName);
        return JSON.fromJson(SceneDTO.class, new FileInputStream(sceneDir));
    }

    /**
     * Renames scene.
     *
     * @param context The projet context of scene.
     * @param oldSceneName The old name of scene.
     * @param newSceneName The new name of scene.
     */
    public static void renameScene(final ProjectContext context, final String oldSceneName, final String newSceneName) {
        final String oldSceneDir = getScenePath(context, oldSceneName);
        final String newSceneDir = getScenePath(context, newSceneName);
        final SceneDTO scene = JSON.fromJson(SceneDTO.class, new FileHandle(oldSceneDir));
        scene.setName(newSceneName);

        FileHandle saveFile = Gdx.files.absolute(newSceneDir);
        saveFile.writeString(JSON.toJson(scene), false);

        deleteScene(context, oldSceneName);
    }

    /**
     * Deletes scene.
     *
     * @param context project context of the scene
     * @param sceneName name of the scene to remove
     */
    public static void deleteScene(final ProjectContext context, final String sceneName) {
        final String sceneDir = getScenePath(context, sceneName);
        FileHandle sceneFile = Gdx.files.absolute(sceneDir);
        sceneFile.delete();
    }

    private static String getScenePath(ProjectContext context, String sceneName) {
        return FilenameUtils.concat(context.path + "/" + ProjectManager.PROJECT_SCENES_DIR,
                sceneName + "." + ProjectManager.PROJECT_SCENE_EXTENSION);
    }
}
