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

package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.utils.Colors;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class FaLabel extends VisLabel {

    public final static LabelStyle style = new LabelStyle();
    static {
        style.font = Mundus.INSTANCE.getFaSmall();
        style.fontColor = Colors.INSTANCE.getTEAL();
    }

    public final static LabelStyle styleActive = new LabelStyle();
    static {
        styleActive.font = Mundus.INSTANCE.getFaSmall();
        styleActive.fontColor = Color.WHITE;
    }

    public FaLabel(String text) {
        super(text);
        setStyle(style);
    }

}
