package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.water.Water;
import com.mbrlabs.mundus.commons.water.attributes.WaterColorAttribute;
import com.mbrlabs.mundus.commons.water.attributes.WaterFloatAttribute;
import com.mbrlabs.mundus.commons.water.attributes.WaterIntAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class WaterAsset extends Asset {

    private static final ObjectMap<String, String> MAP = new ObjectMap<>();

    // property keys
    public static final String PROP_SIZE = "size";
    public static final String PROP_DUDV = "dudv";
    public static final String PROP_NORMAL_MAP = "normalMap";
    public static final String PROP_TILING = "tiling";
    public static final String PROP_WAVE_STRENGTH = "waveStrength";
    public static final String PROP_WAVE_SPEED = "waveSpeed";
    public static final String PROP_FOAM_SCALE = "foamScale";
    public static final String PROP_FOAM_EDGE_BIAS = "foamEdgeBias";
    public static final String PROP_FOAM_EDGE_DISTANCE = "foamEdgeDistance";
    public static final String PROP_FOAM_FALL_OFF_DISTANCE = "foamFallOffDistance";
    public static final String PROP_FOAM_FALL_SCROLL_SPEED = "foamScrollSpeed";
    public static final String PROP_REFLECTIVITY = "reflectivity";
    public static final String PROP_SHINE_DAMPER = "shineDamper";
    public static final String PROP_MAX_VIS_DEPTH = "maxVisibleDepth";
    public static final String PROP_CULL_FACE = "cullFace";
    public static final String PROP_COLOR = "color";

    // ids of dependent assets
    public String dudvID;
    public String normaMapID;

    public Water water;
    public TextureAsset waterTexture;
    public TextureAsset waterFoamTexture;
    public TextureAsset dudvTexture;
    public TextureAsset normalMapTexture;

    public WaterAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
    }

    @Override
    public void load() {
        MAP.clear();
        try {
            Reader reader = file.reader();
            PropertiesUtils.load(MAP, reader);
            reader.close();

            water = new Water(Integer.parseInt(MAP.get(PROP_SIZE, String.valueOf(Water.DEFAULT_SIZE))));
            water.init();

            // asset dependencies, load ids
            dudvID = MAP.get(PROP_DUDV, "dudv");
            normaMapID = MAP.get(PROP_NORMAL_MAP, "waterNormal");

            // float attributes
            water.setFloatAttribute(WaterFloatAttribute.Tiling, Float.parseFloat(MAP.get(PROP_TILING, String.valueOf(Water.DEFAULT_TILING))));
            water.setFloatAttribute(WaterFloatAttribute.WaveStrength, Float.parseFloat(MAP.get(PROP_WAVE_STRENGTH, String.valueOf(Water.DEFAULT_WAVE_STRENGTH))));
            water.setFloatAttribute(WaterFloatAttribute.WaveSpeed, Float.parseFloat(MAP.get(PROP_WAVE_SPEED, String.valueOf(Water.DEFAULT_WAVE_SPEED))));
            water.setFloatAttribute(WaterFloatAttribute.FoamPatternScale, Float.parseFloat(MAP.get(PROP_FOAM_SCALE, String.valueOf(Water.DEFAULT_FOAM_SCALE))));
            water.setFloatAttribute(WaterFloatAttribute.FoamEdgeBias, Float.parseFloat(MAP.get(PROP_FOAM_EDGE_BIAS, String.valueOf(Water.DEFAULT_FOAM_EDGE_BIAS))));
            water.setFloatAttribute(WaterFloatAttribute.FoamEdgeDistance, Float.parseFloat(MAP.get(PROP_FOAM_EDGE_DISTANCE, String.valueOf(Water.DEFAULT_FOAM_EDGE_DISTANCE))));
            water.setFloatAttribute(WaterFloatAttribute.FoamFallOffDistance, Float.parseFloat(MAP.get(PROP_FOAM_FALL_OFF_DISTANCE, String.valueOf(Water.DEFAULT_FOAM_FALL_OFF_DISTANCE))));
            water.setFloatAttribute(WaterFloatAttribute.FoamScrollSpeed, Float.parseFloat(MAP.get(PROP_FOAM_FALL_SCROLL_SPEED, String.valueOf(Water.DEFAULT_FOAM_SCROLL_SPEED))));
            water.setFloatAttribute(WaterFloatAttribute.Reflectivity, Float.parseFloat(MAP.get(PROP_REFLECTIVITY, String.valueOf(Water.DEFAULT_REFLECTIVITY))));
            water.setFloatAttribute(WaterFloatAttribute.ShineDamper, Float.parseFloat(MAP.get(PROP_SHINE_DAMPER, String.valueOf(Water.DEFAULT_SHINE_DAMPER))));
            water.setFloatAttribute(WaterFloatAttribute.MaxVisibleDepth, Float.parseFloat(MAP.get(PROP_MAX_VIS_DEPTH, String.valueOf(Water.DEFAULT_MAX_VISIBLE_DEPTH))));

            water.setIntAttribute(WaterIntAttribute.CullFace, Integer.parseInt(MAP.get(PROP_CULL_FACE, String.valueOf(Water.DEFAULT_CULL_FACE))));

            String colorValue = MAP.get(PROP_COLOR);
            if (colorValue != null) {
                water.setColorAttribute(WaterColorAttribute.Diffuse, Color.valueOf(colorValue));
            } else {
                water.setColorAttribute(WaterColorAttribute.Diffuse, Water.DEFAULT_COLOR);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void load(AssetManager assetManager) {
        // No async loading for water right now
        load();
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        if (assets.containsKey(dudvID)) {
            dudvTexture = (TextureAsset) assets.get(dudvID);
        }

        if (assets.containsKey(normaMapID)) {
            normalMapTexture = (TextureAsset) assets.get(normaMapID);
        }

        if (assets.containsKey("waterFoam")) {
            waterFoamTexture = (TextureAsset) assets.get("waterFoam");
        }
    }

    @Override
    public void applyDependencies() {
        if (waterTexture != null) {
            water.setWaterReflection(waterTexture.getTexture());
        }

        if (waterFoamTexture != null) {
            waterFoamTexture.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            water.setFoamTexture(waterFoamTexture.getTexture());
        }

        if (dudvTexture != null) {
            water.setDudvTexture(dudvTexture.getTexture());
        }

        if (normalMapTexture != null) {
            water.setNormalMap(normalMapTexture.getTexture());
        }
    }

    public void setWaterReflectionTexture(Texture texture){
       water.setWaterReflection(texture);
    }

    public void setWaterRefractionTexture(Texture texture){
        water.setWaterRefractionTexture(texture);
    }

    public void setWaterRefractionDepthTexture(Texture texture){
        water.setWaterRefractionDepthTexture(texture);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        return assetToCheck == dudvTexture || assetToCheck == normalMapTexture || assetToCheck == waterFoamTexture;
    }

}
