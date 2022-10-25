package com.mbrlabs.mundus.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.mbrlabs.mundus.commons.terrain.Terrain;

import java.io.IOException;
import java.io.Writer;

/**
 * @author JamesTKhan
 * @version July 21, 2022
 */
public class ObjExporter {
    private static final Vector3 tmpVec = new Vector3();

    public static void exportToObj(String fileName, Terrain terrain) throws GdxRuntimeException {
        Model model = terrain.getModel();
        int vertexResolution = terrain.vertexResolution;
        final int w = vertexResolution - 1;
        final int h = vertexResolution - 1;

        float[] exportVertices = new float[terrain.getVertices().length];
        short[] indices = new short[w * h * 6];

        Mesh mesh = model.meshes.get(0);
        mesh.getVertices(exportVertices);
        mesh.getIndices(indices);

        int posPos = mesh.getVertexAttributes().getOffset(VertexAttributes.Usage.Position);

        FileHandle fileHandle = Gdx.files.local(fileName + ".obj");

        StringBuilder vertices = new StringBuilder();
        StringBuilder ind = new StringBuilder();
        try {

            // Write vertices to string
            for (int x = 0; x < vertexResolution; x++) {
                for (int z = 0; z < vertexResolution; z++) {

                    if (posPos >= 0) {
                        terrain.getVertexPosition(tmpVec, z, x);
//                        tmpVec.scl(0.1f);
                        vertices.append("v ")
                                .append(tmpVec.x)
                                .append(" ")
                                .append(tmpVec.y)
                                .append(" ")
                                .append(tmpVec.z)
                                .append("\n");
                    }
                }
            }

            // Write indices to string
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    final int c00 = y * vertexResolution + x;
                    final int c10 = c00 + 1;
                    final int c01 = c00 + vertexResolution;
                    final int c11 = c10 + vertexResolution;

                    // Blender throws index out of range if we do not increment by 1
                    ind.append("f ").append(c11 + 1)
                            .append(" ")
                            .append(c10 + 1)
                            .append(" ")
                            .append(c00 + 1)
                            .append("\nf ")
                            .append(c00 + 1)
                            .append(" ")
                            .append(c01 + 1)
                            .append(" ")
                            .append(c11 + 1)
                            .append("\n");
                }
            }

            Writer writer = fileHandle.writer(false);
            writer.write(vertices.toString());
            writer.write(ind.toString());
            StreamUtils.closeQuietly(writer);
        } catch (IOException e) {
            throw new GdxRuntimeException(e);
        }
    }
}
