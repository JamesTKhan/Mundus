package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * This class comes from gdx-gltf's ShaderParser, but slightly modified to first
 * check for the glsl files in Mundus, and if not found, then search for the original
 * file in gdx-gltf.
 *
 * ShaderParser allows to recursively load shader code split into several files.
 *
 * It brings support for file inclusion like: <pre>#include&lt;part.glsl&gt;</pre>
 *
 * Given paths are relative to the file declaring the include statement.
 *
 * @author mgsx
 *
 */
public class MundusShaderParser {
    private final static String includeBefore = "#include <";
    private final static String includeAfter = ">";

    public static String parse(FileHandle file){
        String content = file.readString();
        String[] lines = content.split("\n");
        String result = "";
        for(String line : lines){
            String cleanLine = line.trim();

            if(cleanLine.startsWith(includeBefore)){
                int end = cleanLine.indexOf(includeAfter, includeBefore.length());
                if(end < 0) throw new GdxRuntimeException("malformed include: " + cleanLine);
                String path = cleanLine.substring(includeBefore.length(), end);
                FileHandle subFile = file.sibling(path);

                // If Mundus does not have a custom version of the file, look in the
                // original gdx-gltf location for it
                if (!subFile.exists()) {
                    subFile = Gdx.files.classpath("net/mgsx/gltf/shaders/pbr/" + path);
                }

                result += "\n//////// " + path + "\n";
                result += parse(subFile);
            }else{
                result += line + "\n";
            }
        }
        return result;
    }

}