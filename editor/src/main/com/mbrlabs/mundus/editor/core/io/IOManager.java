package com.mbrlabs.mundus.editor.core.io;

import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;

import java.io.FileNotFoundException;

/**
 * Manages loading and saving of registry and project data.
 *
 * @author JamesTKhan
 * @version August 03, 2023
 */
public interface IOManager {
    Registry loadRegistry();
    void saveRegistry(Registry registry);
    void saveProjectContext(ProjectContext context);
    ProjectContext loadProjectContext(ProjectRef ref) throws FileNotFoundException;
}
