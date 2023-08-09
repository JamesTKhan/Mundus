package com.mbrlabs.mundus.editor.shader;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.shaders.PBRTerrainShader;

/**
 * Overrides PBRTerrainShader to include picking data
 *
 * @author JamesTKhan
 * @version August 16, 2022
 */
public class EditorPBRTerrainShader extends PBRTerrainShader {
    protected final int UNIFORM_PICKER_POS = register(new Uniform("u_pickerPos"));
    protected final int UNIFORM_PICKER_RADIUS = register(new Uniform("u_pickerRadius"));
    protected final int UNIFORM_MOUSE_ACTIVE = register(new Uniform("u_pickerActive"));

    private static boolean pickerActive = false;
    private static final Vector3 pickerPosition = new Vector3();
    private static float pickerRadius = 0;

    public EditorPBRTerrainShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);
    }

    @Override
    public void render(Renderable renderable) {
        // mouse picking
        if(pickerActive) {
            set(UNIFORM_MOUSE_ACTIVE, 1);
            set(UNIFORM_PICKER_POS, pickerPosition);
            set(UNIFORM_PICKER_RADIUS, pickerRadius);
        } else {
            set(UNIFORM_MOUSE_ACTIVE, 0);
        }

        super.render(renderable);
    }

    public static void activatePicker(boolean active) {
        pickerActive = active;
    }

    public static void setPickerPosition(float x, float y, float z) {
        pickerPosition.set(x, y, z);
    }

    public static void setPickerRadius(float radius) {
        pickerRadius = radius;
    }
}
