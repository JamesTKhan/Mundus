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

package com.mbrlabs.mundus.runtime;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.runtime.converter.SceneConverter;

/**
 * @author Marcus Brummer
 * @version 27-10-2016
 */
public class SceneLoader {

    private Mundus mundus;
    private AssetManager assetManager;

    private FileHandle root;

    public SceneLoader(Mundus mundus, FileHandle scenesRoot) {
        this.mundus = mundus;
        this.assetManager = mundus.getAssetManager();
        this.root = scenesRoot;
    }

    public Scene load(String name) {
        Json json = new Json();
        SceneDTO sceneDTO = json.fromJson(SceneDTO.class, root.child(name));

        return SceneConverter.convert(sceneDTO, mundus.getShaders(), assetManager);
    }

}
