package com.mbrlabs.mundus.editor.exporter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonWriter;
import com.mbrlabs.mundus.commons.dto.GameObjectDTO;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.commons.dto.TerrainComponentDTO;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import org.junit.Before;
import org.junit.Test;
import org.pf4j.DefaultPluginManager;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ExporterTest {

    private Exporter exporter;

    @Before
    public void setUp() {
        KryoManager manager = mock(KryoManager.class);
        ProjectContext context = mock(ProjectContext.class);
        DefaultPluginManager pluginManager = mock(DefaultPluginManager.class);

        exporter = new Exporter(manager, context, pluginManager);
    }

    @Test
    public void testExportEmptyScene() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);

        SceneDTO scene = new SceneDTO();
        scene.setName("Scene 1");
        exporter.exportScene(scene, writer, JsonWriter.OutputType.json);

        String result = baos.toString();
        assertEquals("{\"id\":0,\"name\":\"Scene 1\",\"gos\":[]}", result);
    }

    @Test
    public void testExportSimpleScene() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);

        GameObjectDTO terrain = buildTerrain("Terrain 1");
        SceneDTO scene = new SceneDTO();
        scene.setName("Scene 1");
        scene.getGameObjects().add(terrain);

        exporter.exportScene(scene, writer, JsonWriter.OutputType.json);

        String result = baos.toString();
        assertEquals("{\"id\":0,\"name\":\"Scene 1\",\"gos\":[{\"i\":0,\"n\":\"Terrain 1\",\"a\":false,\"t\":[0,0,0,0,0,0,0,0,0,0],\"g\":[\"grass\"],\"ct\":{\"i\":null}}]}", result);
    }

    private GameObjectDTO buildTerrain(String name) {
        GameObjectDTO terrain = new GameObjectDTO();
        terrain.setName(name);

        TerrainComponentDTO terrainComponent = new TerrainComponentDTO();
        terrain.setTerrainComponent(terrainComponent);

        Array<String> tags = new Array<>();
        tags.add("grass");
        terrain.setTags(tags);

        return terrain;
    }

}
