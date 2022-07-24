/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.ui.widgets

import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter

/**
 * @author Marcus Brummer
 * @version 18-02-2016
 */
class FloatFieldWithLabel : TextFieldWithLabel {

    constructor(labelText: String, width: Int, allowNegative: Boolean) : super(labelText, width) {
        textField.textFieldFilter = FloatDigitsOnlyFilter(allowNegative)
    }

    constructor(labelText: String, width: Int) : super(labelText, width) {
        textField.textFieldFilter = FloatDigitsOnlyFilter(true)
    }

    fun isValid(): Boolean {
        // A failsafe catchall to see if its valid
        try {
            java.lang.Float.parseFloat(textField.text)
            textField.isInputValid = true
            return true
        } catch (ex: NumberFormatException) {
            textField.isInputValid = false
            return false
        }
    }

    val float: Float
        get() {
            if (isValid()) {
                return java.lang.Float.parseFloat(textField.text)
            }
            return 0f
        }

}
