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

package com.mbrlabs.mundus.editor.assets

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectSet
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.mbrlabs.mundus.commons.assets.*
import com.mbrlabs.mundus.commons.assets.meta.Meta
import com.mbrlabs.mundus.commons.assets.meta.MetaTerrain
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.AssetUsage
import com.mbrlabs.mundus.commons.utils.FileFormatUtils
import com.mbrlabs.mundus.commons.water.WaterFloatAttribute
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.EditorScene
import com.mbrlabs.mundus.editor.core.project.ProjectManager
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import com.mbrlabs.mundus.editor.ui.UI
import com.mbrlabs.mundus.editor.utils.Log
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.*
import java.util.*

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
class EditorAssetManager(assetsRoot: FileHandle) : AssetManager(assetsRoot) {

    companion object {
        private val TAG = EditorAssetManager::class.java.simpleName
        val STANDARD_ASSET_TEXTURE_CHESSBOARD = "chessboard"
        val STANDARD_ASSET_TEXTURE_DUDV = "dudv"
        val STANDARD_ASSET_TEXTURE_WATER_NORMAL = "waterNormal"
        val STANDARD_ASSET_TEXTURE_WATER_FOAM = "waterFoam"
    }

    /** Modified assets that need to be saved.  */
    private val modifiedAssets = ObjectSet<Asset>()

    /** New (Not modified) assets that need to be saved */
    private val newAssets = ObjectSet<Asset>()

    private val metaSaver = MetaSaver()

    init {
        if (rootFolder != null && (!rootFolder.exists() || !rootFolder.isDirectory)) {
            Log.fatal(TAG, "Root asset folder is not a directory")
        }
    }

    fun addModifiedAsset(asset: Asset) {
        // If it is a new unsaved Asset that has been modified ( like painting on a terrain )
        // do not add it to modified assets, since it is still new/unsaved.
        if (newAssets.contains(asset)) return

        modifiedAssets.add(asset)
    }

    fun getModifiedAssets(): ObjectSet<Asset> {
        return modifiedAssets
    }

    fun addNewAsset(asset: Asset) {
        newAssets.add(asset)
    }

    fun getNewAssets(): ObjectSet<Asset> {
        return newAssets
    }

