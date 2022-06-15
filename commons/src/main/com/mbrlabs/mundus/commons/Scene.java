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

package com.mbrlabs.mundus.commons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btHeightfieldTerrainShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.SkyboxAsset;
import com.mbrlabs.mundus.commons.assets.TerrainAsset;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.physics.PhysicsState;
import com.mbrlabs.mundus.commons.physics.bullet.BulletPhysicsSystem;
import com.mbrlabs.mundus.commons.physics.bullet.GameObjectMotionState;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.InvalidComponentException;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.shaders.DepthShader;
import com.mbrlabs.mundus.commons.shaders.ModelShader;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.commons.utils.NestableFrameBuffer;
import com.mbrlabs.mundus.commons.water.WaterResolution;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable {

    private String name;
    private long id;

    public SceneGraph sceneGraph;
    public MundusEnvironment environment;
    public BulletPhysicsSystem physicsSystem;
    public Skybox skybox;
    public String skyboxAssetId;
    public float waterHeight = 0f;
    public WaterResolution waterResolution = WaterResolution.DEFAULT_WATER_RESOLUTION;

    @Deprecated // TODO not here
    public Array<TerrainAsset> terrains;
    @Deprecated // TODO not here
    public GameObject currentSelection;

    public PerspectiveCamera cam;
    public ModelBatch batch;

    private FrameBuffer fboWaterReflection;
    private FrameBuffer fboWaterRefraction;
    private FrameBuffer fboDepthRefraction;

    protected Vector3 clippingPlaneDisable = new Vector3(0.0f, 0f, 0.0f);
    protected Vector3 clippingPlaneReflection = new Vector3(0.0f, 1f, 0.0f);
    protected Vector3 clippingPlaneRefraction = new Vector3(0.0f, -1f, 0.0f);

    private final float distortionEdgeCorrection = 1f;

    private PhysicsState physicsState = PhysicsState.RUNNING;

    public Scene() {
        environment = new MundusEnvironment();
        currentSelection = null;
        terrains = new Array<>();

        Bullet.init();
        physicsSystem = new BulletPhysicsSystem();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0, 1, -1);
        cam.near = 0.2f;
        cam.far = 10000;

        DirectionalLight dirLight = new DirectionalLight();
        dirLight.color.set(DirectionalLight.DEFAULT_COLOR);
        dirLight.intensity = DirectionalLight.DEFAULT_INTENSITY;
        dirLight.direction.set(DirectionalLight.DEFAULT_DIRECTION);
        dirLight.direction.nor();
        environment.add(dirLight);
        environment.getAmbientLight().intensity = 0.8f;

        sceneGraph = new SceneGraph(this);
    }

    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }
    FloatBuffer fb;
    DebugDrawer debugDrawer;
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3))
            physicsState = PhysicsState.PAUSED;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4))
            physicsState = PhysicsState.RUNNING;

        if (physicsState == PhysicsState.RUNNING) {
            physicsSystem.update(delta);
        } else {
            physicsSystem.updatePhysicsTransforms();
        }

        if (fboWaterReflection == null) {
            Vector2 res = waterResolution.getResolutionValues();
            initFrameBuffers((int) res.x, (int) res.y);

            for (GameObject object : sceneGraph.getGameObjects()) {
                if (object.name.contains("Ship")) {
                    BoundingBox boundingBox = new BoundingBox();

                    ModelComponent modelComponent = (ModelComponent) object.findComponentByType(Component.Type.MODEL);
                    modelComponent.getModelInstance().calculateBoundingBox(boundingBox);
                    Vector3 dim = new Vector3();
                    boundingBox.getDimensions(dim);
                    Vector3 scale = new Vector3();
                    object.getLocalScale(scale);

                    dim.scl(scale.scl(.3f)); // Boxes are always too big so downscale further
                    btBoxShape shape = new btBoxShape(dim);
                    physicsSystem.addRigidBody(shape, 40f, .9f, object.getTransform(), new GameObjectMotionState(object), object);
                }

                if (object.name.contains("Terrain")) {
                    TerrainComponent terrainComponent = (TerrainComponent) object.findComponentByType(Component.Type.TERRAIN);

                     float minHeightChunk = 999;// min height for entire chunk
                     float maxHeightChunk = -999;// max height for entire chunk
                    for (float height : terrainComponent.getTerrain().getData()) {
                        // Set height min/maxes for bullet
                        if (height < minHeightChunk)
                            minHeightChunk = height;
                        else if (height > maxHeightChunk)
                            maxHeightChunk = height;
                    }

                    ByteBuffer vbb = ByteBuffer.allocateDirect(terrainComponent.getTerrain().getData().length * 4);
                    vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
                    fb = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
                    fb.put(terrainComponent.getTerrain().getData());    // add the coordinates to the FloatBuffer
                    fb.position(0);

                    float size = terrainComponent.getTerrain().getTerrain().terrainWidth;
                    float vertexCount = terrainComponent.getTerrain().getTerrain().vertexResolution;
                    btHeightfieldTerrainShape terrainShape = new btHeightfieldTerrainShape(terrainComponent.getTerrain().getTerrain().vertexResolution, terrainComponent.getTerrain().getTerrain().vertexResolution, fb, 1, minHeightChunk, maxHeightChunk, 1, true);
                    terrainShape.setLocalScaling(new Vector3((size) / ((vertexCount - 1) * 1f), 1, (size) / ((vertexCount - 1) * 1f)));
                    terrainShape.setMargin(0);
                    btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, terrainShape, new Vector3( 0,0,0));
                    btRigidBody body = new btRigidBody(constructionInfo);
                    body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);

                    float adjustedHeight = (maxHeightChunk + minHeightChunk) / 2f;
                    body.setWorldTransform(body.getWorldTransform().setTranslation(new Vector3((size / 2f), adjustedHeight, (size / 2f))));

                    physicsSystem.getDynamicsWorld().addRigidBody(body);
                }
            }
         }

        if (sceneGraph.isContainsWater()) {
            captureDepth(delta);
            captureReflectionFBO(delta);
            captureRefractionFBO(delta);
        }

        renderSkybox();
        renderObjects(delta);
        renderWater(delta);

        if (debugDrawer == null)  {
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawAabb);
            physicsSystem.getDynamicsWorld().setDebugDrawer(debugDrawer);

            modelShader.init();
            depthShader.init();
            createBoxes();
        }
