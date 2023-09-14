package com.mbrlabs.mundus.editor.core.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.esotericsoftware.kryo.io.Input;
import com.mbrlabs.mundus.editor.core.kryo.DescriptorConverter;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.ProjectDescriptor;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.utils.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Writer;

/**
 * Manages loading and saving of registry and project data in JSON Format.
 *
 * @author JamesTKhan
 * @version August 03, 2023
 */
public class JsonIOManager implements IOManager {
    private final Json json;

    public JsonIOManager() {
        json = new Json(JsonWriter.OutputType.json);
    }

    @Override
    public Registry loadRegistry() {
        FileHandle fileHandle = getHomeDataFile();
        if (!fileHandle.exists()) {
            Log.info(getClass().getSimpleName(), "No registry file found. Creating new one.");
            return new Registry();
        }

        try {
            return json.fromJson(Registry.class, fileHandle);
        } catch (GdxRuntimeException e) {
            Log.warn(getClass().getSimpleName(), "Could not load registry file. Creating new one.");
            return new Registry();
        }
    }

    @Override
    public void saveRegistry(Registry registry) {
        FileHandle fileHandle = getHomeDataFile();

        Writer writer = fileHandle.writer(false);
        json.setWriter(writer);

        String jsonString = json.prettyPrint(registry);
        fileHandle.writeString(jsonString, false);
    }

    @Override
    public void saveProjectContext(ProjectContext context) {
        FileHandle fileHandle = new FileHandle(context.path + "/" +
                context.name + "." + ProjectManager.PROJECT_EXTENSION);

        Writer writer = fileHandle.writer(false);
        json.setWriter(writer);

        ProjectDescriptor descriptor = DescriptorConverter.convert(context);

        String jsonString = json.prettyPrint(descriptor);
        fileHandle.writeString(jsonString, false);
    }

    @Override
    public ProjectContext loadProjectContext(ProjectRef ref) throws FileNotFoundException {
        // find .pro file
        FileHandle projectFile = null;
        for (FileHandle f : Gdx.files.absolute(ref.getPath()).list()) {
            if (f.extension().equals(ProjectManager.PROJECT_EXTENSION)) {
                projectFile = f;
                break;
            }
        }

        if (projectFile != null) {
            Input input = new Input(new FileInputStream(projectFile.path()));
            ProjectDescriptor projectDescriptor = json.fromJson(ProjectDescriptor.class, input);

            ProjectContext context = DescriptorConverter.convert(projectDescriptor);
            context.activeSceneName = projectDescriptor.getCurrentSceneName();
            return context;
        }

        return null;
    }

    private FileHandle getHomeDataFile() {
        return new FileHandle(Registry.HOME_DATA_FILE);
    }

}
