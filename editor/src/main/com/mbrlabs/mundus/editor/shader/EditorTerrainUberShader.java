package com.mbrlabs.mundus.editor.shader;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.shaders.TerrainUberShader;

/**
 * Overrides TerrainUberShader to include picking data
 *
 * @author JamesTKhan
 * @version August 16, 2022
 */
public class EditorTerrainUberShader extends TerrainUberShader {
    protected final int UNIFORM_PICKER_POS = register(new Uniform("u_pickerPos"));
    protected final int UNIFORM_PICKER_RADIUS = register(new Uniform("u_pickerRadius"));
    protected final int UNIFORM_MOUSE_ACTIVE = register(new Uniform("u_pickerActive"));

    private static boolean pickerActive = false;
    private static Vector3 pickerPosition = new Vector3();
    private static float pickerRadius = 0;

    public boolean invalid = false;

    public EditorTerrainUberShader(Renderable renderable, DefaultShader.Config config) {
        super(renderable, config);
    }

    @Override
    protected String createPrefixForRenderable(Renderable renderable) {
        String prefix = "#define PICKER\n";
        return prefix + super.createPrefixForRenderable(renderable);
    }

    @Override
    public boolean canRender(Renderable instance) {
        if (invalid) {
            return false;
        }
        return super.canRender(instance);
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
