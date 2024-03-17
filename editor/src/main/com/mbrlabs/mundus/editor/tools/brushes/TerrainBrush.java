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

package com.mbrlabs.mundus.editor.tools.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.terrain.SplatMap;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import com.mbrlabs.mundus.commons.utils.Pools;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.GlobalBrushSettingsChangedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.TerrainHeightCommand;
import com.mbrlabs.mundus.editor.history.commands.TerrainPaintCommand;
import com.mbrlabs.mundus.editor.shader.EditorPBRTerrainShader;
import com.mbrlabs.mundus.editor.tools.Tool;
import com.mbrlabs.mundus.editor.ui.UI;
import com.mbrlabs.mundus.editorcommons.events.TerrainVerticesChangedEvent;

/**
 * A Terrain Brush can modify the terrainAsset in various ways (BrushMode).
 * <p>
 * This includes the height of every vertex in the terrainAsset grid & according
 * splatmap.
 *
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public abstract class TerrainBrush extends Tool {

    /**
     * Defines the draw mode of a brush.
     */
    public enum BrushMode {
        /** Raises or lowers the terrainAsset height. */
        RAISE_LOWER,
        /** Sets all vertices of the selection to a specified height. */
        FLATTEN,
        /** Smooths terrain based on average height within radius */
        SMOOTH,
        /** Paints on the splatmap of the terrainAsset. */
        PAINT,
        /** Create a ramp between two points. */
        RAMP
    }

    /**
     * Defines two actions (and it's key codes) every brush and every mode can
     * have.
     *
     * For instance the RAISE_LOWER mode has 'raise' has PRIMARY action and
     * 'lower' as secondary. Pressing the keycode of the secondary & the primary
     * key enables the secondary action.
     **/
    public enum BrushAction {
        PRIMARY(Input.Buttons.LEFT), SECONDARY(Input.Keys.SHIFT_LEFT);

        public final int code;

        BrushAction(int levelCode) {
            this.code = levelCode;
        }

    }

    /**
     * Thrown if a the brush is set to a mode, which it currently does not
     * support.
     */
    public class ModeNotSupportedException extends Exception {
        public ModeNotSupportedException(String message) {
            super(message);
        }
    }

    // used for calculations
    protected static final Vector3 rampEndPoint = new Vector3();
    protected static final Vector2 c = new Vector2();
    protected static final Vector2 p = new Vector2();
    protected static final Vector2 v = new Vector2();
    protected static final Color c0 = new Color();
    protected static final Vector3 tVec0 = new Vector3();
    protected static final Vector3 tVec1 = new Vector3();
    protected static final Vector3 tVec2 = new Vector3();
    private static final Matrix4 tmpMatrix = new Matrix4();

    // all brushes share the some common settings
    private static final GlobalBrushSettingsChangedEvent brushSettingsChangedEvent = new GlobalBrushSettingsChangedEvent();
    private static float strength = 0.5f;
    private static float heightSample = 0f;
    private static SplatTexture.Channel paintChannel;

    // individual brush settings
    protected final Vector3 brushPos = new Vector3();
    protected float radius = 25f;
    protected BrushMode mode;
    protected TerrainAsset terrainAsset;
    protected TerrainComponent terrainComponent;
    private BrushAction action;

    private boolean mouseMoved = false;

    // the pixmap brush
    private final Pixmap brushPixmap;
    private final int pixmapCenter;

    // undo/redo system
    private TerrainHeightCommand heightCommand = null;
    private TerrainPaintCommand paintCommand = null;
    private boolean terrainHeightModified = false;
    private boolean splatmapModified = false;

    public TerrainBrush(ProjectManager projectManager, CommandHistory history,
            FileHandle pixmapBrush) {
        super(projectManager, history);

        brushPixmap = new Pixmap(pixmapBrush);
        pixmapCenter = brushPixmap.getWidth() / 2;
    }

    @Override
    public void act() {
        if (action == null) return;
        if (terrainAsset == null) return;

        // sample height
        if (action == BrushAction.SECONDARY && (mode == BrushMode.FLATTEN)) {
            // brushPos is in world coords, convert to terrains local height by negating world height
            heightSample = brushPos.y - getTerrainPosition(tVec0).y;
            UI.INSTANCE.getToaster().success("Height Sampled: " + heightSample);
            action = null;
            return;
        }

        // Sample end point for ramp
        if (action == BrushAction.SECONDARY && (mode == BrushMode.RAMP)) {
            // brushPos is in world coords, convert to terrains local height by negating world height
            rampEndPoint.set(brushPos.x, brushPos.y - getTerrainPosition(tVec0).y, brushPos.z);
            UI.INSTANCE.getToaster().success("End Point Sampled: " + rampEndPoint);
            action = null;
            return;
        }

        // only act if mouse has been moved
        if (!mouseMoved) return;
        mouseMoved = false;

        if (mode == BrushMode.PAINT) {
            paint();
        } else if (mode == BrushMode.RAISE_LOWER) {
            raiseLower(action);
        } else if (mode == BrushMode.FLATTEN) {
            flatten();
        } else if (mode == BrushMode.SMOOTH) {
            smooth();
        } else if (mode == BrushMode.RAMP) {
            createRamp();
        }

    }

    private void paint() {
        Terrain terrain = terrainAsset.getTerrain();
        SplatMap sm = terrain.getTerrainTexture().getSplatmap();
        if (sm == null) return;

        // should convert world position to terrain local position
        getBrushLocalPosition(tVec1);

        final float splatX = (tVec1.x / (float) terrain.terrainWidth) * sm.getWidth();
        final float splatY = (tVec1.z / (float) terrain.terrainDepth) * sm.getHeight();
        final float splatRad = (radius / terrain.terrainWidth) * sm.getWidth();
        final Pixmap pixmap = sm.getPixmap();

        for (int smX = 0; smX < pixmap.getWidth(); smX++) {
            for (int smY = 0; smY < pixmap.getHeight(); smY++) {
                final float dst = Vector2.dst(splatX, splatY, smX, smY);
                if (dst <= splatRad) {
                    final float opacity = getValueOfBrushPixmap(splatX, splatY, smX, smY, splatRad) * 0.5f * strength;
                    int newPixelColor = sm.additiveBlend(pixmap.getPixel(smX, smY), paintChannel, opacity);
                    pixmap.drawPixel(smX, smY, newPixelColor);
                }
            }
        }

        sm.updateTexture();
        splatmapModified = true;
        getProjectManager().current().assetManager.addModifiedAsset(terrainAsset);
    }

    /**
     * Get average height of all vertices in radius, interpolate heights to average height
     * will a falloff effect based on distance from radius.
     */
    private void smooth() {
        Terrain terrain = terrainAsset.getTerrain();

        // should convert world position to terrain local position
        getBrushLocalPosition(tVec2);

        int weights = 0;
        float totalHeights = 0;

        // Get total height of all vertices within radius
        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z < terrain.vertexResolution; z++) {
                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
                float distance = vertexPos.dst(tVec2);

                tVec2.y = vertexPos.y;
                if (distance <= radius) {
                    totalHeights += vertexPos.y;
                    weights++;
                }
            }
        }

        float averageHeight = totalHeights / weights;

        // Interpolate height with averageHeight
        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z < terrain.vertexResolution; z++) {
                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
                tVec2.y = vertexPos.y;
                float distance = vertexPos.dst(tVec2);

                if (distance <= radius) {
                    final int index = z * terrain.vertexResolution + x;
                    float heightAtIndex = terrain.heightData[index];
                    // Determine how much to interpolate based on distance from radius
                    float elevation = getValueOfBrushPixmap(tVec2.x, tVec2.z, vertexPos.x, vertexPos.z, radius);
                    float smoothedHeight = Interpolation.smooth2.apply(heightAtIndex, averageHeight, elevation * strength);
                    terrain.heightData[index] = smoothedHeight;
                }
            }
        }

        terrain.update();
        terrainHeightModified = true;
        getProjectManager().current().assetManager.addModifiedAsset(terrainAsset);
        Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
    }

    private void createRamp() {
        Terrain terrain = terrainAsset.getTerrain();

        // tvec2 represents the start (brush) point of the ramp
        getBrushLocalPosition(tVec2);
        tVec2.y = brushPos.y - getTerrainPosition(tVec0).y;
        Vector3 startPoint = tVec2;

        // Calculate the direction and length of the ramp
        Vector3 rampDirection = tVec1.set(startPoint).sub(rampEndPoint).nor();
        float rampLength = startPoint.dst(rampEndPoint);

        // Half width for distance checking
        float rampWidth = radius * 2f;
        float halfWidth = rampWidth * 0.5f;

        Vector3 toVertex = Pools.vector3Pool.obtain();
        Vector2 nearestPoint = Pools.vector2Pool.obtain();
        Vector2 vertexPos2 = Pools.vector2Pool.obtain();
        Vector2 startPoint2 = Pools.vector2Pool.obtain().set(startPoint.x, startPoint.z);
        Vector2 rampEnd2 = Pools.vector2Pool.obtain().set(rampEndPoint.x, rampEndPoint.z);

        for (int i = 0; i < 1; i++) {

            for (int x = 0; x < terrain.vertexResolution; x++) {
                for (int z = 0; z < terrain.vertexResolution; z++) {
                    final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
                    toVertex.set(vertexPos).sub(TerrainBrush.rampEndPoint);

                    vertexPos2.set(vertexPos.x, vertexPos.z);
                    MathUtils.findNearestPointOnLine(rampEnd2, startPoint2, vertexPos2, nearestPoint);

                    float distanceToRampLine = vertexPos2.sub(nearestPoint).len();

                    if (distanceToRampLine <= halfWidth) {
                        // Calculate the height from the ramp line
                        float projectedLength = rampDirection.dot(toVertex);
                        float slope = (startPoint.y - TerrainBrush.rampEndPoint.y) / rampLength;
                        float rampHeight = TerrainBrush.rampEndPoint.y + projectedLength * slope;

                        // Interpolate the height based on the distance from the center of the ramp
                        float interpolationFactor = 1.0f - (distanceToRampLine / halfWidth);
                        float interpolatedHeight = Interpolation.smooth2.apply(vertexPos.y, rampHeight, interpolationFactor * strength);

                        // Set the height of the vertex
                        final int index = z * terrain.vertexResolution + x;
                        terrain.heightData[index] = interpolatedHeight;
                    }
                }
            }
        }

        Pools.free(nearestPoint, vertexPos2, startPoint2, rampEnd2);
        Pools.vector3Pool.free(toVertex);

        terrain.update();
        terrainHeightModified = true;
        getProjectManager().current().assetManager.addModifiedAsset(terrainAsset);
        Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
    }

    private void flatten() {
        Terrain terrain = terrainAsset.getTerrain();

        // should convert world position to terrain local position
        getBrushLocalPosition(tVec2);

        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z < terrain.vertexResolution; z++) {
                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
                tVec2.y = vertexPos.y;
                float distance = vertexPos.dst(tVec2);

                if (distance <= radius) {
                    final int index = z * terrain.vertexResolution + x;
                    final float diff = Math.abs(terrain.heightData[index] - heightSample);
                    if (diff <= 1f) {
                        terrain.heightData[index] = heightSample;
                    } else if (diff > 1f) {
                        final float elevation = getValueOfBrushPixmap(tVec2.x, tVec2.z, vertexPos.x, vertexPos.z,
                                radius);
                        // current height is lower than sample
                        if (heightSample > terrain.heightData[index]) {
                            terrain.heightData[index] += elevation * strength;
                        } else {
                            float newHeight = terrain.heightData[index] - elevation * strength;
                            if (diff > Math.abs(newHeight) || terrain.heightData[index] > heightSample) {
                                terrain.heightData[index] = newHeight;
                            }

                        }
                    }
                }
            }
        }

        terrain.update();
        terrainHeightModified = true;
        getProjectManager().current().assetManager.addModifiedAsset(terrainAsset);
        Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
    }

    private void raiseLower(BrushAction action) {
        Terrain terrain = terrainAsset.getTerrain();

        // should convert world position to terrain local position
        getBrushLocalPosition(tVec2);

        float dir = (action == BrushAction.PRIMARY) ? 1 : -1;
        for (int x = 0; x < terrain.vertexResolution; x++) {
            for (int z = 0; z < terrain.vertexResolution; z++) {
                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
                tVec2.y = vertexPos.y;

                float distance = vertexPos.dst(tVec2);

                if (distance <= radius) {
                    float elevation = getValueOfBrushPixmap(tVec2.x, tVec2.z, vertexPos.x, vertexPos.z, radius);
                    terrain.heightData[z * terrain.vertexResolution + x] += dir * elevation * strength;
                }
            }
        }

        terrain.update();
        terrainHeightModified = true;
        getProjectManager().current().assetManager.addModifiedAsset(terrainAsset);
        Mundus.INSTANCE.postEvent(new TerrainVerticesChangedEvent(terrainComponent));
    }

    /**
     * Interpolates the brush texture in the range of centerX - radius to
     * centerX + radius and centerZ - radius to centerZ + radius. PointZ &
     * pointX lies between these ranges.
     *
     * Interpolation is necessary, since the brush pixmap is fixed sized,
     * whereas the input values can scale. (Input points can be vertices or
     * splatmap texture coordinates)
     *
     * @return the interpolated r-channel value of brush pixmap at pointX,
     *         pointZ, which can be interpreted as terrainAsset height
     *         (raise/lower) or opacity (paint)
     */
    private float getValueOfBrushPixmap(float centerX, float centerZ, float pointX, float pointZ, float radius) {
        c.set(centerX, centerZ);
        p.set(pointX, pointZ);
        v.set(p.sub(c));

        final float progress = v.len() / radius;
        v.nor().scl(pixmapCenter * progress);

        final float mapX = pixmapCenter + (int) v.x;
        final float mapY = pixmapCenter + (int) v.y;
        c0.set(brushPixmap.getPixel((int) mapX, (int) mapY));

        return c0.r;
    }

    public void scale(float amount) {
        radius *= amount;
    }

    public static float getStrength() {
        return strength;
    }

    public static void setStrength(float strength) {
        TerrainBrush.strength = strength;
        Mundus.INSTANCE.postEvent(brushSettingsChangedEvent);
    }

    public static float getHeightSample() {
        return heightSample;
    }

    public static void setHeightSample(float heightSample) {
        TerrainBrush.heightSample = heightSample;
        Mundus.INSTANCE.postEvent(brushSettingsChangedEvent);
    }

    public static SplatTexture.Channel getPaintChannel() {
        return paintChannel;
    }

    public static void setPaintChannel(SplatTexture.Channel paintChannel) {
        TerrainBrush.paintChannel = paintChannel;
        Mundus.INSTANCE.postEvent(brushSettingsChangedEvent);
    }

    public BrushMode getMode() {
        return mode;
    }

    public void setMode(BrushMode mode) throws ModeNotSupportedException {
        if (!supportsMode(mode)) {
            throw new ModeNotSupportedException(getName() + " does not support " + mode);
        }
        this.mode = mode;
    }

    public TerrainAsset getTerrainAsset() {
        return terrainAsset;
    }

    public void setTerrainComponent(TerrainComponent terrainComponent) {
        this.terrainAsset = terrainComponent.getTerrainAsset();
        this.terrainComponent = terrainComponent;
    }

    public boolean supportsMode(BrushMode mode) {
        switch (mode) {
            case RAISE_LOWER:
            case FLATTEN:
            case PAINT:
            case SMOOTH:
            case RAMP:
                return true;
        }

        return false;
    }

    private Vector3 getTerrainPosition(Vector3 vector3) {
        terrainComponent.getModelInstance().transform.getTranslation(vector3);
        return vector3;
    }

    private void getBrushLocalPosition(Vector3 value) {
        value.set(brushPos);
        value.mul(tmpMatrix.set(terrainComponent.getModelInstance().transform).inv());
    }

    @Override
    public void render() {
        // rendering of the brush is done in the editor terrain shader
    }

    @Override
    public void dispose() {
        brushPixmap.dispose();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (terrainHeightModified && heightCommand != null) {
            heightCommand.setHeightDataAfter(terrainAsset.getTerrain().heightData);
            getHistory().add(heightCommand);
        }
        if (splatmapModified && paintCommand != null) {
            final SplatMap sm = terrainAsset.getTerrain().getTerrainTexture().getSplatmap();
            paintCommand.setAfter(sm.getPixmap());
            getHistory().add(paintCommand);
        }
        splatmapModified = false;
        terrainHeightModified = false;
        heightCommand = null;
        paintCommand = null;

        action = null;

        return false;
    }

    private BrushAction getAction() {
        final boolean primary = Gdx.input.isButtonPressed(BrushAction.PRIMARY.code);
        final boolean secondary = Gdx.input.isKeyPressed(BrushAction.SECONDARY.code);

        if (primary && secondary) {
            return BrushAction.SECONDARY;
        } else if (primary) {
            return BrushAction.PRIMARY;
        }

        return null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        // get action
        final boolean primary = Gdx.input.isButtonPressed(BrushAction.PRIMARY.code);
        final boolean secondary = Gdx.input.isKeyPressed(BrushAction.SECONDARY.code);
        if (primary && secondary) {
            action = BrushAction.SECONDARY;
        } else if (primary) {
            action = BrushAction.PRIMARY;
        } else {
            action = null;
        }

        if (mode == BrushMode.FLATTEN || mode == BrushMode.RAISE_LOWER || mode == BrushMode.SMOOTH || mode == BrushMode.RAMP) {
            heightCommand = new TerrainHeightCommand(terrainAsset.getTerrain());
            heightCommand.setHeightDataBefore(terrainAsset.getTerrain().heightData);
        } else if (mode == BrushMode.PAINT) {
            final SplatMap sm = terrainAsset.getTerrain().getTerrainTexture().getSplatmap();
            if (sm != null) {
                paintCommand = new TerrainPaintCommand(terrainAsset.getTerrain());
                paintCommand.setBefore(sm.getPixmap());
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (terrainComponent != null) {
            Ray ray = getProjectManager().current().currScene.viewport.getPickRay(screenX, screenY);
            terrainComponent.getRayIntersection(brushPos, ray);
        }

        mouseMoved = true;

        EditorPBRTerrainShader.setPickerPosition(brushPos.x, brushPos.y, brushPos.z);

        // Show mouse position if it is on terrain
        if (terrainComponent.isOnTerrain(brushPos.x, brushPos.z)) {
            UI.INSTANCE.getStatusBar().setMousePos(brushPos.x, brushPos.y, brushPos.z);
        } else {
            UI.INSTANCE.getStatusBar().clearMousePos();
        }

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY < 0) {
            scale(0.9f);
        } else {
            scale(1.1f);
        }
        EditorPBRTerrainShader.setPickerRadius(radius);

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    @Override
    public void onDisabled() {
        EditorPBRTerrainShader.activatePicker(false);
    }

    @Override
    public void onActivated() {
        EditorPBRTerrainShader.activatePicker(true);
        EditorPBRTerrainShader.setPickerPosition(brushPos.x, brushPos.y, brushPos.z);
        EditorPBRTerrainShader.setPickerRadius(radius);
    }

}
