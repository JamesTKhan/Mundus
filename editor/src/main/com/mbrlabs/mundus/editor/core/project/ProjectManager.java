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

package com.mbrlabs.mundus.editor.core.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.ModelAsset;
import com.mbrlabs.mundus.commons.assets.SkyboxAsset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;
import com.mbrlabs.mundus.commons.assets.meta.MetaFileParseException;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.scene3d.components.WaterComponent;
import com.mbrlabs.mundus.editor.Main;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.assets.AssetAlreadyExistsException;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.EditorScene;
import com.mbrlabs.mundus.editor.core.converter.SceneConverter;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.scene.SceneManager;
import com.mbrlabs.mundus.editor.events.LogEvent;
import com.mbrlabs.mundus.editor.events.LogType;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.scene3d.components.PickableComponent;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.utils.Log;
import com.mbrlabs.mundus.editor.utils.SkyboxBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Manages Mundus projects and scenes.
 *
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager implements Disposable {

    private static final String TAG = ProjectManager.class.getSimpleName();

    private static final String DEFAULT_SCENE_NAME = "Main Scene";
    public static final String PROJECT_ASSETS_DIR = "assets/";
    public static final String PROJECT_SCENES_DIR = "scenes/";
    public static final String PROJECT_SCENE_EXTENSION = "mundus";
    public static final String PROJECT_EXTENSION = "pro";

    private ProjectContext currentProject;
    private Registry registry;
    private KryoManager kryoManager;
    private ModelBatch modelBatch;

    public ProjectManager(KryoManager kryoManager, Registry registry, ModelBatch modelBatch) {
        this.registry = registry;
        this.kryoManager = kryoManager;
        this.modelBatch = modelBatch;
        currentProject = new ProjectContext(-1);
    }

    /**
     * Returns current project.
     *
     * @return current project
     */
    public ProjectContext current() {
        return currentProject;
    }

    /**
     * Saves the active project
     */
    public void saveCurrentProject() {
        saveProject(currentProject);
    }

    public String assetFolder() {
        return currentProject.path + "/" + PROJECT_ASSETS_DIR;
    }

    /**
     * Creates & saves a new project.
     *
     * Creates a new project. However, it does not switch the current project.
     *
     * @param name
     *            project name
     * @param folder
     *            absolute path to project folder
     * @return new project context
     */
    public ProjectContext createProject(String name, String folder) {
        ProjectRef ref = registry.createProjectRef(name, folder);
        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_ASSETS_DIR).mkdirs();
        new File(path, PROJECT_SCENES_DIR).mkdirs();

        // create currentProject current
        ProjectContext newProjectContext = new ProjectContext(-1);
        newProjectContext.path = path;
        newProjectContext.name = ref.getName();
        newProjectContext.assetManager = new EditorAssetManager(
                new FileHandle(path + "/" + ProjectManager.PROJECT_ASSETS_DIR));

        // create default scene & save .mundus
        EditorScene scene = new EditorScene();
        scene.setName(DEFAULT_SCENE_NAME);
        scene.skybox = SkyboxBuilder.createDefaultSkybox(Shaders.INSTANCE.getSkyboxShader());
        scene.skyboxAssetId = getDefaultSkyboxAsset(newProjectContext, true).getID();
        scene.setId(newProjectContext.obtainID());
        SceneManager.saveScene(newProjectContext, scene);
        scene.sceneGraph.scene.batch = modelBatch;

        // save .pro file
        newProjectContext.scenes.add(scene.getName());
        newProjectContext.currScene = scene;
        saveProject(newProjectContext);

        // create standard assets
        newProjectContext.assetManager.createStandardAssets();

        // Generate assets.txt file
        newProjectContext.assetManager.createAssetsTextFile();

        return newProjectContext;
    }

    /**
     * Gets the default skybox, if it can be found.
     *
     * @param projectContext the project context to use
     * @param createIfMissing if true, creates default skybox if it's missing
     * @return skybox asset
     */
    public SkyboxAsset getDefaultSkyboxAsset(ProjectContext projectContext, boolean createIfMissing) {

        // See if the default skybox already exists
        SkyboxAsset defaultSkybox = (SkyboxAsset) projectContext.assetManager.findAssetByFileName("default.sky");

        if (defaultSkybox == null && createIfMissing) {
            FileHandle texture = Gdx.files.internal("textures/skybox/default/skybox_default.png");
            TextureAsset textureAsset = projectContext.assetManager.getOrCreateTextureAsset(texture);

            String id = textureAsset.getID();
            // Create it if it does not exist
            try {
                defaultSkybox = projectContext.assetManager.createSkyBoxAsset("default", id, id, id, id, id, id);
                defaultSkybox.positiveX = textureAsset;
                defaultSkybox.negativeX = textureAsset;
                defaultSkybox.positiveY = textureAsset;
                defaultSkybox.negativeY = textureAsset;
                defaultSkybox.positiveZ = textureAsset;
                defaultSkybox.negativeZ = textureAsset;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AssetAlreadyExistsException e) {
                e.printStackTrace();
            }

            if (defaultSkybox == null)
                throw new GdxRuntimeException("Unable to get or create default skybox.");
        }

        return defaultSkybox;
    }

    /**
     * Imports (opens) a mundus project, that is not in the registry.
     *
     * @param absolutePath
     *            path to project
     * @return project context of imported project
     * @throws ProjectAlreadyImportedException
     *             if project exists already in registry
     * @throws ProjectOpenException
     *             project could not be opened
     */
    public ProjectContext importProject(String absolutePath)
            throws ProjectAlreadyImportedException, ProjectOpenException {
        // check if already imported
        for (ProjectRef ref : registry.getProjects()) {
            if (ref.getPath().equals(absolutePath)) {
                throw new ProjectAlreadyImportedException("Project " + absolutePath + " is already imported");
            }
        }

        ProjectRef ref = new ProjectRef();
        ref.setPath(absolutePath);

        try {
            ProjectContext context = loadProject(ref);
            ref.setName(context.name);
            registry.getProjects().add(ref);
            kryoManager.saveRegistry(registry);
            return context;
        } catch (Exception e) {
            throw new ProjectOpenException(e.getMessage());
        }
    }

    /**
     * Loads the project context for a project.
     *
     * This does not open to that project, it only loads it.
     *
     * @param ref
     *            project reference to the project
     * @return loaded project context
     * @throws FileNotFoundException
     *             if project can't be found
     */
    public ProjectContext loadProject(ProjectRef ref)
            throws FileNotFoundException, MetaFileParseException, AssetNotFoundException {
        ProjectContext context = kryoManager.loadProjectContext(ref);
        context.path = ref.getPath();

        // load assets
        loadAssets(ref, context);

        // create standard assets if any are missing, to support backwards compatibility when new standard assets are added
        boolean standardAssetReloaded = context.assetManager.createStandardAssets();

        // If a standard asset was missing we reload assets, now that the standard asset is recreated.
        if (standardAssetReloaded) {
            Mundus.INSTANCE.postEvent(new LogEvent(LogType.WARN, "A standard asset was missing. Reloading assets." +
                    " This only occurs if a standard asset was deleted."));
            loadAssets(ref, context);
        }

        context.currScene = loadScene(context, context.activeSceneName);

        return context;
    }

    private void loadAssets(ProjectRef ref, ProjectContext context) throws MetaFileParseException, AssetNotFoundException {
        context.assetManager = new EditorAssetManager(
                new FileHandle(ref.getPath() + "/" + ProjectManager.PROJECT_ASSETS_DIR));

        context.assetManager.loadAssets(new AssetManager.AssetLoadingListener() {
            @Override
            public void onLoad(Asset asset, int progress, int assetCount) {
                Log.debug(TAG, "Loaded {} asset ({}/{})", asset.getMeta().getType(), progress, assetCount);
                Mundus.INSTANCE.postEvent(new LogEvent("Loaded " + asset.getMeta().getType() + " asset ("+progress+"/"+assetCount+")"));
            }

            @Override
            public void onFinish(int assetCount) {
                Log.debug(TAG, "Finished loading {} assets", assetCount);
                Mundus.INSTANCE.postEvent(new LogEvent("Finished loading " + assetCount + " assets"));
            }
        }, false);
    }

    /**
     * Completely saves a project & all scenes.
     *
     * @param projectContext
     *            project context
     */
    public void saveProject(ProjectContext projectContext) {
        // save modified assets
        EditorAssetManager assetManager = projectContext.assetManager;
        for (Asset asset : assetManager.getModifiedAssets()) {
            try {
                Log.debug(TAG, "Saving modified asset: {}", asset);
                assetManager.saveAsset(asset);
                System.out.println(asset.getName());
            } catch (IOException e) {
                Log.exception(TAG, e);
            }
        }
        assetManager.getModifiedAssets().clear();

        for (Asset asset : assetManager.getNewAssets()) {
            try {
                Log.debug(TAG, "Saving new asset: {}", asset);
                assetManager.saveAsset(asset);
                System.out.println(asset.getName());
            } catch (IOException e) {
                Log.exception(TAG, e);
            }
        }
        assetManager.getNewAssets().clear();

        // Generate assets.txt file
        assetManager.createAssetsTextFile();

        // save current in .pro file
        kryoManager.saveProjectContext(projectContext);
        // save scene in .mundus file
        SceneManager.saveScene(projectContext, projectContext.currScene);

        Log.debug(TAG, "Saving currentProject {}", projectContext.name + " [" + projectContext.path + "]");
        Mundus.INSTANCE.postEvent(new LogEvent("Saving currentProject " + projectContext.name + " [" + projectContext.path + "]"));
    }

    /**
     * Loads the project that was open when the user quit the program.
     *
     * Does not open open the project.
     *
     * @return project context of last project
     */
    public ProjectContext loadLastProject() {
        ProjectRef lastOpenedProject = registry.getLastOpenedProject();
        if (lastOpenedProject != null) {
            try {
                return loadProject(lastOpenedProject);
            } catch (FileNotFoundException fnf) {
                Log.error(TAG, fnf.getMessage());
                fnf.printStackTrace();
            } catch (AssetNotFoundException anf) {
                Log.error(TAG, anf.getMessage());
            } catch (MetaFileParseException mfp) {
                Log.error(TAG, mfp.getMessage());
            }
            return null;
        }

        return null;
    }

    /**
     * Opens a project.
     *
     * Opens a project. If a project is already open it will be disposed.
     * 
     * @param context
     *            project context to open
     */
    public void changeProject(ProjectContext context) {
        if (currentProject != null) {
            if (currentProject.assetManager != null) {
                currentProject.assetManager.deleteNewUnsavedAssets();
            }
            currentProject.dispose();
        }

        currentProject = context;
        // currentProject.copyFrom(context);
        registry.setLastProject(new ProjectRef());
        registry.getLastOpenedProject().setName(context.name);
        registry.getLastOpenedProject().setPath(context.path);

        kryoManager.saveRegistry(registry);

        Gdx.graphics.setTitle(constructWindowTitle());
        Mundus.INSTANCE.postEvent(new ProjectChangedEvent(context));
    }

    /**
     * Creates a new scene for the given project.
     *
     * @param project
     *            project
     * @param name
     *            scene name
     * @return newly created scene
     */
    public Scene createScene(ProjectContext project, String name) {
        Scene scene = new Scene();
        long id = project.obtainID();
        scene.setId(id);
        scene.setName(name);
        scene.skyboxAssetId = getDefaultSkyboxAsset(project, false).getID();
        if (scene.skyboxAssetId != null)
            scene.skybox = SkyboxBuilder.createDefaultSkybox(Shaders.INSTANCE.getSkyboxShader());
        project.scenes.add(scene.getName());
        SceneManager.saveScene(project, scene);

        return scene;
    }

    /**
     * Loads a scene.
     *
     * This does not open the scene.
     *
     * @param context
     *            project context of the scene
     * @param sceneName
     *            name of the scene
     * @return loaded scene
     * @throws FileNotFoundException
     *             if scene file not found
     */
    public EditorScene loadScene(ProjectContext context, String sceneName) throws FileNotFoundException {
        SceneDTO sceneDTO = SceneManager.loadScene(context, sceneName);

        EditorScene scene = SceneConverter.convert(sceneDTO, context.assetManager.getAssetMap());

        // load skybox
        if (scene.skyboxAssetId != null && context.assetManager.getAssetMap().containsKey(scene.skyboxAssetId)) {
            SkyboxAsset asset = (SkyboxAsset) context.assetManager.getAssetMap().get(scene.skyboxAssetId);
            scene.setSkybox(asset, Shaders.INSTANCE.getSkyboxShader());
        }

        scene.batch = modelBatch;

        scene.setShadowMapShader(Shaders.INSTANCE.getShadowMapShader());
        scene.setDepthShader(Shaders.INSTANCE.getDepthShader());

        scene.setShadowQuality(sceneDTO.getShadowResolution());

        SceneGraph sceneGraph = scene.sceneGraph;
        for (GameObject go : sceneGraph.getGameObjects()) {
            initGameObject(context, go);
        }

        // create TerrainGroup for active scene
        Array<Component> terrainComponents = new Array<>();
        for (GameObject go : sceneGraph.getGameObjects()) {
            go.findComponentsByType(terrainComponents, Component.Type.TERRAIN, true);
        }
        for (Component c : terrainComponents) {
            if (c instanceof TerrainComponent) {
                scene.terrains.add(((TerrainComponent) c).getTerrain());
            }
        }

        return scene;
    }

    /**
     * Loads and opens scene
     *
     * @param projectContext
     *            project context of scene
     * @param sceneName
     *            scene name
     */
    public void changeScene(ProjectContext projectContext, String sceneName) {
        try {
            EditorScene newScene = loadScene(projectContext, sceneName);
            projectContext.currScene.dispose();
            projectContext.currScene = newScene;

            Gdx.graphics.setTitle(constructWindowTitle());
            Mundus.INSTANCE.postEvent(new SceneChangedEvent());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.error(TAG, e.getMessage());
        }
    }

    private void initGameObject(ProjectContext context, GameObject root) {
        initComponents(context, root);
        if (root.getChildren() != null) {
            for (GameObject c : root.getChildren()) {
                initGameObject(context, c);
            }
        }
    }

    private void initComponents(ProjectContext context, GameObject go) {
        Array<ModelAsset> models = context.assetManager.getModelAssets();
        Array.ArrayIterator<Component> iterator = go.getComponents().iterator();
        while(iterator.hasNext()) {
            Component c = iterator.next();
            if (c == null) {
                // To prevent crashing, log a warning statement and remove the corrupted component
                iterator.remove();
                Log.warn(TAG, "A component for {} was null on load, this may be caused by deleting an asset that is still in a scene.", go);
                Mundus.INSTANCE.postEvent(new LogEvent(LogType.ERROR, "A component for "+ go.name +"  was null on load, this may be caused by deleting an asset that is still in a scene."));
                go.name = go.name.concat(" [COMPONENT ERROR]");
                continue;
            }
            // Model component
            if (c.getType() == Component.Type.MODEL) {
                ModelComponent modelComponent = (ModelComponent) c;
                ModelAsset model = findModelById(models, modelComponent.getModelAsset().getID());
                if (model != null) {
                    modelComponent.setModel(model, false);
                } else {
                    Log.fatal(TAG, "model for modelInstance not found: {}", modelComponent.getModelAsset().getID());
                }
            } else if (c.getType() == Component.Type.TERRAIN) {
                ((TerrainComponent) c).getTerrain().getTerrain().setTransform(go.getTransform());
            } else if (c.getType() == Component.Type.WATER) {
                ((WaterComponent) c).getWaterAsset().water.setTransform(go.getTransform());
            }

            // encode id for picking
            if (c instanceof PickableComponent) {
                ((PickableComponent) c).encodeRaypickColorId();
            }
        }
    }

    private ModelAsset findModelById(Array<ModelAsset> models, String id) {
        for (ModelAsset m : models) {
            if (m.getID().equals(id)) {
                return m;
            }
        }

        return null;
    }

    private String constructWindowTitle() {
        return currentProject.name + " - " + currentProject.currScene.getName() + " [" + currentProject.path + "]"
                + " - " + Main.TITLE;
    }

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    /**
     * Disposes current model batch, assigns the new batch and updates the batch
     * on the current scene.
     *
     * @param modelBatch new ModelBatch instance to use
     */
    public void setModelBatch(ModelBatch modelBatch) {
        if (this.modelBatch != null) {
            this.modelBatch.dispose();
        }

        this.modelBatch = modelBatch;

        if (currentProject != null && currentProject.currScene != null) {
            currentProject.currScene.batch = this.modelBatch;
        }
    }

    @Override
    public void dispose() {
        currentProject.dispose();
    }
}
