package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mbrlabs.mundus.commons.assets.meta.MetaTerrain;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Loads Mundus Terrain objects via meta and .terra file data.
 * @author JamesTKhan
 * @version July 24, 2022
 */
public class TerrainLoader extends AsynchronousAssetLoader<Terrain, TerrainLoader.TerrainParameter> {

    private Terrain terrain = null;

    public TerrainLoader() {
        this(new InternalFileHandleResolver());
    }

    public TerrainLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
        if (parameter == null) {
            throw new GdxRuntimeException("TerrainLoader requires parameters");
        }

        if (parameter.metaTerrain == null) {
            throw new GdxRuntimeException("MetaTerrain is required for TerrainLoader");
        }
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {
        terrain = null;

        final FloatArray floatArray = new FloatArray();

        // load height data from terra file
        DataInputStream is;
        try {
            is = new DataInputStream(new BufferedInputStream(file.read()));
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
            is.close();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GdxRuntimeException("Error reading terra file: " + file.name());
        }
        terrain = new Terrain(parameter.metaTerrain.getSize(), floatArray.toArray());
        terrain.updateUvScale(new Vector2(parameter.metaTerrain.getUv(), parameter.metaTerrain.getUv()));
    }

    @Override
    public Terrain loadSync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {
        terrain.init();
        terrain.update();

        Terrain terrain = this.terrain;
        this.terrain = null;
        return terrain;
    }

    static public class TerrainParameter extends AssetLoaderParameters<Terrain> {
        public TerrainParameter(MetaTerrain metaTerrain) {
            this.metaTerrain = metaTerrain;
        }

        /** Required to create the terrain properly **/
        public MetaTerrain metaTerrain = null;
    }
}
