package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.TextureData
import com.mbrlabs.mundus.commons.utils.MathUtils

/**
 * @author JamesTKhan
 * @version August 02, 2022
 */
object ImageUtils {

    fun validateImageFile(fileHandle: FileHandle): String? {
        return validateImageFile(fileHandle, requireSquare = false, requirePowerOfTwo = false)
    }

    fun validateImageFile(fileHandle: FileHandle, requireSquare: Boolean, requirePowerOfTwo: Boolean): String? {
        if (!fileHandle.exists()) {
            return "File does not exist or unable to import."
        }
        if (!isImage(fileHandle)) {
            return "Format not supported. Supported formats: png, jpg, jpeg, tga."
        }

        var data: TextureData? = null

        if (requireSquare) {
            data = TextureData.Factory.loadFromFile(fileHandle, false)
            if (data.width != data.height) {
                return "Image must be square."
            }
        }

        if (requirePowerOfTwo) {
            if (data == null) {
                data = TextureData.Factory.loadFromFile(fileHandle, false)
            }
            if (!MathUtils.isPowerOfTwo(data!!.width) || !MathUtils.isPowerOfTwo(data.height)) {
                return "Image dimensions must be a power of two."
            }
        }

        return null
    }
}