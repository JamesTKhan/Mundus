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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.meta.MetaLoader;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Read-only asset manager.
 *
 * @author Marcus Brummer
 * @version 06-10-2016
 */
@SuppressWarnings("NonJREEmulationClassesInClientCode")
public class AssetManager implements Disposable {

    private static final String TAG = AssetManager.class.getSimpleName();

    protected FileHandle rootFolder;

    protected Array<Asset> assets;
    protected Map<String, Asset> assetIndex;

    /**
     * Asset manager constructor.
     *
     * @param assetsFolder
     *            root directory of assets
     */
    public AssetManager(FileHandle assetsFolder) {
        this.rootFolder = assetsFolder;
        this.assets = new Array<Asset>();
        this.assetIndex = new HashMap<String, Asset>();
    }

    /**
     * Returns an asset by id.
     *
     * @param id
     *            id of asset
     * @return matching asset or null
     */
    public Asset findAssetByID(String id) {
        if (id == null) return null;
        return assetIndex.get(id);
    }

    /**
     * Returns an asset by filename, else null if not found.
     * @param fileName the filename to search
     * @return matching asset or null
     */
    public Asset findAssetByFileName(String fileName) {
        for (Asset asset : assets) {
            if (asset.file.name().equals(fileName))
                return asset;
        }
        return null;
    }

    public Map<String, Asset> getAssetMap() {
        return assetIndex;
    }

    public void addAsset(Asset asset) {
        if (asset == null) return;
        if (assetIndex.get(asset.getID()) == null) {
            assets.add(asset);
            assetIndex.put(asset.getID(), asset);
        }
    }

    /**
     * Returns all assets.
     *
     * @return all assets
     */
    public Array<Asset> getAssets() {
        return assets;
    }

    /**
     * Returns all assets of type MODEL.
     *
     * @return all model assets
     */
    public Array<ModelAsset> getModelAssets() {
        Array<ModelAsset> models = new Array<ModelAsset>();
        for (Asset asset : assets) {
            if (asset instanceof ModelAsset) {
                models.add((ModelAsset) asset);
            }
        }

        return models;
    }

    /**
     * Returns all assets of type TERRAIN.
     *
     * @return all model assets
     */
    public Array<TerrainAsset> getTerrainAssets() {
        Array<TerrainAsset> terrains = new Array<TerrainAsset>();
        for (Asset asset : assets) {
            if (asset instanceof TerrainAsset) {
                terrains.add((TerrainAsset) asset);
            }
        }

        return terrains;
    }

    /**
     * Returns all assets of type MATERIAL.
     *
     * @return all model assets
     */
    public Array<MaterialAsset> getMaterialAssets() {
        Array<MaterialAsset> materials = new Array<MaterialAsset>();
        for (Asset asset : assets) {
            if (asset instanceof MaterialAsset) {
                materials.add((MaterialAsset) asset);
            }
        }

        return materials;
    }

