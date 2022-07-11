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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 09-10-2016
 */
public class MaterialAsset extends Asset {

    private static final ObjectMap<String, String> MAP = new ObjectMap<>();

    public static final String EXTENSION = ".mat";

    // property keys
    public static final String PROP_DIFFUSE_COLOR = "diffuse.color";
    public static final String PROP_DIFFUSE_TEXTURE = "diffuse.texture";
    public static final String PROP_MAP_NORMAL = "map.normal";
    public static final String PROP_MAP_EMISSIVE_COLOR = "emissive.color";
    public static final String PROP_MAP_EMISSIVE_TEXTURE = "emissive.texture";
    public static final String PROP_METAL_ROUGH_TEXTURE = "metallicRoughTexture";
    public static final String PROP_OCCLUSION_TEXTURE = "occlusionTexture";
    public static final String PROP_ROUGHNESS = "roughness";
    public static final String PROP_OPACITY = "opacity";
    public static final String PROP_METALLIC = "metallic";
    public static final String PROP_ALPHA_TEST = "alphaTest";
    public static final String PROP_NORMAL_SCALE = "normalScale";
    public static final String PROP_CULL_FACE = "cullFace";

    // ids of dependent assets
    private String diffuseTextureID;
    private String normalMapID;
    private String emissiveTextureID;
    private String metallicRoughnessTextureID;
    private String occlusionTextureID;

    // Possible values are GL_FRONT_AND_BACK, GL_BACK, GL_FRONT, or -1 to inherit default
    private int cullFace = -1;

    private Color diffuseColor = Color.WHITE.cpy();
    private Color emissiveColor = Color.BLACK.cpy();
    private TextureAsset diffuseTexture;
    private TextureAsset normalMap;
    private TextureAsset emissiveTexture;
    private TextureAsset metallicRoughnessTexture;
    private TextureAsset occlusionTexture;

    private float roughness = 1f;
    private float metallic = 0f;
    private float opacity = 1f;
    private float alphaTest = 0f;
    private float normalScale = 1f;

