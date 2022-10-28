package com.mbrlabs.mundus.commons.water.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.mbrlabs.mundus.commons.water.WaterMaterial;

/**
 * A standard libGDX attribute to hold our WaterMaterial
 *
 * @author JamesTKhan
 * @version August 15, 2022
 */
public class WaterMaterialAttribute extends Attribute {
    public final static String WaterMaterialAlias = "waterData";
    public final static long WaterMaterial = register(WaterMaterialAlias);

    protected static long Mask = WaterMaterial;

    public WaterMaterial waterMaterial;

    /**
     * Method to check whether the specified type is a valid DoubleAttribute
     * type
     */
    public static Boolean is(final long type) {
        return (type & Mask) != 0;
    }

    protected WaterMaterialAttribute(long type) {
        super(type);
    }

    public WaterMaterialAttribute(long type, WaterMaterial waterMaterial) {
        super(type);
        this.waterMaterial = waterMaterial;
    }

    public static WaterMaterialAttribute createWaterMaterialAttribute(WaterMaterial waterMaterial) {
        return new WaterMaterialAttribute(WaterMaterial, waterMaterial);
    }
    @Override
    public Attribute copy() {
        return new WaterMaterialAttribute(this.type);
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        WaterMaterialAttribute otherValue = ((WaterMaterialAttribute) o);
        return waterMaterial.equals(otherValue.waterMaterial) ? 0 : -1;
    }
}
