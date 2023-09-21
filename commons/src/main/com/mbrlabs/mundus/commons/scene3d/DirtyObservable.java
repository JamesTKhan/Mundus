package com.mbrlabs.mundus.commons.scene3d;

import com.badlogic.gdx.utils.Array;

/**
 * Observable that notifies listeners when it is marked dirty.
 *
 * @author JamesTKhan
 * @version September 17, 2023
 */
public class DirtyObservable {
    private Array<DirtyListener> listeners;

    public void addListener(DirtyListener listener) {
        if (listeners == null) listeners = new Array<>();
        listeners.add(listener);
    }

    public void removeListener(DirtyListener listener) {
        if (listeners == null) return;
        listeners.removeValue(listener, true);
    }

    public void notifyListeners() {
        if (listeners == null) return;
        for (DirtyListener listener : listeners) {
            listener.onDirty();
        }
    }
}