    public MaterialAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {
        MAP.clear();
        try {
            Reader reader = file.reader();
            PropertiesUtils.load(MAP, reader);
            reader.close();

            try {
                String value = MAP.get(PROP_ROUGHNESS, null);
                if (value != null) {
                    roughness = Float.parseFloat(value);
                }
                value = MAP.get(PROP_OPACITY, null);
                if (value != null) {
                    opacity = Float.parseFloat(value);
                }
                value = MAP.get(PROP_METALLIC, null);
                if (value != null) {
                    metallic = Float.parseFloat(value);
                }
                value = MAP.get(PROP_ALPHA_TEST, null);
                if (value != null) {
                    alphaTest = Float.parseFloat(value);
                }
                value = MAP.get(PROP_NORMAL_SCALE, null);
                if (value != null) {
                    normalScale = Float.parseFloat(value);
                }
                value = MAP.get(PROP_CULL_FACE, null);
                if (value != null) {
                    cullFace = Integer.parseInt(value);
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            // diffuse color
            String diffuseHex = MAP.get(PROP_DIFFUSE_COLOR);
            if (diffuseHex != null) {
                diffuseColor = Color.valueOf(diffuseHex);
            }

            // emissive color
            String emissiveHex = MAP.get(PROP_MAP_EMISSIVE_COLOR);
            if (emissiveHex != null) {
                emissiveColor = Color.valueOf(emissiveHex);
            }

            // asset dependencies
            diffuseTextureID = MAP.get(PROP_DIFFUSE_TEXTURE, null);
            normalMapID = MAP.get(PROP_MAP_NORMAL, null);
            metallicRoughnessTextureID = MAP.get(PROP_METAL_ROUGH_TEXTURE, null);
            emissiveTextureID = MAP.get(PROP_MAP_EMISSIVE_TEXTURE, null);
            occlusionTextureID = MAP.get(PROP_OCCLUSION_TEXTURE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies this material asset to the libGDX material.
     *
     * @param material
     * @return
     */
    public Material applyToMaterial(Material material) {
        if (diffuseColor != null) {
            material.set(PBRColorAttribute.createBaseColorFactor(diffuseColor));
        }
        if (emissiveColor != null) {
            material.set(PBRColorAttribute.createEmissive(emissiveColor));
        }
        if (diffuseTexture != null) {
            material.set(new PBRTextureAttribute(PBRTextureAttribute.BaseColorTexture, diffuseTexture.getTexture()));
        } else {
            material.remove(PBRTextureAttribute.Diffuse);
        }
        if (normalMap != null) {
            material.set(new PBRTextureAttribute(PBRTextureAttribute.NormalTexture, normalMap.getTexture()));
        } else {
            material.remove(PBRTextureAttribute.NormalTexture);
        }
        if (emissiveTexture != null) {
            material.set(new PBRTextureAttribute(PBRTextureAttribute.EmissiveTexture, emissiveTexture.getTexture()));
        } else {
            material.remove(PBRTextureAttribute.EmissiveTexture);
        }
        if (metallicRoughnessTexture != null) {
            material.set(new PBRTextureAttribute(PBRTextureAttribute.MetallicRoughnessTexture, metallicRoughnessTexture.getTexture()));
        } else {
            material.remove(PBRTextureAttribute.MetallicRoughnessTexture);
        }
        if (occlusionTexture != null) {
            material.set(new PBRTextureAttribute(PBRTextureAttribute.OcclusionTexture, occlusionTexture.getTexture()));
        } else {
            material.remove(PBRTextureAttribute.OcclusionTexture);
        }

        material.set(PBRFloatAttribute.createRoughness(roughness));
        material.set(PBRFloatAttribute.createMetallic(metallic));
        material.set(PBRFloatAttribute.createNormalScale(normalScale));

        if (cullFace != -1) {
            material.set(IntAttribute.createCullFace(cullFace));
        } else {
            material.remove(IntAttribute.CullFace);
        }

        if (opacity < 1f) {
            material.set(new BlendingAttribute(true, opacity));
        } else {
            if (alphaTest == 0) {
                material.remove(BlendingAttribute.Type);
            }
        }

        if (alphaTest > 0) {
            material.set(PBRFloatAttribute.createAlphaTest(alphaTest));
            // We need blending attribute to trip the blendedFlag in shader
            material.set(new BlendingAttribute(false, opacity));
        } else {
            material.remove(PBRFloatAttribute.AlphaTest);
            if (opacity == 1f) {
                material.remove(BlendingAttribute.Type);
            }
        }

        return material;
    }

    public float getRoughness() {
        return roughness;
    }

    public void setRoughness(float roughness) {
        this.roughness = roughness;
    }

    public float getMetallic() {
        return metallic;
    }

    public void setMetallic(float metallic) {
        this.metallic = metallic;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getAlphaTest() {
        return alphaTest;
    }

    public void setAlphaTest(float alphaTest) {
        this.alphaTest = alphaTest;
    }

    public float getNormalScale() {
        return normalScale;
    }

    public void setNormalScale(float normalScale) {
        this.normalScale = normalScale;
    }

    public TextureAsset getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(TextureAsset normalMap) {
        this.normalMap = normalMap;
        if (normalMap != null) {
            this.normalMapID = normalMap.getID();
        } else {
            this.normalMapID = null;
        }
    }

    public TextureAsset getDiffuseTexture() {
        return diffuseTexture;
    }

    public void setDiffuseTexture(TextureAsset diffuseTexture) {
        this.diffuseTexture = diffuseTexture;
        if (diffuseTexture != null) {
            this.diffuseTextureID = diffuseTexture.getID();
        } else {
            this.diffuseTextureID = null;
        }
    }

    public TextureAsset getEmissiveTexture() {
        return emissiveTexture;
    }

    public void setEmissiveTexture(TextureAsset emissiveTexture) {
        this.emissiveTexture = emissiveTexture;
        if (emissiveTexture != null) {
            this.emissiveTextureID = emissiveTexture.getID();
        } else {
            this.emissiveTextureID = null;
        }
    }

    public Color getEmissiveColor() {
        return emissiveColor;
    }

    public TextureAsset getMetallicRoughnessTexture() {
        return metallicRoughnessTexture;
    }

    public void setMetallicRoughnessTexture(TextureAsset metallicRoughnessTexture) {
        this.metallicRoughnessTexture = metallicRoughnessTexture;
        if (metallicRoughnessTexture != null) {
            this.metallicRoughnessTextureID = metallicRoughnessTexture.getID();
        } else {
            this.metallicRoughnessTextureID = null;
        }
    }

    public TextureAsset getOcclusionTexture() {
        return occlusionTexture;
    }

    public void setOcclusionTexture(TextureAsset occlusionTexture) {
        this.occlusionTexture = occlusionTexture;
        if (occlusionTexture != null) {
            this.occlusionTextureID = occlusionTexture.getID();
        } else {
            this.occlusionTextureID = null;
        }
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    public int getCullFace() {
        return cullFace;
    }

    public void setCullFace(int cullFace) {
        this.cullFace = cullFace;
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        if (diffuseTextureID != null && assets.containsKey(diffuseTextureID)) {
            diffuseTexture = (TextureAsset) assets.get(diffuseTextureID);
        }
        if (normalMapID != null && assets.containsKey(normalMapID)) {
            normalMap = (TextureAsset) assets.get(normalMapID);
        }
        if (emissiveTextureID != null && assets.containsKey(emissiveTextureID)) {
            emissiveTexture = (TextureAsset) assets.get(emissiveTextureID);
        }
        if (metallicRoughnessTextureID != null && assets.containsKey(metallicRoughnessTextureID)) {
            metallicRoughnessTexture = (TextureAsset) assets.get(metallicRoughnessTextureID);
        }
        if (occlusionTextureID != null && assets.containsKey(occlusionTextureID)) {
            occlusionTexture = (TextureAsset) assets.get(occlusionTextureID);
        }
    }

    @Override
    public void applyDependencies() {
        // nothing to apply
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (assetToCheck instanceof TextureAsset) {
            if (fileMatch(diffuseTexture, assetToCheck)) return true;
            if (fileMatch(normalMap, assetToCheck)) return true;
            if (fileMatch(emissiveTexture, assetToCheck)) return true;
            if (fileMatch(metallicRoughnessTexture, assetToCheck)) return true;
            return fileMatch(occlusionTexture, assetToCheck);
        }
        return false;
    }

    private boolean fileMatch(Asset childAsset, Asset assetToCheck) {
        return childAsset != null && childAsset.getFile().path().equals(assetToCheck.getFile().path());
    }
}
