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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.kotcrab.vis.ui.widget.file.StreamingFileChooserListener;
import com.mbrlabs.mundus.editor.ui.UI;
import com.mbrlabs.mundus.editor.utils.ImageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
public class ImageChooserField extends VisTable {

    private static final Drawable PLACEHOLDER_IMG = new TextureRegionDrawable(
            new TextureRegion(new Texture(Gdx.files.internal("ui/img_placeholder.png"))));

    private final int width;
    private final VisTextButton fcBtn;
    private final boolean multiSelectEnabled;
    private boolean requireSquareImage = false; // Require image to be perfect square
    private boolean requirePowerOfTwo = false;  // Require image width/height to be power of 2
    private final ImageChosenListener listener;

    private Array<FileHandle> selectedFiles = null;

    private final Image img;
    private Texture texture;
    private FileHandle fileHandle;

    public ImageChooserField(int width, boolean multiSelect, ImageChosenListener listener) {
        super();
        this.width = width;
        fcBtn = new VisTextButton("Select");
        img = new Image(PLACEHOLDER_IMG);
        this.listener = listener;

        multiSelectEnabled = multiSelect;
        if (multiSelectEnabled) {
            selectedFiles = new Array<>();
        }

        setupUI();
        setupListeners();
    }

    public FileHandle getFile() {
        return this.fileHandle;
    }

    public void removeImage() {
        img.setDrawable(PLACEHOLDER_IMG);
        this.fileHandle = null;
    }

    public void setButtonText(String text) {
        fcBtn.setText(text);
    }

    public void setImage(FileHandle fileHandle) {
        if (texture != null) {
            texture.dispose();
        }

        this.fileHandle = fileHandle;

        if (fileHandle != null) {
            texture = new Texture(fileHandle);
            img.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        } else {
            img.setDrawable(PLACEHOLDER_IMG);
        }
    }

    private void setupUI() {
        pad(5);
        add(img).width(width).height(width).expandX().fillX().row();
        add(fcBtn).width(width).padTop(5).expandX();
    }

    private void setupListeners() {
        fcBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                FileChooser fileChooser = UI.INSTANCE.getFileChooser();
                fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
                fileChooser.setMultiSelectionEnabled(multiSelectEnabled);

                if (multiSelectEnabled) {
                    fileChooser.setListener(new StreamingFileChooserListener() {
                        @Override
                        public void begin() {
                            // Clear the files array before we start
                            selectedFiles.clear();
                        }

                        public void selected(FileHandle file) {
                            // Called for each selected file
                            selectedFiles.add(file);
                        }

                        @Override
                        public void end() {
                            if (selectedFiles.size > 1) {
                                handleMultipleSelect(selectedFiles);
                            } else {
                                handleSingleSelect(selectedFiles.get(0));
                            }
                        }
                    });
                } else {
                    fileChooser.setListener(new SingleFileChooserListener() {
                        public void selected(FileHandle file) {
                            handleSingleSelect(file);
                        }
                    });
                }

                UI.INSTANCE.addActor(fileChooser.fadeIn());
            }
        });
    }

    /**
     * Multi-Selection calls the listener onImagesChosen with the valid image files
     * and a hashmap containing the files that failed validation paired with the error message.
     *
     * If the listener is not set for multi-select then nothing will happen. The listener implementation
     * is expected to handle the success and failedFiles from the calling end as needed (like showing error messages).
     *
     * @param selectedFiles Array of multiple selected files
     */
    private void handleMultipleSelect(Array<FileHandle> selectedFiles) {
        if (listener == null) {
            return;
        }

        // <File Name, Error Message>
        HashMap<FileHandle, String> failedFiles = new HashMap<>();

        // Put files that fail validation into failedFiles hashmap with an error message
        for (FileHandle file : selectedFiles) {
            String errorMessage = validateImageFile(file);
            if (errorMessage != null) {
                failedFiles.put(file, errorMessage);
            }
        }

        // Remove failed files from file array
        for(Map.Entry<FileHandle, String> entry : failedFiles.entrySet()) {
            selectedFiles.removeValue(entry.getKey(), true);
        }

        listener.onImagesChosen(selectedFiles, failedFiles);
    }

    /**
     * Validates and either sets the image or displays an error if the image fails validation
     *
     * @param fileHandle the selected file
     */
    private void handleSingleSelect(FileHandle fileHandle) {
        String errorMessage = validateImageFile(fileHandle);
        if (errorMessage == null) {
            setImage(fileHandle);
            if (listener != null)
                listener.onImageChosen();
        } else {
            Dialogs.showErrorDialog(UI.INSTANCE, errorMessage);
        }
    }

    public String validateImageFile(FileHandle fileHandle) {
        return ImageUtils.INSTANCE.validateImageFile(fileHandle, requireSquareImage, requirePowerOfTwo);
    }

    public boolean isRequireSquareImage() {
        return requireSquareImage;
    }

    public void setRequireSquareImage(boolean requireSquareImage) {
        this.requireSquareImage = requireSquareImage;
    }

    public boolean isRequirePowerOfTwo() {
        return requirePowerOfTwo;
    }

    public void setRequirePowerOfTwo(boolean requirePowerOfTwo) {
        this.requirePowerOfTwo = requirePowerOfTwo;
    }

    public interface ImageChosenListener {
        /**
         * Called for single selection file choosers
         */
        void onImageChosen();

        /**
         * Called for multi-select image chooser
         * @param images array of images that passed image validations
         * @param failedFiles Hashmap of failed file (key) that did not pass image validation with error message (value)
         */
        void onImagesChosen(Array<FileHandle> images, HashMap<FileHandle, String> failedFiles);
    }

}
