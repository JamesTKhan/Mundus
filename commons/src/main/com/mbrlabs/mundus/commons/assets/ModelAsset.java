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
package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaModel;
import com.mbrlabs.mundus.commons.g3d.MG3dModelLoader;
import com.mbrlabs.mundus.commons.utils.FileFormatUtils;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public class ModelAsset extends Asset {

    private Model model;

    private Map<String, MaterialAsset> defaultMaterials;

    public ModelAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
        defaultMaterials = new HashMap<>();
    }

    public Model getModel() {
        return model;
    }

    public Map<String, MaterialAsset> getDefaultMaterials() {
        return defaultMaterials;
    }

    @Override
    public void load() {
        // TODO don't create a new loader each time
        if (FileFormatUtils.isG3DB(file)) {
            MG3dModelLoader loader = new MG3dModelLoader(new UBJsonReader());
            model = loader.loadModel(file);
        } else if (FileFormatUtils.isGLTF(file)) {
            GLTFLoader loader = new GLTFLoader();
            model = loader.load(file).scene.model;
        } else if (FileFormatUtils.isGLB(file)) {
            GLBLoader loader = new GLBLoader();
            model = loader.load(file).scene.model;
        } else {
            throw new GdxRuntimeException("Unsupported 3D model");
        }
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        // materials
        MetaModel metaModel = meta.getModel();
        for (String g3dbMatID : metaModel.getDefaultMaterials().keys()) {
            String uuid = metaModel.getDefaultMaterials().get(g3dbMatID);
            defaultMaterials.put(g3dbMatID, (MaterialAsset) assets.get(uuid));
        }
    }

    @Override
    public void applyDependencies() {
        if (model == null) return;

        // materials
        for (Material mat : model.materials) {
            MaterialAsset materialAsset = defaultMaterials.get(mat.id);
            if (materialAsset == null) continue;
            materialAsset.applyToMaterial(mat);
        }
    }

    @Override
    public void dispose() {
        if (model != null) {
            model.dispose();
        }
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        // if it's a MaterialAsset compare to the models materials
        if (assetToCheck instanceof MaterialAsset) {
            return defaultMaterials.containsValue(assetToCheck);
        }

        // check if the materials use the asset, like a texture asset
        for (Map.Entry<String, MaterialAsset> stringMaterialAssetEntry : defaultMaterials.entrySet()) {
            if (stringMaterialAssetEntry.getValue().usesAsset(assetToCheck)) {
                return true;
            }
        }
        return false;
    }
}
