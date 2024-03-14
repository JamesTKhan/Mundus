package com.mbrlabs.mundus.pluginapi.ui;

public interface RootWidget {

    Widget addRadioButtons(String button1Text, String button2Text, RadioButtonListener listener);

    Widget addRadioButtons(String button1Text, String button2Text, boolean selectedFirst, RadioButtonListener listener);

    Widget addSpinner(String text, int min, int max, int initValue, SpinnerListener listener);

    Widget addCheckbox(String text, CheckboxListener listener);

    Widget addCheckbox(String text, boolean checked, CheckboxListener listener);

    void addRow();
}
