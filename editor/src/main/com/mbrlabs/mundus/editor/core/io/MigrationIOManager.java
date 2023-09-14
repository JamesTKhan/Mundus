package com.mbrlabs.mundus.editor.core.io;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.KryoException;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.LogEvent;

import java.io.FileNotFoundException;

/**
 * An intermediate class that handles the migration of project data from Kryo to Json formatting
 * if necessary. If the project data is already in Json format, then this class will simply call JsonIOManager.
 * Eventually this class will be removed in favor of JsonIOManager.
 *
 * Backs up kryo .registry and .pro files before replacing them with JSON versions.
 *
 * @author JamesTKhan
 * @version August 03, 2023
 */
public class MigrationIOManager extends JsonIOManager {
    private KryoManager kryoManager;

    public MigrationIOManager() {
        // Kryo with TaggedFieldSerializer is not compatible with Java 9+ out of box
        // due to reflection. If JDK <= 1.8 we will check if the registry is still kryo and
        // if so, migrate it over to Json. If JDK > 1.8 we will just use JsonIOManager
        String version = System.getProperty("java.specification.version");
        double versionDouble = Double.parseDouble(version);
        if (versionDouble <= 1.8) {
            kryoManager = new KryoManager();
        }
    }
    @Override
    public Registry loadRegistry() {
        if (kryoManager != null && kryoManager.isRegistryKryo()) {
            migrateRegistry();
        }

        return super.loadRegistry();
    }

    @Override
    public ProjectContext loadProjectContext(ProjectRef ref) throws FileNotFoundException {
        if (kryoManager != null) {
            try {
                ProjectContext projectContext = kryoManager.loadProjectContext(ref);
                migrateProject(ref, projectContext);
            } catch (KryoException e) {
                // Assume that the project is in Json format already
            }
        }

        return super.loadProjectContext(ref);
    }

    private void migrateRegistry() {
        kryoManager.backupRegistry();

        // Migrate registry to Json
        Registry kryoRegistry = kryoManager.loadRegistry();
        saveRegistry(kryoRegistry);
    }

    private void migrateProject(ProjectRef project, ProjectContext kryoProject) {
        // Migrate project to Json
        if (kryoProject == null) return;

        String log = "Migrating project " + project.getName() + " to Json format";
        Mundus.INSTANCE.postEvent(new LogEvent(log));
        Gdx.app.log("MigrationIOManager", log);

        kryoProject.path = project.getPath();
        kryoProject.currScene.setName(kryoProject.activeSceneName);

        kryoManager.backupProjectContext(kryoProject);
        saveProjectContext(kryoProject);
    }
}
