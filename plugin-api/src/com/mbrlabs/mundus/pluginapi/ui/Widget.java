package com.mbrlabs.mundus.pluginapi.ui;

public interface Widget {

    enum WidgetAlign {
        LEFT,
        CENTER,
        RIGHT
    }

    void setAlign(WidgetAlign align);
}