//        debugDrawer.begin(cam);
//        physicsSystem.getDynamicsWorld().debugDrawWorld();
//        debugDrawer.end();
    }

    private void createBoxes() {
        Material material = new Material();
        material.set(ColorAttribute.createDiffuse(Color.RED));

        BoundingBox boundingBox = new BoundingBox();

        for (int i = 0; i < 100; i++) {
            GameObject gameObject = new GameObject(sceneGraph, "test", 99);
            gameObject.setLocalPosition(MathUtils.random(1000,1400), 400, MathUtils.random(300f,500f));
            ModelComponent modelComponent = new ModelComponent(gameObject, modelShader);
            modelComponent.setDepthShader(depthShader);

            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            MeshPartBuilder builder = modelBuilder.part("ID", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
            BoxShapeBuilder.build(builder, 0,0,0, 12f, 16f, 10f);
            Model model = modelBuilder.end();
            modelComponent.setModel(model);
            try {
                gameObject.addComponent(modelComponent);
                sceneGraph.addGameObject(gameObject);

                modelComponent.getModelInstance().calculateBoundingBox(boundingBox);
                Vector3 dim = new Vector3();
                boundingBox.getDimensions(dim);
                Vector3 scale = new Vector3();
                gameObject.getLocalScale(scale);

                dim.scl(scale.scl(.4f)); // Boxes are always too big so downscale further
                btBoxShape shape = new btBoxShape(dim);
                physicsSystem.addRigidBody(shape, 1f, .9f, gameObject.getTransform(), new GameObjectMotionState(gameObject), gameObject);
            } catch (InvalidComponentException e) {
                e.printStackTrace();
            }
        }

    }

    ModelShader modelShader = new ModelShader();
    DepthShader depthShader = new DepthShader();
    private void renderObjects(float delta) {
        // Render objects
        batch.begin(cam);
        sceneGraph.render(delta, clippingPlaneDisable, 0);
        batch.end();
    }

    private void renderWater(float delta) {
        if (sceneGraph.isContainsWater()) {
            Texture refraction = fboWaterRefraction.getColorBufferTexture();
            Texture reflection = fboWaterReflection.getColorBufferTexture();
            Texture refractionDepth = fboDepthRefraction.getColorBufferTexture();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // Render Water
            batch.begin(cam);
            sceneGraph.renderWater(delta, reflection, refraction, refractionDepth);
            batch.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void initFrameBuffers(int width, int height) {
        fboWaterReflection = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
        fboWaterRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
        fboDepthRefraction = new NestableFrameBuffer(Pixmap.Format.RGB888, width, height, true);
    }

    private void captureReflectionFBO(float delta) {
        // Calc vertical distance for camera for reflection FBO
        float camReflectionDistance = 2 * (cam.position.y - waterHeight);

        // Save current cam positions
        Vector3 camPos = cam.position.cpy();
        Vector3 camDir = cam.direction.cpy();

        // Position camera for reflection capture
        cam.direction.scl(1, -1, 1).nor();
        cam.position.sub(0, camReflectionDistance, 0);
        cam.update();

        // Render reflections to FBO
        fboWaterReflection.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderSkybox();
        batch.begin(cam);
        sceneGraph.render(delta, clippingPlaneReflection, -waterHeight + distortionEdgeCorrection);
        batch.end();
        fboWaterReflection.end();

        // Restore camera positions
        cam.direction.set(camDir);
        cam.position.set(camPos);
        cam.update();
    }

    private void captureDepth(float delta) {
        // Render depth refractions to FBO
        fboDepthRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(cam);
        sceneGraph.renderDepth(delta, clippingPlaneRefraction, waterHeight + distortionEdgeCorrection);
        batch.end();
        fboDepthRefraction.end();
    }

    private void renderSkybox() {
        if (skybox != null) {
            batch.begin(cam);
            batch.render(skybox.getSkyboxInstance(), environment, skybox.shader);
            batch.end();
        }
    }

    private void captureRefractionFBO(float delta) {
        // Render refractions to FBO
        fboWaterRefraction.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        renderSkybox();
        batch.begin(cam);
        sceneGraph.render(delta, clippingPlaneRefraction, waterHeight + distortionEdgeCorrection);
        batch.end();
        fboWaterRefraction.end();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Set the water resolution to use for water reflection and refractions.
     * This will reinitialize the frame buffers with the given resolution.
     * @param resolution the resolution to use
     */
    public void setWaterResolution(WaterResolution resolution) {
        this.waterResolution = resolution;
        Vector2 res = waterResolution.getResolutionValues();
        initFrameBuffers((int) res.x, (int) res.y);
    }

    /**
     * Sets and switches the scenes skybox to the given SkyboxAsset.
     *
     * @param skyboxAsset the asset to use
     * @param skyboxShader the shader to use
     */
    public void setSkybox(SkyboxAsset skyboxAsset, Shader skyboxShader) {
        if (skyboxAsset == null) return;

        skyboxAssetId = skyboxAsset.getID();
        skybox =  new Skybox(skyboxAsset.positiveX.getFile(),
                skyboxAsset.negativeX.getFile(),
                skyboxAsset.positiveY.getFile(),
                skyboxAsset.negativeY.getFile(),
                skyboxAsset.positiveZ.getFile(),
                skyboxAsset.negativeZ.getFile(),
                skyboxShader);

        skybox.setRotateSpeed(skyboxAsset.rotateSpeed);
        skybox.setRotateEnabled(skyboxAsset.rotateEnabled);

    }

    @Override
    public void dispose() {
        if (skybox != null) {
            skybox.dispose();
        }

        if (physicsSystem != null) {
            physicsSystem.dispose();
        }
    }
}
