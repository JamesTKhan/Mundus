package com.mbrlabs.mundus.pluginapi;

import com.mbrlabs.mundus.pluginapi.ui.RootWidget;
import org.pf4j.ExtensionPoint;

public interface MenuExtension extends ExtensionPoint {

    String getMenuName();

    void setupDialogRootWidget(RootWidget rootWidget);
}
