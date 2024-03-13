package com.mbrlabs.mundus.pluginapi.ui;

public interface RootWidget {

    Widget addRadioButtons(String button1Text, String button2Text, RadioButtonListener listener);

    Widget addSpinner(String text, int min, int max, int initValue, SpinnerListener listener);

    Widget addCheckbox(String text, CheckboxListener listener);

    void addRow();
}
