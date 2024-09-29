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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.meta.MetaFileParseException;
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter;
import com.mbrlabs.mundus.commons.shaders.MundusPBRShaderProvider;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;

/**
 * @author Marcus Brummer
 * @version 27-10-2016
 */
public class Mundus implements Disposable {
    public static final String PROJECT_ASSETS_DIR = "assets";
    public static final String PROJECT_SCENES_DIR = "scenes";

    private static final String TAG = Mundus.class.getSimpleName();

    private final SceneLoader sceneLoader;
    private final AssetManager assetManager;
    private final FileHandle root;

    private Shaders shaders;

    /**
     * Initializes Mundus, defaulting to synchronous loading of assets
     * @param mundusRoot FileHandle to the root directory of the Mundus project to load
     */
    public Mundus(final FileHandle mundusRoot) {
        this(mundusRoot, new Config());
    }

    public Mundus(final FileHandle mundusRoot, CustomComponentConverter... customComponentConverters) {
        this(mundusRoot, new Config(), customComponentConverters);
    }

    /**
     * Initializes Mundus and begins loading assets.
     *
     * If async is true,
     * {@link #continueLoading()} should be called every frame until it returns true before loading any scenes.
     *
     * If you want to control when loading is started you can set autoLoad to false.
     * A possible use case is if you want to add non-mundus assets (like music files)
     * to the assetManager for inclusion in asynchronous loading.
     *
     * Ex.
     * <pre>
     * {@code
     *
     * 	   Mundus.Config config = new Mundus.Config();
     *     config.autoLoad = true;
     *
     *     mundus = new Mundus(Gdx.files.internal("MundusExampleProject"), config);
     *     mundus.getAssetManager().queueAssetsForLoading(true);
     *
     *     // Queuing up your own assets to include in asynchronous loading
     *     mundus.getAssetManager().getGdxAssetManager().load("Instrumental.mp3", Music.class);
     *
     *     // Retrieving your custom asset later on after loading is completed
     *     Music music = mundus.getAssetManager().getGdxAssetManager().get("Instrumental.mp3");
     * }
     * </pre>
     *
     * @param mundusRoot FileHandle to the root directory of the Mundus project to load
     * @param config the configuration to use
     * @param customComponentConverters the converters for custom components. these fields are optional / nullable
     */
    public Mundus(final FileHandle mundusRoot, Config config, CustomComponentConverter... customComponentConverters) {
        this.root = mundusRoot;
        this.assetManager = new AssetManager(root.child(PROJECT_ASSETS_DIR));
        this.sceneLoader = new SceneLoader(this, root.child(PROJECT_SCENES_DIR), customComponentConverters);

        if (config.autoLoad) {
            init(config.asyncLoad);
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Shaders getShaders() {
        if (shaders == null) {
            initShaders();
        }
        return shaders;
    }

    /**
     * Loads a Scene. This is the default way to load a scene. Use overloaded
     * methods if more customization is needed.
     */
    public Scene loadScene(final String name) {
        PBRShaderConfig config = ShaderUtils.buildPBRShaderConfig(assetManager.maxNumBones);
        return loadScene(name, config);
    }

    /**
     * Optionally pass in your own PBRShaderConfig.
     */
    public Scene loadScene(final String name, PBRShaderConfig config) {
        return loadScene(name, config, new SceneRenderableSorter());
    }

    /**
     * Provide your own PBRShaderConfig and RenderableSorter
     */
    public Scene loadScene(final String name, PBRShaderConfig config, RenderableSorter renderableSorter) {
        return loadScene(name, config, ShaderUtils.buildPBRShaderDepthConfig(assetManager.maxNumBones), renderableSorter);
    }

    /**
     * Provide your own PBRShaderConfig and DepthShader config
     */
    public Scene loadScene(final String name, PBRShaderConfig config, DepthShader.Config depthConfig) {
        return loadScene(name, config, depthConfig, new SceneRenderableSorter());
    }

    /**
     * Provide your own PBRShaderConfig, DepthShader config and RenderableSorter
     */
    public Scene loadScene(final String name, PBRShaderConfig config, DepthShader.Config depthConfig, RenderableSorter renderableSorter) {
        final Scene scene = sceneLoader.load(name);
        scene.batch = new ModelBatch(new MundusPBRShaderProvider(config), renderableSorter);

        scene.depthBatch = new ModelBatch(new PBRDepthShaderProvider(depthConfig));

        return scene;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    public void init(boolean async) {
        try {
            assetManager.queueAssetsForLoading(true);
            if (!async) {
                assetManager.finalizeLoad();
            }
        } catch (Exception e) {
            Gdx.app.log(TAG, e.getMessage());
        }
        initShaders();
    }

    public void initShaders() {
        shaders = new Shaders();
    }

    /**
     * Should be called each frame until it returns true
     * which indicates that Mundus is loaded.
     *
     * @return boolean indicating if asynchronous loading is complete
     */
    public boolean continueLoading() {
        try {
            return assetManager.continueLoading();
        } catch (MetaFileParseException | AssetNotFoundException e) {
            throw new GdxRuntimeException("Error while loading assets: " + e);
        }
    }

    /**
     * Returns a progress value between 0.0 and 1.0 representing the percentage loaded.
     * @return progress percentage
     */
    public float getProgress() {
        return assetManager.getProgress();
    }

    public static class Config {
        /** Start loading assets immediately */
        public boolean autoLoad = true;
        /** Load the project asynchronously (true) or synchronously (false)*/
        public boolean asyncLoad = false;

    }

}
