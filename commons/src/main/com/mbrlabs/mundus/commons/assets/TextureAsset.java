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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.utils.TextureProvider;
import com.mbrlabs.mundus.commons.utils.TextureUtils;

import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public class TextureAsset extends Asset implements TextureProvider {

    private Texture texture;
    private boolean generateMipMaps;
    private boolean tileable;

    public TextureAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    public TextureAsset generateMipmaps(boolean mipmaps) {
        this.generateMipMaps = mipmaps;
        return this;
    }

    public TextureAsset setTileable(boolean tileable) {
        this.tileable = tileable;
        return this;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public void load() {
        if (generateMipMaps) {
            texture = TextureUtils.loadMipmapTexture(file, false);
        } else {
            texture = new Texture(file);
        }

        if (tileable) {
            texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }
    }

    @Override
    public void load(AssetManager assetManager) {
        texture = assetManager.get(meta.getFile().pathWithoutExtension());

        if (tileable) {
            texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        // no dependencies here
    }

    @Override
    public void applyDependencies() {
        // no dependencies here
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        return false;
    }
}