    /**
     * Loads all assets in the project's asset folder.
     *
     * @param listener
     *            informs about current loading progress
     * @param isRuntime
     *            is this called by the runtime or editor (runtime requires different file logic)
     * @throws AssetNotFoundException
     *             if a meta file points to a non existing asset
     * @throws MetaFileParseException
     *             if a meta file can't be parsed
     */
    public void loadAssets(AssetLoadingListener listener, boolean isRuntime) throws AssetNotFoundException, MetaFileParseException {
        final MetaLoader metaLoader = new MetaLoader();

        String[] files;
        FileHandle fileList;
        FileHandle[] metaFiles;

        if (isRuntime && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            // Desktop applications cannot use .list() for internal jar files.
            // Application will need to provide an assets.txt file listing all Mundus assets
            // in the Mundus root directory.
            // https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/
            fileList = rootFolder.child("assets.txt");
            files = fileList.readString().split("\\n");
            metaFiles = getMetaFiles(files);
        } else if (isRuntime && Gdx.app.getType() == Application.ApplicationType.WebGL) {
            // For WebGL we use a native split method for string split
            fileList = rootFolder.child("assets.txt");
            files = split(fileList.readString(), "\n");
            metaFiles = getMetaFiles(files);
        } else {
            // Editor uses this block to load meta files
            FileFilter metaFileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith(Meta.META_EXTENSION);
                }
            };
            metaFiles = rootFolder.list(metaFileFilter);
        }

        // load assets
        for (FileHandle meta : metaFiles) {
            Asset asset = loadAsset(metaLoader.load(meta));
            if (listener != null) {
                listener.onLoad(asset, assets.size, metaFiles.length);
            }
        }

        // resolve material assets
        for (Asset asset : assets) {
            if (asset instanceof MaterialAsset) {
                asset.resolveDependencies(assetIndex);
                asset.applyDependencies();
            }
        }

        // resolve other assets
        for (Asset asset : assets) {
            if (asset instanceof MaterialAsset) continue;
            asset.resolveDependencies(assetIndex);
            asset.applyDependencies();
        }

        if(listener != null) {
            listener.onFinish(assets.size);
        }
    }

    /**
     * Get an array of Meta FileHandles for the given String array of file names.
     *
     * @param files the array of file names to retrieve meta filehandles from
     * @return FileHandle array of meta files.
     */
    private FileHandle[] getMetaFiles(String[] files) {
        // Get meta file extension file names
        Array<String> metalFileNames = new Array<>();
        for (String filename: files) {
            if (filename.endsWith(Meta.META_EXTENSION)) {
                metalFileNames.add(filename);
            }
        }

        FileHandle[] metaFiles = new FileHandle[metalFileNames.size];
        for (int i = 0; i < metaFiles.length; i++) {
            metaFiles[i] = rootFolder.child(metalFileNames.get(i));
        }

        return metaFiles;
    }

    /**
     * Loads an asset, given it's meta file.
     *
     * @param meta
     *            meta file of asset
     * @return asset or null
     * @throws AssetNotFoundException
     *             if a meta file points to a non existing asset
     * @throws MetaFileParseException
     *             if a meta file can't be parsed
     */
    public Asset loadAsset(Meta meta) throws MetaFileParseException, AssetNotFoundException {
        // get handle to asset
     //   String assetPath = meta.getFile().pathWithoutExtension();
        FileHandle assetFile = meta.getFile().sibling(meta.getFile().nameWithoutExtension());

        // check if asset exists
        if (!assetFile.exists()) {
            throw new AssetNotFoundException("Meta file found, but asset does not exist: " + meta.getFile().path());
        }

        // load actual asset
        Asset asset = null;
        switch (meta.getType()) {
            case TEXTURE:
                asset = loadTextureAsset(meta, assetFile);
                break;
            case PIXMAP_TEXTURE:
                asset = loadPixmapTextureAsset(meta, assetFile);
                break;
            case TERRAIN:
                asset = loadTerrainAsset(meta, assetFile);
                break;
            case MODEL:
                asset = loadModelAsset(meta, assetFile);
                break;
            case MATERIAL:
                asset = loadMaterialAsset(meta, assetFile);
                break;
            case WATER:
                asset = loadWaterAsset(meta, assetFile);
                break;
            case SKYBOX:
                asset = loadSkyboxAsset(meta, assetFile);
                break;
            default:
                return null;
        }

        addAsset(asset);
        return asset;
    }

    private Asset loadSkyboxAsset(Meta meta, FileHandle assetFile) {
        SkyboxAsset asset = new SkyboxAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private MaterialAsset loadMaterialAsset(Meta meta, FileHandle assetFile) {
        MaterialAsset asset = new MaterialAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private TextureAsset loadTextureAsset(Meta meta, FileHandle assetFile) {
        TextureAsset asset = new TextureAsset(meta, assetFile);
        // TODO parse special texture instead of always setting them
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();
        return asset;
    }

    private TerrainAsset loadTerrainAsset(Meta meta, FileHandle assetFile) {
        TerrainAsset asset = new TerrainAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private PixmapTextureAsset loadPixmapTextureAsset(Meta meta, FileHandle assetFile) {
        PixmapTextureAsset asset = new PixmapTextureAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private ModelAsset loadModelAsset(Meta meta, FileHandle assetFile) {
        ModelAsset asset = new ModelAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    private WaterAsset loadWaterAsset(Meta meta, FileHandle assetFile) {
        WaterAsset asset = new WaterAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    @Override
    public void dispose() {
        for (Asset asset : assets) {
            asset.dispose();
            Gdx.app.log(TAG, "Disposing asset: " + asset.toString());
        }
        assets.clear();
        assetIndex.clear();
    }

    /**
     * Native JavaScript string split method for GWT support
     */
    public static final native String[] split(String string, String separator) /*-{
        return string.split(separator);
    }-*/;

    /**
     * Used to inform users about the current loading status.
     */
    public interface AssetLoadingListener {
        /**
         * Called if an asset loaded
         * 
         * @param asset
         *            loaded asset
         * @param progress
         *            number of already loaded assets
         * @param assetCount
         *            total number of assets
         */
        void onLoad(Asset asset, int progress, int assetCount);

        /**
         * Called if all assets loaded.
         * 
         * @param assetCount
         *            total number of loaded assets
         */
        void onFinish(int assetCount);
    }

}
