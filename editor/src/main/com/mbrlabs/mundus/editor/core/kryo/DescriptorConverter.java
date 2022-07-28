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

package com.mbrlabs.mundus.editor.core.kryo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonWriter;
import com.mbrlabs.mundus.editor.core.kryo.descriptors.*;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectSettings;
import com.mbrlabs.mundus.editor.core.registry.KeyboardLayout;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.registry.Settings;

/**
 * Converts runtime formats into Kryo compatible formats for internal project
 * persistence.
 *
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class DescriptorConverter {

    private final static String TAG = DescriptorConverter.class.getSimpleName();

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Registry
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static RegistryDescriptor convert(Registry registry) {
        RegistryDescriptor descriptor = new RegistryDescriptor();

        descriptor.setLastProject(convert(registry.getLastOpenedProject()));
        for (ProjectRef projectRef : registry.getProjects()) {
            descriptor.getProjects().add(convert(projectRef));
        }
        descriptor.setSettingsDescriptor(convert(registry.getSettings()));

        return descriptor;
    }

    public static Registry convert(RegistryDescriptor descriptor) {
        boolean lastOpenedProjectDeleted = false;
        Registry registry = new Registry();

        registry.setLastProject(convert(descriptor.getLastProject()));
        for (ProjectRefDescriptor projectRef : descriptor.getProjects()) {

            // If the project files were deleted, do not convert
            boolean directoryExists =  Files.isDirectory(Paths.get(projectRef.getPath()));

            if (directoryExists) {
                registry.getProjects().add(convert(projectRef));
            } else if (projectRef.getPath().equals(descriptor.getLastProject().getPath())) {
                // Uh oh, the last project opened no longer exists, lets set a different one as last opened
                lastOpenedProjectDeleted = true;
            }

        }
        registry.setSettings(convert(descriptor.getSettingsDescriptor()));

        if (lastOpenedProjectDeleted) {
            if (!registry.getProjects().isEmpty()) {
                // Open the last project in the list
                registry.setLastProject(registry.getProjects().get(registry.getProjects().size()-1));
            }
        }

        return registry;
    }

    private static ProjectRef convert(ProjectRefDescriptor descriptor) {
        ProjectRef project = new ProjectRef();
        project.setName(descriptor.getName());
        project.setPath(descriptor.getPath());

        return project;
    }

    private static ProjectRefDescriptor convert(ProjectRef project) {
        ProjectRefDescriptor descriptor = new ProjectRefDescriptor();
        descriptor.setPath(project.getPath());
        descriptor.setName(project.getName());

        return descriptor;
    }

    private static Settings convert(SettingsDescriptor descriptor) {
        Settings settings = new Settings();
        settings.setFbxConvBinary(descriptor.getFbxConvBinary());
        settings.setKeyboardLayout(descriptor.getKeyboardLayout());

        if (settings.getKeyboardLayout() == null) {
            if (Locale.getDefault().equals(Locale.GERMAN) || Locale.getDefault().equals(Locale.GERMANY)) {
                settings.setKeyboardLayout(KeyboardLayout.QWERTZ);
            } else {
                settings.setKeyboardLayout(KeyboardLayout.QWERTY);
            }
        }

        return settings;
    }

    private static SettingsDescriptor convert(Settings settings) {
        SettingsDescriptor descriptor = new SettingsDescriptor();
        descriptor.setKeyboardLayout(settings.getKeyboardLayout());
        descriptor.setFbxConvBinary(settings.getFbxConvBinary());

        return descriptor;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Project
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ProjectDescriptor convert(ProjectContext project) {
        ProjectDescriptor descriptor = new ProjectDescriptor();
        descriptor.setName(project.name);
        descriptor.setCurrentSceneName(project.currScene.getName());
        descriptor.setNextAvailableID(project.inspectCurrentID());
        descriptor.setSettings(convert(project.settings));

        // scenes
        for (String sceneName : project.scenes) {
            descriptor.getSceneRefDescriptor().add(new SceneRefDescriptor(sceneName));
        }

        return descriptor;
    }

    public static ProjectContext convert(ProjectDescriptor projectDescriptor) {
        ProjectContext context = new ProjectContext(projectDescriptor.getNextAvailableID());
        context.name = projectDescriptor.getName();

        // project settings
        context.settings = convert(projectDescriptor.getSettings());

        // scenes
        for (SceneRefDescriptor sceneRefDescriptor : projectDescriptor.getSceneRefDescriptor()) {
            context.scenes.add(sceneRefDescriptor.getName());
        }

        return context;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Project Settings
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ProjectSettingsDescriptor convert(ProjectSettings settings) {
        ProjectSettingsDescriptor descriptor = new ProjectSettingsDescriptor();

        // export settings
        descriptor.setExportAllAssets(settings.getExport().allAssets);
        descriptor.setExportCompressScenes(settings.getExport().compressScenes);
        if(settings.getExport().outputFolder != null) {
            descriptor.setExportOutputFolder(settings.getExport().outputFolder.path());
        }
        descriptor.setJsonType(settings.getExport().jsonType.toString());

        return descriptor;
    }

    public static ProjectSettings convert(ProjectSettingsDescriptor descriptor) {
        ProjectSettings settings = new ProjectSettings();
        if(descriptor == null) return settings;

        // export settings
        settings.getExport().allAssets = descriptor.isExportAllAssets();
        settings.getExport().compressScenes = descriptor.isExportCompressScenes();
        if(descriptor.getExportOutputFolder() != null && descriptor.getExportOutputFolder().length() > 0) {
            settings.getExport().outputFolder = new FileHandle(descriptor.getExportOutputFolder());
        }
        settings.getExport().jsonType = JsonWriter.OutputType.valueOf(descriptor.getJsonType());

        return settings;
    }

}
