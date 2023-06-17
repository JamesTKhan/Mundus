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
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.terrain.SplatMap;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainLoader;
import com.mbrlabs.mundus.commons.terrain.TerrainMaterial;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public class TerrainAsset extends Asset {

    private float[] data;

    // dependencies
    private PixmapTextureAsset splatmap;
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
    private MaterialAsset materialAsset;

    private Terrain terrain;

    public TerrainAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    public float[] getData() {
        return data;
    }

    public PixmapTextureAsset getSplatmap() {
        return splatmap;
    }

    public void setSplatmap(PixmapTextureAsset splatmap) {
        this.splatmap = splatmap;
        if (splatmap == null) {
            meta.getTerrain().setSplatmap(null);
        } else {
            meta.getTerrain().setSplatmap(splatmap.getID());
        }
    }

    public void setTriplanar(boolean value) {
        meta.getTerrain().setTriplanar(value);
        terrain.getTerrainTexture().setTriplanar(value);
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

    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public void load() {
        // Load a terrain synchronously
        FileHandle terraFile;
        if (meta.getFile().type() == Files.FileType.Absolute) {
            terraFile = Gdx.files.absolute(meta.getFile().pathWithoutExtension());
        } else {
            terraFile = Gdx.files.internal(meta.getFile().pathWithoutExtension());
        }
        TerrainLoader.TerrainParameter param = new TerrainLoader.TerrainParameter(meta.getTerrain());
        TerrainLoader terrainLoader = new TerrainLoader(null);
        terrainLoader.loadAsync(null, null, terraFile, param);
        terrain = terrainLoader.loadSync(null, null, terraFile, param);
        setTriplanar(meta.getTerrain().isTriplanar());
    }

    @Override
    public void load(AssetManager assetManager) {
        terrain = assetManager.get(meta.getFile().pathWithoutExtension());
        setTriplanar(meta.getTerrain().isTriplanar());
        data = terrain.heightData;
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {

        // material
        String materialId = meta.getTerrain().getMaterialId();
        if (materialId == null || materialId.isEmpty()) {
            materialId = "terrain_default";
            meta.getTerrain().setMaterialId(materialId);
        }

        if (assets.containsKey(materialId)) {
            MaterialAsset asset = (MaterialAsset) assets.get(materialId);
            setMaterialAsset(asset);
        } else {
            Gdx.app.error("TerrainAsset", "Cannot find material asset " + materialId +
                    ". A default material can be generated by opening the project in the editor.");
        }

        // splatmap
        String id = meta.getTerrain().getSplatmap();
        if (id != null && assets.containsKey(id)) {
            setSplatmap((PixmapTextureAsset) assets.get(id));

            // If WebGL, we use base64 string for pixmap since pixmap cannot read from binaries on GWT
            if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
                ((PixmapTextureAsset) assets.get(id)).loadBase64(meta.getTerrain().getSplatBase64());
            }

        }

        // splat channel base
        id = meta.getTerrain().getSplatBase();
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
        TerrainMaterial terrainMaterial = terrain.getTerrainTexture();

        if (materialAsset != null) {
            materialAsset.applyToMaterial(terrain.getMaterial(), true);
        }

        if (splatmap == null) {
            terrainMaterial.setSplatmap(null);
        } else {
            terrainMaterial.setSplatmap(new SplatMap(splatmap));
        }
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

        terrain.update();
    }

    @Override
    public void dispose() {

    }

    public void updateUvScale(Vector2 uvScale) {
        terrain.updateUvScale(uvScale);
        terrain.update();
        meta.getTerrain().setUv(uvScale.x);
    }

    public MaterialAsset getMaterialAsset() {
        return materialAsset;
    }

    public void setMaterialAsset(MaterialAsset materialAsset) {
        this.materialAsset = materialAsset;
        meta.getTerrain().setMaterialId(materialAsset.getID());
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (assetToCheck == splatmap)
            return true;

        // does the splatmap use the asset
        if (assetToCheck instanceof TextureAsset) {
            for (Map.Entry<SplatTexture.Channel, SplatTexture> texture : terrain.getTerrainTexture().getTextures().entrySet()) {
                if (texture.getValue().texture.getFile().path().equals(assetToCheck.getFile().path())) {
                    return true;
                }
            }

            // Check normal maps
            if (assetToCheck == splatBaseNormal || assetToCheck == splatRNormal ||
                assetToCheck == splatBNormal || assetToCheck == splatGNormal || assetToCheck == splatANormal) {
                return true;
            }

        }

        return false;
    }
}
