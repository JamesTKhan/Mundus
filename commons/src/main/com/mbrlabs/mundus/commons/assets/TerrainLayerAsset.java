package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainMaterial;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

import java.util.Map;

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

    private Terrain terrain;


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
        String id = meta.getTerrain().getSplatBase();
        if (id != null && assets.containsKey(id)) {
            setSplatBase((TextureAsset) assets.get(id));
        }

        // splat channel r
        id = meta.getTerrain().getSplatR();
        if (id != null && assets.containsKey(id)) {
            setSplatR((TextureAsset) assets.get(id));
        }

        // splat channel g
        id = meta.getTerrain().getSplatG();
        if (id != null && assets.containsKey(id)) {
            setSplatG((TextureAsset) assets.get(id));
        }

        // splat channel b
        id = meta.getTerrain().getSplatB();
        if (id != null && assets.containsKey(id)) {
            setSplatB((TextureAsset) assets.get(id));
        }

        // splat channel a
        id = meta.getTerrain().getSplatA();
        if (id != null && assets.containsKey(id)) {
            setSplatA((TextureAsset) assets.get(id));
        }

        // splat normal channel base
        id = meta.getTerrain().getSplatBaseNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatBaseNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel r
        id = meta.getTerrain().getSplatRNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatRNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel g
        id = meta.getTerrain().getSplatGNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatGNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel b
        id = meta.getTerrain().getSplatBNormal();
        if (id != null && assets.containsKey(id)) {
            setSplatBNormal((TextureAsset) assets.get(id));
        }

        // splat normal channel a
        id = meta.getTerrain().getSplatANormal();
        if (id != null && assets.containsKey(id)) {
            setSplatANormal((TextureAsset) assets.get(id));
        }
    }

    @Override
    public void applyDependencies() {
        final TerrainMaterial terrainMaterial = terrain.getTerrainTexture();

        if (splatBase == null) {
            terrainMaterial.removeTexture(SplatTexture.Channel.BASE);
        } else {
            terrainMaterial.setSplatTexture(new SplatTexture(SplatTexture.Channel.BASE, splatBase));
        }
        if (splatR == null) {
            terrainMaterial.removeTexture(SplatTexture.Channel.R);
        } else {
            terrainMaterial.setSplatTexture(new SplatTexture(SplatTexture.Channel.R, splatR));
        }
        if (splatG == null) {
            terrainMaterial.removeTexture(SplatTexture.Channel.G);
        } else {
            terrainMaterial.setSplatTexture(new SplatTexture(SplatTexture.Channel.G, splatG));
        }
        if (splatB == null) {
            terrainMaterial.removeTexture(SplatTexture.Channel.B);
        } else {
            terrainMaterial.setSplatTexture(new SplatTexture(SplatTexture.Channel.B, splatB));
        }
        if (splatA == null) {
            terrainMaterial.removeTexture(SplatTexture.Channel.A);
        } else {
            terrainMaterial.setSplatTexture(new SplatTexture(SplatTexture.Channel.A, splatA));
        }

        if (splatBaseNormal == null) {
            terrainMaterial.removeNormalTexture(SplatTexture.Channel.BASE);
        } else {
            terrainMaterial.setSplatNormalTexture(new SplatTexture(SplatTexture.Channel.BASE, splatBaseNormal));
        }
        if (splatRNormal == null) {
            terrainMaterial.removeNormalTexture(SplatTexture.Channel.R);
        } else {
            terrainMaterial.setSplatNormalTexture(new SplatTexture(SplatTexture.Channel.R, splatRNormal));
        }
        if (splatGNormal == null) {
            terrainMaterial.removeNormalTexture(SplatTexture.Channel.G);
        } else {
            terrainMaterial.setSplatNormalTexture(new SplatTexture(SplatTexture.Channel.G, splatGNormal));
        }
        if (splatBNormal == null) {
            terrainMaterial.removeNormalTexture(SplatTexture.Channel.B);
        } else {
            terrainMaterial.setSplatNormalTexture(new SplatTexture(SplatTexture.Channel.B, splatBNormal));
        }
        if (splatANormal == null) {
            terrainMaterial.removeNormalTexture(SplatTexture.Channel.A);
        } else {
            terrainMaterial.setSplatNormalTexture(new SplatTexture(SplatTexture.Channel.A, splatANormal));
        }
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
            meta.getTerrain().setSplatBase(null);
        } else {
            terrain.getMaterial().set(PBRTextureAttribute.createBaseColorTexture(splatBase.getTexture()));
            meta.getTerrain().setSplatBase(splatBase.getID());
        }
    }

    public TextureAsset getSplatR() {
        return splatR;
    }

    public void setSplatR(TextureAsset splatR) {
        this.splatR = splatR;
        if (splatR == null) {
            getMeta().getTerrain().setSplatR(null);
        } else {
            meta.getTerrain().setSplatR(splatR.getID());

        }
    }

    public TextureAsset getSplatG() {
        return splatG;
    }

    public void setSplatG(TextureAsset splatG) {
        this.splatG = splatG;
        if (splatG == null) {
            meta.getTerrain().setSplatG(null);
        } else {
            meta.getTerrain().setSplatG(splatG.getID());
        }
    }

    public TextureAsset getSplatB() {
        return splatB;
    }

    public void setSplatB(TextureAsset splatB) {
        this.splatB = splatB;
        if (splatB == null) {
            meta.getTerrain().setSplatB(null);
        } else {
            meta.getTerrain().setSplatB(splatB.getID());
        }
    }

    public TextureAsset getSplatA() {
        return splatA;
    }

    public void setSplatA(TextureAsset splatA) {
        this.splatA = splatA;
        if (splatA == null) {
            meta.getTerrain().setSplatA(null);
        } else {
            meta.getTerrain().setSplatA(splatA.getID());
        }
    }

    public TextureAsset getSplatBaseNormal() {
        return splatBaseNormal;
    }

    public void setSplatBaseNormal(TextureAsset splatBaseNormal) {
        this.splatBaseNormal = splatBaseNormal;
        if (splatBaseNormal == null) {
            meta.getTerrain().setSplatBaseNormal(null);
        } else {
            terrain.getMaterial().set(PBRTextureAttribute.createNormalTexture(splatBaseNormal.getTexture()));
            meta.getTerrain().setSplatBaseNormal(splatBaseNormal.getID());
        }
    }

    public TextureAsset getSplatRNormal() {
        return splatRNormal;
    }

    public void setSplatRNormal(TextureAsset splatRNormal) {
        this.splatRNormal = splatRNormal;
        if (splatRNormal == null) {
            meta.getTerrain().setSplatRNormal(null);
        } else {
            meta.getTerrain().setSplatRNormal(splatRNormal.getID());
        }
    }

    public TextureAsset getSplatBNormal() {
        return splatBNormal;
    }

    public void setSplatBNormal(TextureAsset splatBNormal) {
        this.splatBNormal = splatBNormal;
        if (splatBNormal == null) {
            meta.getTerrain().setSplatBNormal(null);
        } else {
            meta.getTerrain().setSplatBNormal(splatBNormal.getID());
        }
    }

    public TextureAsset getSplatGNormal() {
        return splatGNormal;
    }

    public void setSplatGNormal(TextureAsset splatGNormal) {
        this.splatGNormal = splatGNormal;
        if (splatGNormal == null) {
            meta.getTerrain().setSplatGNormal(null);
        } else {
            meta.getTerrain().setSplatGNormal(splatGNormal.getID());
        }
    }

    public TextureAsset getSplatANormal() {
        return splatANormal;
    }

    public void setSplatANormal(TextureAsset splatANormal) {
        this.splatANormal = splatANormal;
        if (splatANormal == null) {
            meta.getTerrain().setSplatANormal(null);
        } else {
            meta.getTerrain().setSplatANormal(splatANormal.getID());
        }
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
