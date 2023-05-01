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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.utils.TextureProvider;
import com.mbrlabs.mundus.editor.utils.Colors;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TextureGrid<T extends TextureProvider> extends VisTable {

    private static final int HIGHLIGHT_LINE_WIDTH = 3;

    private final GridGroup grid;
    private OnTextureClickedListener listener;

    private final Image selectedOverlay;

    public TextureGrid(int imgSize, int spacing) {
        super();
        this.grid = new GridGroup(imgSize, spacing);
        add(grid).expand().fill().row();

        selectedOverlay = createSelectedOverlayImage(imgSize);
    }

    public TextureGrid(int imgSize, int spacing, Array<T> textures) {
        this(imgSize, spacing);
        setTextures(textures);
    }

    public void setListener(OnTextureClickedListener listener) {
        this.listener = listener;
    }

    public void setTextures(Array<T> textures) {
        grid.clearChildren();
        for (T tex : textures) {
            grid.addActor(new TextureItem<>(tex));
        }
    }

    public void addTexture(T texture) {
        grid.addActor(new TextureItem<>(texture));
    }

    public void removeTextures() {
        grid.clearChildren();
    }

    public void highlightFirst() {
        final TextureItem<T> first = (TextureItem<T>) grid.getChildren().first();
        first.highlight();
    }

    /**
     *
     */
    public interface OnTextureClickedListener {
        void onTextureSelected(TextureProvider textureProvider, boolean leftClick);
    }

    private Image createSelectedOverlayImage(final int imgSize) {
        final Pixmap pixmap = new Pixmap(imgSize, imgSize, Pixmap.Format.RGBA8888);
        pixmap.setColor(Colors.INSTANCE.getTEAL());

        // Fill top line
        pixmap.fillRectangle(0, 0, imgSize, HIGHLIGHT_LINE_WIDTH);

        //Fill left line
        pixmap.fillRectangle(0, 0, HIGHLIGHT_LINE_WIDTH, imgSize);

        // Fill right line
        pixmap.fillRectangle(imgSize - HIGHLIGHT_LINE_WIDTH, 0, HIGHLIGHT_LINE_WIDTH, imgSize);

        // Fill bottom line
        pixmap.fillRectangle(0, imgSize - HIGHLIGHT_LINE_WIDTH, imgSize, HIGHLIGHT_LINE_WIDTH);

        return new Image(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
    }

    /**
     *
     */
    private class TextureItem<T extends TextureProvider> extends VisTable {

        private final Stack stack;

        public TextureItem(final T tex) {
            super();
            stack = new Stack();
            stack.add(new VisImage(tex.getTexture()));
            add(stack);

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (listener == null) return;
                    highlight();
                    listener.onTextureSelected(tex, button == Input.Buttons.LEFT);
                }
            });

        }

        public void highlight() {
            stack.add(selectedOverlay);
        }

    }

}
