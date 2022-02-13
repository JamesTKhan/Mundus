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
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

import java.io.IOException;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 09-10-2016
 */
public class MaterialAsset extends Asset {

    private static final ObjectMap<String, String> MAP = new ObjectMap<String, String>();

    public static final String EXTENSION = ".mat";

    // property keys
    public static final String PROP_DIFFUSE_COLOR = "diffuse.color";
    public static final String PROP_DIFFUSE_TEXTURE = "diffuse.texture";
    public static final String PROP_MAP_NORMAL = "map.normal";
    public static final String PROP_SHININESS = "shininess";
    public static final String PROP_OPACITY = "opacity";

    // ids of dependent assets
    private String diffuseTextureID;
    private String normalMapID;

    private Color diffuseColor = Color.WHITE.cpy();
    private TextureAsset diffuseTexture;
    private TextureAsset normalMap;
    private float shininess = 0f;
    private float opacity = 0f;

    public MaterialAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {
        MAP.clear();
        try {
            PropertiesUtils.load(MAP, file.reader());
            // shininess & opacity
            try {
                String value = MAP.get(PROP_SHININESS, null);
                if (value != null) {
                    shininess = Float.valueOf(value);
                }
                value = MAP.get(PROP_OPACITY, null);
                if (value != null) {
                    opacity = Float.valueOf(value);
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            // diffuse color
            String diffuseHex = MAP.get(PROP_DIFFUSE_COLOR);
            if (diffuseHex != null) {
                diffuseColor = Color.valueOf(diffuseHex);
            }

            // asset dependencies
            diffuseTextureID = MAP.get(PROP_DIFFUSE_TEXTURE, null);
            normalMapID = MAP.get(PROP_MAP_NORMAL, null);
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
            material.set(new PBRColorAttribute(PBRColorAttribute.Diffuse, diffuseColor));
        }
        if (diffuseTexture != null) {
            material.set(new PBRTextureAttribute(PBRTextureAttribute.Diffuse, diffuseTexture.getTexture()));
        } else {
            material.remove(PBRTextureAttribute.Diffuse);
        }
        if (normalMap != null) {
            material.set(new PBRTextureAttribute(PBRTextureAttribute.Normal, normalMap.getTexture()));
        } else {
            material.remove(PBRTextureAttribute.Normal);
        }

        material.set(new PBRFloatAttribute(PBRFloatAttribute.Metallic, shininess));
        material.set(new PBRFloatAttribute(PBRFloatAttribute.Roughness, shininess));

        return material;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
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

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        if (diffuseTextureID != null && assets.containsKey(diffuseTextureID)) {
            diffuseTexture = (TextureAsset) assets.get(diffuseTextureID);
        }
        if (normalMapID != null && assets.containsKey(normalMapID)) {
            normalMap = (TextureAsset) assets.get(normalMapID);
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

}
