package com.mbrlabs.mundus.editor.exporter;

import com.badlogic.gdx.utils.JsonWriter;
import com.mbrlabs.mundus.commons.dto.GameObjectDTO;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.commons.dto.TerrainComponentDTO;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.HistoryTest;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.system.CallbackI;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ExporterTest {

    private Exporter exporter;

    @Before
    public void setUp() {
        KryoManager manager = mock(KryoManager.class);
        ProjectContext context = mock(ProjectContext.class);

        exporter = new Exporter(manager, context);
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

        List<String> tags = new ArrayList<>();
        tags.add("grass");
        terrain.setTags(tags);

        return terrain;
    }

}
