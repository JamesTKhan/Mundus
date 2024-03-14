package com.mbrlabs.mundus.pluginapi.ui;

public interface RootWidget {

    Widget addRadioButtons(String button1Text, String button2Text, RadioButtonListener listener);

    Widget addRadioButtons(String button1Text, String button2Text, boolean selectedFirst, RadioButtonListener listener);

    Widget addSpinner(String text, int min, int max, int initValue, IntSpinnerListener listener);

    Widget addSpinner(String text, int min, int max, int initValue, int step, IntSpinnerListener listener);

    Widget addSpinner(String text, float min, float max, float initValue, FloatSpinnerListener listener);

    Widget addSpinner(String text, float min, float max, float initValue, float step, FloatSpinnerListener listener);

    Widget addCheckbox(String text, CheckboxListener listener);

    Widget addCheckbox(String text, boolean checked, CheckboxListener listener);

    void addRow();
}
