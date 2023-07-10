package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.meta.Meta;

import java.util.Map;

/**
 * Holds a reference to Textures for use by TerrainAssets
 */
public class TerrainLayerAsset extends Asset {

    private TextureAsset splatBase;
    private TextureAsset splatR;
    private TextureAsset splatG;
    private TextureAsset splatB;
    private TextureAsset splatA;
    private TextureAsset splatBaseNormal;
    private TextureAsset splatRNormal;
    private TextureAsset splatBNormal;
    private TextureAsset splatGNormal;
    private TextureAsset splatANormal;

    /**
     * @param meta
     * @param assetFile
     */
    public TerrainLayerAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {

    }

    @Override
    public void load(AssetManager assetManager) {

    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        // splat channel base
        String id = meta.getTerrainLayer().getSplatBase();
        if (id != null && assets.containsKey(id)) {
            setSplatBase((TextureAsset) assets.get(id));
        }

        // splat channel r
        id = meta.getTerrainLayer().getSplatR();
        if (id != null && assets.containsKey(id)) {
            setSplatR((TextureAsset) assets.get(id));
        }

        // splat channel g
        id = meta.getTerrainLayer().getSplatG();
        if (id != null && assets.containsKey(id)) {
            setSplatG((TextureAsset) assets.get(id));
        }

        // splat channel b
        id = meta.getTerrainLayer().getSplatB();
        if (id != null && assets.containsKey(id)) {
            setSplatB((TextureAsset) assets.get(id));
        }

        // splat channel a
        id = meta.getTerrainLayer().getSplatA();
        if (id != null && assets.containsKey(id)) {
            setSplatA((TextureAsset) assets.get(id));
        }

        // splat normal channel base
        id = meta.getTerrainLayer().getSplatBaseNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatBaseNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel r
        id = meta.getTerrainLayer().getSplatRNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatRNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel g
        id = meta.getTerrainLayer().getSplatGNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatGNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel b
        id = meta.getTerrainLayer().getSplatBNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatBNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel a
        id = meta.getTerrainLayer().getSplatANormal();
        if (id != null && assets.containsKey(id)) {
            setSplatANormal((TextureAsset) assets.get(id));
        }
    }

    @Override
    public void applyDependencies() {
        // Nothing to do here
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        // Check normal maps
        if (assetToCheck == splatBaseNormal || assetToCheck == splatRNormal ||
                assetToCheck == splatBNormal || assetToCheck == splatGNormal || assetToCheck == splatANormal) {
            return true;
        }

        return false;
    }

    public TextureAsset getSplatBase() {
        return splatBase;
    }

    public void setSplatBase(TextureAsset splatBase) {
        this.splatBase = splatBase;
        if (splatBase == null) {
            meta.getTerrainLayer().setSplatBase(null);
        } else {
            meta.getTerrainLayer().setSplatBase(splatBase.getID());
        }
    }

    public TextureAsset getSplatR() {
        return splatR;
    }

    public void setSplatR(TextureAsset splatR) {
        this.splatR = splatR;
        if (splatR == null) {
            getMeta().getTerrainLayer().setSplatR(null);
        } else {
            meta.getTerrainLayer().setSplatR(splatR.getID());

        }
    }

    public TextureAsset getSplatG() {
        return splatG;
    }

    public void setSplatG(TextureAsset splatG) {
        this.splatG = splatG;
        if (splatG == null) {
            meta.getTerrainLayer().setSplatG(null);
        } else {
            meta.getTerrainLayer().setSplatG(splatG.getID());
        }
    }

    public TextureAsset getSplatB() {
        return splatB;
    }

    public void setSplatB(TextureAsset splatB) {
        this.splatB = splatB;
        if (splatB == null) {
            meta.getTerrainLayer().setSplatB(null);
        } else {
            meta.getTerrainLayer().setSplatB(splatB.getID());
        }
    }

    public TextureAsset getSplatA() {
        return splatA;
    }

    public void setSplatA(TextureAsset splatA) {
        this.splatA = splatA;
        if (splatA == null) {
            meta.getTerrainLayer().setSplatA(null);
        } else {
            meta.getTerrainLayer().setSplatA(splatA.getID());
        }
    }

    public TextureAsset getSplatBaseNormal() {
        return splatBaseNormal;
    }

    public void setSplatBaseNormal(TextureAsset splatBaseNormal) {
        this.splatBaseNormal = splatBaseNormal;
        if (splatBaseNormal == null) {
            meta.getTerrainLayer().setSplatBaseNormal(null);
        } else {
            meta.getTerrainLayer().setSplatBaseNormal(splatBaseNormal.getID());
        }
    }

    public TextureAsset getSplatRNormal() {
        return splatRNormal;
    }

    public void setSplatRNormal(TextureAsset splatRNormal) {
        this.splatRNormal = splatRNormal;
        if (splatRNormal == null) {
            meta.getTerrainLayer().setSplatRNormal(null);
        } else {
            meta.getTerrainLayer().setSplatRNormal(splatRNormal.getID());
        }
    }

    public TextureAsset getSplatBNormal() {
        return splatBNormal;
    }

    public void setSplatBNormal(TextureAsset splatBNormal) {
        this.splatBNormal = splatBNormal;
        if (splatBNormal == null) {
            meta.getTerrainLayer().setSplatBNormal(null);
        } else {
            meta.getTerrainLayer().setSplatBNormal(splatBNormal.getID());
        }
    }

    public TextureAsset getSplatGNormal() {
        return splatGNormal;
    }

    public void setSplatGNormal(TextureAsset splatGNormal) {
        this.splatGNormal = splatGNormal;
        if (splatGNormal == null) {
            meta.getTerrainLayer().setSplatGNormal(null);
        } else {
            meta.getTerrainLayer().setSplatGNormal(splatGNormal.getID());
        }
    }

    public TextureAsset getSplatANormal() {
        return splatANormal;
    }

    public void setSplatANormal(TextureAsset splatANormal) {
        this.splatANormal = splatANormal;
        if (splatANormal == null) {
            meta.getTerrainLayer().setSplatANormal(null);
        } else {
            meta.getTerrainLayer().setSplatANormal(splatANormal.getID());
        }
    }

}