    /**
     * Creates a new meta file and saves it at the given location.
     *
     * @param file
     *            save location
     * @param type
     *            asset type
     * @return saved meta file
     *
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createNewMetaFile(file: FileHandle, type: AssetType): Meta {
        if (file.exists()) {
            Mundus.postEvent(LogEvent(LogType.ERROR, "Tried to create new Meta File that already exists: " + file.name()))
            throw AssetAlreadyExistsException()
        }

        val meta = Meta(file)
        meta.uuid = newUUID()
        meta.version = Meta.CURRENT_VERSION
        meta.lastModified = System.currentTimeMillis()
        meta.type = type
        metaSaver.save(meta)

        return meta
    }

    private fun newUUID(): String {
        return UUID.randomUUID().toString().replace("-".toRegex(), "")
    }

    /**
     * Creates a couple of standard assets if they are not present.
     *
     * Creates a couple of standard assets in the current project, that should
     * be included in every project.
     *
     * Returns true if an asset was loaded.
     */
    fun createStandardAssets(): Boolean {
        try {

            var assetLoaded = false

            if (findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD) == null) {
                createStandardAsset(STANDARD_ASSET_TEXTURE_CHESSBOARD, "standardAssets/chessboard.png")
                assetLoaded = true
            }

            if (findAssetByID(STANDARD_ASSET_TEXTURE_DUDV) == null) {
                createStandardAsset(STANDARD_ASSET_TEXTURE_DUDV, "standardAssets/dudv.png")
                assetLoaded = true
            }

            if (findAssetByID(STANDARD_ASSET_TEXTURE_WATER_NORMAL) == null) {
                createStandardAsset(STANDARD_ASSET_TEXTURE_WATER_NORMAL, "standardAssets/waterNormal.png")
                assetLoaded = true
            }

            if (findAssetByID(STANDARD_ASSET_TEXTURE_WATER_FOAM) == null) {
                createStandardAsset(STANDARD_ASSET_TEXTURE_WATER_FOAM, "standardAssets/waterFoam.png")
                assetLoaded = true
            }

            return assetLoaded

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    private fun createStandardAsset(id: String, path: String) {
        val textureAsset = getOrCreateTextureAsset(Gdx.files.internal(path))
        assetIndex.remove(textureAsset.id)
        textureAsset.meta.uuid = id
        assetIndex[textureAsset.id] = textureAsset
        metaSaver.save(textureAsset.meta)
    }

    /**
     * Creates a new model asset.
     *
     * Creates a new model asset in the current project and adds it to this
     * asset manager.
     *
     * @param model imported model
     * @return model asset
     *
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createModelAsset(model: FileHandleWithDependencies): ModelAsset {
        val modelFilename = model.name()
        val metaFilename = modelFilename + ".meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.MODEL)

        // copy model file
        model.copyTo(FileHandle(rootFolder.path()))

        // load & return asset
        val assetFile = FileHandle(FilenameUtils.concat(rootFolder.path(), modelFilename))
        val asset = ModelAsset(meta, assetFile)
        asset.load()

        addAsset(asset)
        return asset
    }

    /**
     * Creates a new terrainAsset asset.
     *
     * This creates a .terra file (height data) and a pixmap texture (splatmap).
     * The asset will be added to this asset manager.
     *
     * @param vertexResolution
     *            vertex resolution of the terrainAsset
     * @param size
     *            terrainAsset size
     * @return new terrainAsset asset
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createTerraAsset(name: String, vertexResolution: Int, size: Int): TerrainAsset {
        val terraFilename = name + ".terra"
        val metaFilename = terraFilename + ".meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.TERRAIN)
        meta.terrain = MetaTerrain()
        meta.terrain.size = size
        meta.terrain.uv = 60f
        metaSaver.save(meta)

        // create terra file
        val terraPath = FilenameUtils.concat(rootFolder.path(), terraFilename)
        val terraFile = File(terraPath)
        FileUtils.touch(terraFile)

        // create initial height data
        val data = FloatArray(vertexResolution * vertexResolution)
        for (i in data.indices) {
            data[i] = 0f
        }

        // write terra file
        val outputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(terraFile)))
        for (f in data) {
            outputStream.writeFloat(f)
        }
        outputStream.flush()
        outputStream.close()

        // load & apply standard chessboard texture
        val asset = TerrainAsset(meta, FileHandle(terraFile))
        asset.load()

        // set base texture
        val chessboard = findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD)
        if (chessboard != null) {
            asset.splatBase = chessboard as TextureAsset
            asset.applyDependencies()
            metaSaver.save(asset.meta)
        }

        addAsset(asset)
        return asset
    }

    /**
     * Creates a new pixmap texture asset.
     *
     * This creates a new pixmap texture and adds it to this asset manager.
     *
     * @param size
     *            size of the pixmap in pixels
     * @return new pixmap asset
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createPixmapTextureAsset(size: Int): PixmapTextureAsset {
        val pixmapFilename = newUUID().substring(0, 5) + ".png"
        val metaFilename = pixmapFilename + ".meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.PIXMAP_TEXTURE)

        // create pixmap
        val pixmapPath = FilenameUtils.concat(rootFolder.path(), pixmapFilename)
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        val pixmapAssetFile = FileHandle(pixmapPath)
        PixmapIO.writePNG(pixmapAssetFile, pixmap)
        pixmap.dispose()

        // load & return asset
        val asset = PixmapTextureAsset(meta, pixmapAssetFile)
        asset.load()

        addAsset(asset)
        return asset
    }

    /**
     * Creates a new texture asset using the given texture file.
     *
     * @param texture
     * @return
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createTextureAsset(texture: FileHandle): TextureAsset {
        val meta = createMetaFileFromAsset(texture, AssetType.TEXTURE)
        val importedAssetFile = copyToAssetFolder(texture)

        val asset = TextureAsset(meta, importedAssetFile)
        // TODO parse special texture instead of always setting them
        asset.setTileable(true)
        asset.generateMipmaps(true)
        asset.load()

        addAsset(asset)
        return asset
    }

    /**
     * Creates a new texture asset if it does not exist, else
     * returns an existing one.
     *
     * @param texture
     * @return
     * @throws IOException
     */
    fun getOrCreateTextureAsset(texture: FileHandle): TextureAsset {
        val existingTexture = findAssetByFileName(texture.name())
        if (existingTexture != null)
            return existingTexture as TextureAsset

        return createTextureAsset(texture)
    }

    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createSkyBoxAsset(name: String, positiveX: String, negativeX: String, positiveY: String, negativeY: String, positiveZ: String, negativeZ: String): SkyboxAsset {
        val fileName = "$name.sky"
        val metaFilename = "$fileName.meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.SKYBOX)

        // create file
        val filePath = FilenameUtils.concat(rootFolder.path(), fileName)
        val file = File(filePath)
        FileUtils.touch(file)

        // load & apply asset
        val asset = SkyboxAsset(meta, FileHandle(file))
        asset.setIds(positiveX, negativeX,
                positiveY, negativeY, positiveZ, negativeZ)
        asset.resolveDependencies(assetMap)

        saveAsset(asset)
        addAsset(asset)
        return asset
    }

    /**
     * Creates a new & empty material asset.
     *
     * @return new material asset
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createMaterialAsset(name: String): MaterialAsset {
        // create empty material file
        val path = FilenameUtils.concat(rootFolder.path(), name) + MaterialAsset.EXTENSION
        val matFile = Gdx.files.absolute(path)
        FileUtils.touch(matFile.file())

        val meta = createMetaFileFromAsset(matFile, AssetType.MATERIAL)
        val asset = MaterialAsset(meta, matFile)
        asset.load()

        saveAsset(asset)
        addAsset(asset)
        return asset
    }

    /**
     * @param asset
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveAsset(asset: Asset) {
        if (asset is MaterialAsset) {
            saveMaterialAsset(asset)
        } else if (asset is TerrainAsset) {
            saveTerrainAsset(asset)
        } else if (asset is ModelAsset) {
            saveModelAsset(asset)
        } else if (asset is WaterAsset) {
            saveWaterAsset(asset)
        } else if (asset is SkyboxAsset) {
            saveSkyboxAsset(asset)
        }
        // TODO other assets ?
    }

    /**
     * @param asset
     */
    @Throws(IOException::class)
    fun saveModelAsset(asset: ModelAsset) {
        for (g3dbMatID in asset.defaultMaterials.keys) {
            asset.meta.model.defaultMaterials.put(g3dbMatID, asset.defaultMaterials[g3dbMatID]!!.id)
        }
        metaSaver.save(asset.meta)
    }

    /**
     * Delete the asset from the project
     */
    fun deleteAsset(asset: Asset, projectManager: ProjectManager) {
        if (asset is SkyboxAsset) {
            val skyboxUsages = findSkyboxUsagesInScenes(projectManager, asset)
            if (skyboxUsages.isNotEmpty()) {
                Dialogs.showDetailsDialog(UI, "Before deleting a skybox, remove usages of the skybox and save the scene. See details for usages.", "Asset deletion", skyboxUsages.toString())
                return
            }
        } else {
            val objectsUsingAsset = findAssetUsagesInScenes(projectManager, asset)
            val assetsUsingAsset = findAssetUsagesInAssets(asset)

            if (objectsUsingAsset.isNotEmpty() || assetsUsingAsset.isNotEmpty()) {
                showUsagesFoundDialog(objectsUsingAsset, assetsUsingAsset)
                return
            }
        }

        // continue with deletion
        assets?.removeValue(asset, true)

        if (asset.file.extension().equals(FileFormatUtils.FORMAT_3D_GLTF)) {
            // Delete the additional gltf binary file if found
            val binPath = asset.file.pathWithoutExtension() + ".bin"
            val binFile = Gdx.files.getFileHandle(binPath, Files.FileType.Absolute)
            if (binFile.exists())
                binFile.delete()
        }

        if (asset.meta.file.exists())
            asset.meta.file.delete()

        if (asset.file.exists())
            asset.file.delete()
    }

    /**
     * Deletes asset files (.terra, etc..) and meta files for assets that are new and not saved.
     */
    fun deleteNewUnsavedAssets() {
        for (asset in getNewAssets()) {
            Log.debug(TAG, "Removing new unsaved asset: {}", asset)
            asset.file.delete()
            asset.meta.file.delete()
        }
        getNewAssets().clear()
    }

    /**
     * Build a dialog displaying the usages for the asset trying to be deleted.
     */
    private fun showUsagesFoundDialog(objectsWithAssets: HashMap<GameObject, String>, assetsUsingAsset: ArrayList<Asset>) {
        val iterator = objectsWithAssets.iterator()
        var details = "Scenes using asset:"

        // Create scenes section
        while (iterator.hasNext()) {
            val next = iterator.next()

            val sceneName = next.value
            val gameObject = next.key

            var moreDetails = buildString {
                append("\nScene: ")
                append("[")
                append(sceneName)
                append("] Object name: ")
                append("[")
                append(gameObject.name)
                append("]")
            }

            if (iterator.hasNext()) {
                moreDetails += ", "
            }

            details += (moreDetails)
        }

        // add assets section
        if (assetsUsingAsset.isNotEmpty()) {
            details += "\n\nAssets using asset:"

            for (name in assetsUsingAsset)
                details += "\n" + name
        }

        Dialogs.showDetailsDialog(UI, "Before deleting an asset, remove usages of the asset and save. See details for usages.", "Asset deletion", details)
    }

    /**
     * Searches all assets in the current context for any usages of the given asset
     */
    private fun findAssetUsagesInAssets(asset: Asset): ArrayList<Asset> {
        val assetsUsingAsset = ArrayList<Asset>()

        // Check for dependent assets that are not in scenes
        for (otherAsset in assets) {
            if (asset != otherAsset && otherAsset.usesAsset(asset)) {
                assetsUsingAsset.add(otherAsset)
            }
        }

        return assetsUsingAsset
    }

    /**
     * Searches all scenes in the current context for any usages of the given asset
     */
    private fun findAssetUsagesInScenes(projectManager: ProjectManager, asset: Asset): HashMap<GameObject, String> {
        val objectsWithAssets = HashMap<GameObject, String>()

        // we check for usages in all scenes
        for (sceneName in projectManager.current().scenes) {
            val scene = projectManager.loadScene(projectManager.current(), sceneName)
            checkSceneForAssetUsage(scene, asset, objectsWithAssets)
        }

        return objectsWithAssets
    }

    private fun checkSceneForAssetUsage(scene: EditorScene?, asset: Asset, objectsWithAssets: HashMap<GameObject, String>) {
        for (gameObject in scene!!.sceneGraph.gameObjects) {
            for (component in gameObject.components) {
                if (component is AssetUsage) {
                    if (component.usesAsset(asset))
                        objectsWithAssets[gameObject] = scene.name
                }
            }
        }
    }

    private fun findSkyboxUsagesInScenes(projectManager: ProjectManager, asset: SkyboxAsset): ArrayList<String> {
        val scenesWithSkybox = ArrayList<String>()

        // we check for usages in all scenes
        for (sceneName in projectManager.current().scenes) {
            val scene = projectManager.loadScene(projectManager.current(), sceneName)
            if (scene.skyboxAssetId == asset.id) {
                scenesWithSkybox.add(scene.name)
            }
        }

        return scenesWithSkybox
    }

    /**
     * Saves an existing terrainAsset asset.
     *
     * This updates all modifiable assets and the meta file.
     *
     * @param terrain
     *             terrainAsset asset
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveTerrainAsset(terrain: TerrainAsset) {
        // save .terra file
        val outputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(terrain.file.file())))
        for (f in terrain.terrain.heightData) {
            outputStream.writeFloat(f)
        }
        outputStream.flush()
        outputStream.close()

        // save splatmap
        val splatmap = terrain.splatmap
        if (splatmap != null) {
            PixmapIO.writePNG(splatmap.file, splatmap.pixmap)

            // Encode splatmap PNG file to base64 string, used for pixmap on GWT
            val encoded = Base64.getEncoder().encodeToString(splatmap.file.readBytes())
            terrain.meta.terrain.splatBase64 = "data:image/png;base64,$encoded"
        }

        // save meta file
        metaSaver.save(terrain.meta)
    }

    @Throws(IOException::class)
    fun saveMaterialAsset(mat: MaterialAsset) {
        // save .mat
        val props = Properties()
        if (mat.diffuseColor != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_COLOR, mat.diffuseColor.toString())
        }
        if (mat.emissiveColor != null) {
            props.setProperty(MaterialAsset.PROP_MAP_EMISSIVE_COLOR, mat.emissiveColor.toString())
        }
        if (mat.diffuseTexture != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_TEXTURE, mat.diffuseTexture.id)
        }
        if (mat.normalMap != null) {
            props.setProperty(MaterialAsset.PROP_MAP_NORMAL, mat.normalMap.id)
        }
        if (mat.emissiveTexture != null) {
            props.setProperty(MaterialAsset.PROP_MAP_EMISSIVE_TEXTURE, mat.emissiveTexture.id)
        }
        if (mat.metallicRoughnessTexture != null) {
            props.setProperty(MaterialAsset.PROP_METAL_ROUGH_TEXTURE, mat.metallicRoughnessTexture.id)
        }
        if (mat.occlusionTexture != null) {
            props.setProperty(MaterialAsset.PROP_OCCLUSION_TEXTURE, mat.occlusionTexture.id)
        }
        props.setProperty(MaterialAsset.PROP_OPACITY, mat.opacity.toString())
        props.setProperty(MaterialAsset.PROP_ROUGHNESS, mat.roughness.toString())
        props.setProperty(MaterialAsset.PROP_METALLIC, mat.metallic.toString())
        props.setProperty(MaterialAsset.PROP_ALPHA_TEST, mat.alphaTest.toString())
        props.setProperty(MaterialAsset.PROP_NORMAL_SCALE, mat.normalScale.toString())
        props.setProperty(MaterialAsset.PROP_SHADOW_BIAS, mat.shadowBias.toString())
        props.setProperty(MaterialAsset.PROP_CULL_FACE, mat.cullFace.toString())

        setTexCoordInfo(props, mat.diffuseTexCoord)
        setTexCoordInfo(props, mat.normalTexCoord)
        setTexCoordInfo(props, mat.emissiveTexCoord)
        setTexCoordInfo(props, mat.metallicRoughnessTexCoord)
        setTexCoordInfo(props, mat.occlusionTexCoord)

        val fileOutputStream = FileOutputStream(mat.file.file())
        props.store(fileOutputStream, null)
        fileOutputStream.flush()
        fileOutputStream.close()

        // save meta file
        metaSaver.save(mat.meta)
    }

    private fun setTexCoordInfo(props: Properties, texCoordInfo: TexCoordInfo) {
        props.setProperty(texCoordInfo.PROP_UV, texCoordInfo.uvIndex.toString())
        props.setProperty(texCoordInfo.PROP_OFFSET_U, texCoordInfo.offsetU.toString())
        props.setProperty(texCoordInfo.PROP_OFFSET_V, texCoordInfo.offsetV.toString())
        props.setProperty(texCoordInfo.PROP_SCALE_U, texCoordInfo.scaleU.toString())
        props.setProperty(texCoordInfo.PROP_SCALE_V, texCoordInfo.scaleV.toString())
        props.setProperty(texCoordInfo.PROP_ROTATION_UV, texCoordInfo.rotationUV.toString())
    }

    private fun saveWaterAsset(asset: WaterAsset) {
        val props = Properties()

        props.setProperty(WaterAsset.PROP_SIZE, asset.water.waterWidth.toString())

        props.setProperty(WaterAsset.PROP_DUDV, asset.dudvTexture.id)
        props.setProperty(WaterAsset.PROP_NORMAL_MAP, asset.normalMapTexture.id)

        props.setProperty(WaterAsset.PROP_TILING, asset.water.getFloatAttribute(WaterFloatAttribute.Tiling).toString())
        props.setProperty(WaterAsset.PROP_WAVE_STRENGTH, asset.water.getFloatAttribute(WaterFloatAttribute.WaveStrength).toString())
        props.setProperty(WaterAsset.PROP_WAVE_SPEED, asset.water.getFloatAttribute(WaterFloatAttribute.WaveSpeed).toString())

        props.setProperty(WaterAsset.PROP_FOAM_SCALE, asset.water.getFloatAttribute(WaterFloatAttribute.FoamPatternScale).toString())
        props.setProperty(WaterAsset.PROP_FOAM_EDGE_BIAS, asset.water.getFloatAttribute(WaterFloatAttribute.FoamEdgeBias).toString())
        props.setProperty(WaterAsset.PROP_FOAM_EDGE_DISTANCE, asset.water.getFloatAttribute(WaterFloatAttribute.FoamEdgeDistance).toString())
        props.setProperty(WaterAsset.PROP_FOAM_FALL_OFF_DISTANCE, asset.water.getFloatAttribute(WaterFloatAttribute.FoamFallOffDistance).toString())
        props.setProperty(WaterAsset.PROP_FOAM_FALL_SCROLL_SPEED, asset.water.getFloatAttribute(WaterFloatAttribute.FoamScrollSpeed).toString())

        props.setProperty(WaterAsset.PROP_REFLECTIVITY, asset.water.getFloatAttribute(WaterFloatAttribute.Reflectivity).toString())
        props.setProperty(WaterAsset.PROP_SHINE_DAMPER, asset.water.getFloatAttribute(WaterFloatAttribute.ShineDamper).toString())

        val fileOutputStream = FileOutputStream(asset.file.file())
        props.store(fileOutputStream, null)
        fileOutputStream.flush()
        fileOutputStream.close()

        metaSaver.save(asset.meta)
    }

    private fun saveSkyboxAsset(asset: SkyboxAsset) {
        // save .sky
        val props = Properties()

        props.setProperty(SkyboxAsset.PROP_POSITIVE_X, asset.positiveX.id)
        props.setProperty(SkyboxAsset.PROP_NEGATIVE_X, asset.negativeX.id)

        props.setProperty(SkyboxAsset.PROP_POSITIVE_Y, asset.positiveY.id)
        props.setProperty(SkyboxAsset.PROP_NEGATIVE_Y, asset.negativeY.id)

        props.setProperty(SkyboxAsset.PROP_POSITIVE_Z, asset.positiveZ.id)
        props.setProperty(SkyboxAsset.PROP_NEGATIVE_Z, asset.negativeZ.id)

        props.setProperty(SkyboxAsset.PROP_ROTATE_ENABLED, asset.rotateEnabled.toString())
        props.setProperty(SkyboxAsset.PROP_ROTATE_SPEED, asset.rotateSpeed.toString())

        val fileOutputStream = FileOutputStream(asset.file.file())
        props.store(fileOutputStream, null)
        fileOutputStream.flush()
        fileOutputStream.close()

        // save meta file
        metaSaver.save(asset.meta)
    }

    @Throws(IOException::class, AssetAlreadyExistsException::class)
    private fun createMetaFileFromAsset(assetFile: FileHandle, type: AssetType): Meta {
        val metaName = assetFile.name() + "." + Meta.META_EXTENSION
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaName)
        return createNewMetaFile(FileHandle(metaPath), type)
    }

    private fun copyToAssetFolder(file: FileHandle): FileHandle {
        val copy = FileHandle(FilenameUtils.concat(rootFolder.path(), file.name()))
        file.copyTo(copy)
        return copy
    }

    fun createWaterAsset(name: String): WaterAsset {
        val waterFileName = "$name.water"
        val metaFilename = "$waterFileName.meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.WATER)

        // create water file
        val path = FilenameUtils.concat(rootFolder.path(), waterFileName)
        val file = File(path)
        FileUtils.touch(file)

        // if foam image is missing, create it
        if (findAssetByID(STANDARD_ASSET_TEXTURE_WATER_FOAM) == null) {
            createStandardAsset(STANDARD_ASSET_TEXTURE_WATER_FOAM, "standardAssets/waterFoam.png")
        }

        val asset = WaterAsset(meta, FileHandle(file))
        asset.load()

        // set base textures
        asset.applyDependencies()
        asset.resolveDependencies(assetMap)

        saveAsset(asset)
        addAsset(asset)
        return asset
    }

    fun getSkyboxAssets(): Array<SkyboxAsset> {
        val skyboxes = Array<SkyboxAsset>()
        for (asset in assets) {
            if (asset.meta.type == AssetType.SKYBOX)
                skyboxes.add(asset as SkyboxAsset)
        }
        return skyboxes
    }

    /**
     * Generates a txt file in the projects assets folder that lists all asset files. Overwrites existing
     * asset.txt file.
     *
     * Desktop applications cannot use .list() for internal jar files.
     * Desktop apps need to provide an assets.txt file listing all Mundus assets
     * in the Mundus assets directory. See [AssetManager.loadAssets] for how the file is used on load.
     */
    fun createAssetsTextFile() {
        // get path for assets file
        val path = FilenameUtils.concat(rootFolder.path(), "assets.txt")

        // Build the String listing all asset files
        val moreDetails = buildString {
            for (asset in assets) {
                append(asset.file.name())
                appendLine()
                append(asset.meta.file.name())
                appendLine()
            }
        }

        // Save to file
        val fileHandle = FileHandle(path)
        fileHandle.writeString(moreDetails, false)
    }

}
