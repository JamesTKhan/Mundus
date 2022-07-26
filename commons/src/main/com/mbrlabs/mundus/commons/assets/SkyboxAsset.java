package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.skybox.Skybox;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class SkyboxAsset extends Asset {

    private static final ObjectMap<String, String> MAP = new ObjectMap<>();

    // property keys
    public static final String PROP_POSITIVE_X = "positiveX";
    public static final String PROP_NEGATIVE_X = "negativeX";
    public static final String PROP_POSITIVE_Y = "positiveY";
    public static final String PROP_NEGATIVE_Y = "negativeY";
    public static final String PROP_POSITIVE_Z = "positiveZ";
    public static final String PROP_NEGATIVE_Z = "negativeZ";
    public static final String PROP_ROTATE_ENABLED = "rotateEnabled";
    public static final String PROP_ROTATE_SPEED = "rotateSpeed";

    // ids of dependent assets
    public String positiveXID;
    public String negativeXID;
    public String positiveYID;
    public String negativeYID;
    public String positiveZID;
    public String negativeZID;

    public TextureAsset positiveX;
    public TextureAsset negativeX;
    public TextureAsset positiveY;
    public TextureAsset negativeY;
    public TextureAsset positiveZ;
    public TextureAsset negativeZ;

    public boolean rotateEnabled;
    public float rotateSpeed;

    public SkyboxAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {
        MAP.clear();
        try {
            Reader reader = file.reader();
            PropertiesUtils.load(MAP, reader);
            reader.close();

            // asset dependencies, load ids
            positiveXID = MAP.get(PROP_POSITIVE_X, null);
            negativeXID = MAP.get(PROP_NEGATIVE_X, null);

            positiveYID = MAP.get(PROP_POSITIVE_Y, null);
            negativeYID = MAP.get(PROP_NEGATIVE_Y, null);

            positiveZID = MAP.get(PROP_POSITIVE_Z, null);
            negativeZID = MAP.get(PROP_NEGATIVE_Z, null);

            rotateEnabled = Boolean.parseBoolean(MAP.get(PROP_ROTATE_ENABLED, String.valueOf(Skybox.DEFAULT_ROTATE_ENABLED)));
            rotateSpeed = Float.parseFloat(MAP.get(PROP_ROTATE_SPEED, String.valueOf(Skybox.DEFAULT_ROTATE_SPEED)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(AssetManager assetManager) {
        // No async loading for skybox right now
        load();
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        if (assets.containsKey(positiveXID)) {
            positiveX = (TextureAsset) assets.get(positiveXID);
        }
        if (assets.containsKey(negativeXID)) {
            negativeX = (TextureAsset) assets.get(negativeXID);
        }
        if (assets.containsKey(positiveYID)) {
            positiveY = (TextureAsset) assets.get(positiveYID);
        }
        if (assets.containsKey(negativeYID)) {
            negativeY = (TextureAsset) assets.get(negativeYID);
        }
        if (assets.containsKey(positiveZID)) {
            positiveZ = (TextureAsset) assets.get(positiveZID);
        }
        if (assets.containsKey(negativeZID)) {
            negativeZ = (TextureAsset) assets.get(negativeZID);
        }
    }

    @Override
    public void applyDependencies() {
        // not needed
    }

    @Override
    public void dispose() {
        positiveX.dispose();
        negativeX.dispose();
        positiveY.dispose();
        negativeY.dispose();
        positiveZ.dispose();
        negativeZ.dispose();
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (assetToCheck == positiveX || assetToCheck == negativeX ||
                assetToCheck == positiveY || assetToCheck == negativeY ||
                assetToCheck == positiveZ || assetToCheck == negativeZ) {
            return true;
        }

        return false;
    }

    /**
     * Set TextureAsset Ids for the skybox. Useful for first initial creation of skybox
     */
    public void setIds(String positiveX, String negativeX, String positiveY, String negativeY, String positiveZ, String negativeZ) {
        this.positiveXID = positiveX;
        this.negativeXID = negativeX;
        this.positiveYID = positiveY;
        this.negativeYID = negativeY;
        this.positiveZID = positiveZ;
        this.negativeZID = negativeZ;
    }
}
