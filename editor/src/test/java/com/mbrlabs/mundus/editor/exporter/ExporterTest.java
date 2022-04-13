package com.mbrlabs.mundus.editor.exporter;

import com.badlogic.gdx.utils.JsonWriter;
import com.mbrlabs.mundus.commons.dto.SceneDTO;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.HistoryTest;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.system.CallbackI;

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

        exporter = new Exporter(manager, context);
    }

    @Test
    public void testExportEmptyScene() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);

        SceneDTO scene = new SceneDTO();
        exporter.exportScene(scene, writer, JsonWriter.OutputType.json);

        String result = baos.toString();
        assertEquals("{\"id\":0,\"name\":null,\"gos\":[]}", result);
    }

}
