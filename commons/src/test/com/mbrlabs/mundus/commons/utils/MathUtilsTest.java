package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathUtilsTest {
    @Test
    public void barryCentric() {
        Vector3 a = new Vector3(1.0f, 0.0f, 0.0f);
        Vector3 b = new Vector3(0.0f, 1.0f, 0.0f);
        Vector3 c = new Vector3(0.0f, 0.0f, 1.0f);
        Vector2 pos = new Vector2(0.0f, 0.0f);

        assertEquals(1.0f, MathUtils.barryCentric(a, b, c, pos), 0.0f);
    }

    @Test
    public void angle() {
        assertEquals(45.0f, MathUtils.angle(0.0f,0.0f,1.0f,1.0f), 0.0f);
    }

    @Test
    public void getAngleBetween() {
        float result = MathUtils.getAngleBetween(new Vector3(1,0f,0.0f), new Vector3(0,1,0));
        assertEquals(90f, Math.round(result), 0f);
    }

    @Test
    public void rotateUpDown() {
        Vector3 testVector = new Vector3(1f,0f,0f);
        MathUtils.rotateUpDown(testVector, -1f);

        assertEquals(0.9998f, roundUp(testVector.x), 0);
        assertEquals(-0.0175f, roundUp(testVector.y), 0);
        assertEquals(0f, testVector.z, 0);

    }

    private float roundUp(float value) {
        return (float) (Math.round(value * 10000.00) / 10000.00);
    }

}