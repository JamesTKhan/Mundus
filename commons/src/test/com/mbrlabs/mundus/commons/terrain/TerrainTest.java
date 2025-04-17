package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.math.Matrix4;
import org.junit.Assert;
import org.junit.Test;

public class TerrainTest {

    @Test
    public void testGetHeightAtWorldCoordOnC00AndC10Line() {
        // given
        final int vertexResolution = 50;
        final int terrainWidth = 100;
        // The gridSquareSize is 2.0408163 ((float)terrainWidth / ((float)vertexResolution -1)

        // and
        final float terrainX = 15.412959f; // The gridX is 7
        final float terrainZ = 22.448978f; // The gridZ is 11

        // and
        final float[] heightData = new float[vertexResolution * vertexResolution];
        heightData[(11 + 1) * vertexResolution + 7] = 5.3379793f; // C01
        heightData[11 * vertexResolution + 7 + 1] = 4.534852f; // C10
        heightData[11 * vertexResolution + 7] = 4.63853f; // C00
        heightData[(11 + 1) * vertexResolution + 7 + 1] = 5.5601234f; // C11

        final Terrain terrain = new Terrain(vertexResolution, heightData);
        terrain.terrainWidth = terrainWidth;

        // when get height value on C00 - C10 line
        final float result = terrain.getHeightAtWorldCoord(terrainX, terrainZ, new Matrix4());

        // then the value will be from C00 - C10 line
        Assert.assertEquals(4.57589f, result, 0.01f);
    }

}
