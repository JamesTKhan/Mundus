package com.mbrlabs.mundus.pluginapi.ui;

public interface Cell {

    Cell setAlign(WidgetAlign align);

    Cell setPad(float top, float right, float bottom, float left);

    Cell grow();

    void delete();
}
