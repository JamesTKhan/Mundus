package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testIsPointOnOrInTriangle() {
        final float ax = 0.0f;
        final float ay = 0.0f;
        final float bx = 1.0f;
        final float by = 0.0f;
        final float cx = 0.0f;
        final float cy = 1.0f;

        // AB side
        assertTrue(MathUtils.isPointOnOrInTriangle(0.54f, 0.0f, ax, ay, bx, by, cx, cy));

        // AC side
        assertTrue(MathUtils.isPointOnOrInTriangle(0.0f, 0.54f, ax, ay, bx, by, cx, cy));

        // BC side
        assertTrue(MathUtils.isPointOnOrInTriangle(0.5f, 0.5f, ax, ay, bx, by, cx, cy));

        // Inside triangle
        assertTrue(MathUtils.isPointOnOrInTriangle(0.2f, 0.2f, ax, ay, bx, by, cx, cy));
    }

    private float roundUp(float value) {
        return (float) (Math.round(value * 10000.00) / 10000.00);
    }

}