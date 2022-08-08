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

package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Marcus Brummer
 * @version 03-12-2015
 */
public class MathUtils {
    private static final Vector3 tmp = new Vector3();

    public static float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    /**
     * Angle between 2 points.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float angle(float x1, float y1, float x2, float y2) {
        return (float) Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
    }

    /**
     * Get an angle between two Vector3s
     *
     * @param from the vector to compare
     * @param to the vector to compare with
     * @return angle in degrees
     */
    public static float getAngleBetween(Vector3 from, Vector3 to) {
        float absolute = (float) Math.sqrt(from.len() * to.len());
        if (com.badlogic.gdx.math.MathUtils.isZero(absolute))
            return 0; // It is close enough to just return 0

        float angleDot = from.dot(to);
        float dot = com.badlogic.gdx.math.MathUtils.clamp(angleDot / absolute, -1f, 1f);
        return com.badlogic.gdx.math.MathUtils.acos(dot) * com.badlogic.gdx.math.MathUtils.radiansToDegrees;
    }

    /**
     * Rotate a directional vector up/down by given angle.
     *
     * @param vectorToRotate the vector to rotate
     * @param angleDegrees the angle in degrees to rotate by
     */
    public static void rotateUpDown(Vector3 vectorToRotate, float angleDegrees) {
        tmp.set(vectorToRotate);

        // Determine the axis to use
        Vector3 axis = tmp.crs(Vector3.Y);

        // If collinear, set to right
        if (axis == Vector3.Zero) axis = Vector3.X;

        vectorToRotate.rotate(axis, angleDegrees);
    }

    public static boolean isPowerOfTwo(int number) {
        return (number & (number - 1)) == 0;
    }

}
