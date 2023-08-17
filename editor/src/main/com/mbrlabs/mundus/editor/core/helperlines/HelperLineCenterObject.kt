/*
 * Copyright (c) 2023. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.core.helperlines

import com.badlogic.gdx.math.Vector3

data class HelperLineCenterObject(
        var x: Int = 0,
        var y: Int = 0,
        var position: Vector3 = Vector3(),
        var full: Boolean = true
        ) {

        fun initialize(x: Int, y: Int, position: Vector3, full: Boolean): HelperLineCenterObject{
                this.x = x
                this.y = y
                this.position = position
                this.full = full

                return this
        }
}
