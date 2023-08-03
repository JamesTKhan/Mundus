package com.mbrlabs.mundus.editor.core.io;

/**
 * Provider for IOManager. Needed because dependency injection with interfaces does
 * not seem to work, so we provide this POJO to inject the IOManager.
 *
 * @author JamesTKhan
 * @version August 03, 2023
 */
public class IOManagerProvider {
    private final IOManager ioManager;

    public IOManagerProvider(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    public IOManager getIOManager() {
        return ioManager;
    }
}
