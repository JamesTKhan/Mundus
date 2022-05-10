package com.mbrlabs.mundus.commons.assets.meta;

public class MetaSkybox {
    public static final String JSON_POSITIVE_X = "positiveX";
    public static final String JSON_NEGATIVE_X = "negativeX";
    public static final String JSON_POSITIVE_Y = "positiveY";
    public static final String JSON_NEGATIVE_Y = "negativeY";
    public static final String JSON_POSITIVE_Z = "positiveZ";
    public static final String JSON_NEGATIVE_Z = "negativeZ";

    public String positiveX;
    public String negativeX;
    public String positiveY;
    public String negativeY;
    public String positiveZ;
    public String negativeZ;

    public MetaSkybox() {
    }

    public MetaSkybox(String positiveX, String negativeX, String positiveY, String negativeY, String positiveZ, String negativeZ) {
        this.positiveX = positiveX;
        this.negativeX = negativeX;
        this.positiveY = positiveY;
        this.negativeY = negativeY;
        this.positiveZ = positiveZ;
        this.negativeZ = negativeZ;
    }

    public String getPositiveX() {
        return positiveX;
    }

    public void setPositiveX(String positiveX) {
        this.positiveX = positiveX;
    }

    public String getNegativeX() {
        return negativeX;
    }

    public void setNegativeX(String negativeX) {
        this.negativeX = negativeX;
    }

    public String getPositiveY() {
        return positiveY;
    }

    public void setPositiveY(String positiveY) {
        this.positiveY = positiveY;
    }

    public String getNegativeY() {
        return negativeY;
    }

    public void setNegativeY(String negativeY) {
        this.negativeY = negativeY;
    }

    public String getPositiveZ() {
        return positiveZ;
    }

    public void setPositiveZ(String positiveZ) {
        this.positiveZ = positiveZ;
    }

    public String getNegativeZ() {
        return negativeZ;
    }

    public void setNegativeZ(String negativeZ) {
        this.negativeZ = negativeZ;
    }

    @Override
    public String toString() {
        return "MetaSkybox{" +
                "positiveX='" + positiveX + '\'' +
                ", negativeX='" + negativeX + '\'' +
                ", positiveY='" + positiveY + '\'' +
                ", negativeY='" + negativeY + '\'' +
                ", positiveZ='" + positiveZ + '\'' +
                ", negativeZ='" + negativeZ + '\'' +
                '}';
    }
}
