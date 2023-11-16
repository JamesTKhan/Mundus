package com.mbrlabs.mundus.pluginapi.ui;

public interface RootWidget {

    void addRadioButtons(String button1Text, String button2Text, RadioButtonListener listener);

    void addRow();

    void addSpinner(String text, int min, int max, int initValue, SpinnerChangeListener listener);
}
